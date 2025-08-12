#!/usr/bin/env bash
set -euo pipefail
ya
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
AWSTOOL="awslocal"

LAMBDA_NAME="sqs-ts-lambda"
QUEUE_NAME="demo-queue"
ROLE_NAME="lambda-exec-role"

echo "-> Build TypeScript"
npm run build

echo "-> Zip dist/"
npm run zip

echo "-> Start LocalStack if needed (docker compose)"
# docker compose up -d   # descomenta si quieres levantar aquí

echo "-> Create IAM role (LocalStack acepta cualquier trust policy)"
$AWSTOOL iam create-role \
  --role-name "$ROLE_NAME" \
  --assume-role-policy-document '{
    "Version":"2012-10-17",
    "Statement":[{"Effect":"Allow","Principal":{"Service":"lambda.amazonaws.com"},"Action":"sts:AssumeRole"}]
  }' >/dev/null || true

echo "-> Attach basic policies (opcionales en LocalStack)"
$AWSTOOL iam attach-role-policy \
  --role-name "$ROLE_NAME" \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole >/dev/null || true

echo "-> Create SQS queue"
QUEUE_URL=$($AWSTOOL sqs create-queue --queue-name "$QUEUE_NAME" --query 'QueueUrl' --output text)
echo "Queue URL: $QUEUE_URL"

echo "-> Create/Update Lambda"
# Intenta crear; si existe, actualiza el código
set +e
$AWSTOOL lambda create-function \
  --function-name "$LAMBDA_NAME" \
  --runtime nodejs20.x \
  --role "arn:aws:iam::000000000000:role/$ROLE_NAME" \
  --handler "handler.handler" \
  --zip-file "fileb://dist.zip" \
  --timeout 30 \
  --memory-size 256 >/dev/null
CREATE_RC=$?
set -e

if [ $CREATE_RC -ne 0 ]; then
  echo "-> Updating function code"
  $AWSTOOL lambda update-function-code \
    --function-name "$LAMBDA_NAME" \
    --zip-file "fileb://dist.zip" >/dev/null
fi

echo "-> Create Event Source Mapping (SQS -> Lambda)"
# Si ya existe, ignora error
set +e
$AWSTOOL lambda create-event-source-mapping \
  --function-name "$LAMBDA_NAME" \
  --event-source-arn "arn:aws:sqs:us-east-1:000000000000:$QUEUE_NAME" \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 5 \
  --function-response-types ReportBatchItemFailures >/dev/null
set -e

echo "-> Done. Send a test message:"
echo "$AWSTOOL sqs send-message --queue-url $QUEUE_URL --message-body '{\"userId\":\"123\",\"action\":\"welcome\"}'"
