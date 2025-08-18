#!/usr/bin/env bash
set -euo pipefail

# Set environment variables for LocalStack
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

# Change to the terraform directory
cd "$(dirname "$0")/../terraform"

echo "-> Destroying Terraform-managed resources"
terraform destroy -auto-approve

echo "-> Done! All resources have been removed."
