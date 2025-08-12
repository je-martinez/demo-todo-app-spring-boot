#!/usr/bin/env bash
set -euo pipefail

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
AWSTOOL="awslocal"

LAMBDA_NAME="sqs-ts-lambda"
QUEUE_NAME="demo-queue"
ROLE_NAME="lambda-exec-role"

# Variables de entorno para la Lambda
LAMBDA_ENV_VARS="Variables={\
NODE_ENV=production,\
LOG_LEVEL=info,\
APP_ENV=dev,\
FEATURE_FLAG_X=true,\
API_BASE_URL=https://api.example.test\
}"

echo "-> Build TypeScript"
yarn run build

echo "-> Zip dist/"
yarn run zip

echo "-> Create IAM role (LocalStack acepta cualquier trust policy)"
$AWSTOOL iam create-role \
  --role-name "$ROLE_NAME" \
  --assume-role-policy-document '{
    "Version":"2012-10-17",
    "Statement":[{"Effect":"Allow","Principal":{"Service":"lambda.amazonaws.com"},"Action":"sts:AssumeRole"}]
  }' >/dev/null || true

echo "-> Attach basic policies"
$AWSTOOL iam attach-role-policy \
  --role-name "$ROLE_NAME" \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole >/dev/null || true

echo "-> Create SQS queue"
QUEUE_URL=$($AWSTOOL sqs create-queue --queue-name "$QUEUE_NAME" --query 'QueueUrl' --output text)
echo "Queue URL: $QUEUE_URL"

echo "-> Create/Update Lambda"
set +e
$AWSTOOL lambda create-function \
  --function-name "$LAMBDA_NAME" \
  --runtime nodejs20.x \
  --role "arn:aws:iam::000000000000:role/$ROLE_NAME" \
  --handler "handler.handler" \
  --zip-file "fileb://dist.zip" \
  --timeout 30 \
  --memory-size 256 \
  --environment "$LAMBDA_ENV_VARS" >/dev/null
CREATE_RC=$?
set -e

if [ $CREATE_RC -ne 0 ]; then
  echo "-> Updating function code"
  $AWSTOOL lambda update-function-code \
    --function-name "$LAMBDA_NAME" \
    --zip-file "fileb://dist.zip" >/dev/null

  echo "-> Updating function environment variables"
  $AWSTOOL lambda update-function-configuration \
    --function-name "$LAMBDA_NAME" \
    --environment "$LAMBDA_ENV_VARS" >/dev/null
fi

echo "-> Create Event Source Mapping (SQS -> Lambda)"
set +e

# Check if event source mapping already exists and delete it
echo "-> Checking for existing event source mapping..."
EXISTING_MAPPING=$($AWSTOOL lambda list-event-source-mappings \
  --function-name "$LAMBDA_NAME" \
  --event-source-arn "arn:aws:sqs:us-east-1:000000000000:$QUEUE_NAME" \
  --query 'EventSourceMappings[0].UUID' \
  --output text 2>/dev/null || echo "")

if [ "$EXISTING_MAPPING" != "None" ] && [ "$EXISTING_MAPPING" != "" ]; then
  echo "-> Deleting existing event source mapping: $EXISTING_MAPPING"
  $AWSTOOL lambda delete-event-source-mapping --uuid "$EXISTING_MAPPING" >/dev/null 2>&1 || true
  # Wait a moment for deletion to complete
  sleep 2
fi

echo "-> Creating new event source mapping..."
$AWSTOOL lambda create-event-source-mapping \
  --function-name "$LAMBDA_NAME" \
  --event-source-arn "arn:aws:sqs:us-east-1:000000000000:$QUEUE_NAME" \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 5 \
  --function-response-types ReportBatchItemFailures >/dev/null

if [ $? -eq 0 ]; then
  echo "-> Event source mapping created successfully"
else
  echo "-> Failed to create event source mapping"
fi

set -e

echo "-> Done. Send a test message:"
echo "$AWSTOOL sqs send-message --queue-url $QUEUE_URL --message-body '{\"userId\":\"123\",\"action\":\"welcome\"}'"
