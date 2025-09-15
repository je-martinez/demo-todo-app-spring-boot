#!/usr/bin/env bash
set -euo pipefail

# Set environment variables for LocalStack
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

# Change to the terraform directory
cd "$(dirname "$0")/../terraform"

echo "-> Build TypeScript"
cd .. && yarn run build

echo "-> Zip dist/"
yarn run zip

echo "-> Change back to terraform directory"
cd terraform

echo "-> Initialize Terraform"
terraform init

echo "-> Plan Terraform deployment"
terraform plan

echo "-> Apply Terraform configuration"
terraform apply -auto-approve

echo "-> Get outputs"
terraform output

echo "-> Done! Your Lambda function and SQS queue are now deployed."
echo "-> To test, you can send a message to the SQS queue using the URL from the output above."