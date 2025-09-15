#!/bin/bash

# Comprehensive script to setup S3 infrastructure and upload image
# This script will:
# 1. Initialize terraform (if needed)
# 2. Apply terraform configuration
# 3. Upload image.png to S3
# 4. Display the URL to view the image

set -e

echo "🚀 Setting up S3 infrastructure and uploading image..."
echo "=================================================="

# Change to terraform directory
cd "$(dirname "$0")/../terraform"

# Check if terraform is initialized
if [ ! -d ".terraform" ]; then
    echo "📦 Initializing Terraform..."
    terraform init
else
    echo "✅ Terraform already initialized"
fi

# Apply terraform configuration
echo "🔧 Applying Terraform configuration..."
terraform apply -auto-approve

# Get the bucket name
BUCKET_NAME=$(terraform output -raw s3_bucket_name)
echo "📦 S3 Bucket created: $BUCKET_NAME"

# Go back to scripts directory
cd ../scripts

# Upload the image
echo "📤 Uploading image.png to S3..."
./upload-image.sh

echo ""
echo "🎉 Setup complete! Your S3 bucket is ready and the image is uploaded."
echo ""
echo "🔗 IMAGE URL: http://localhost:4566/$BUCKET_NAME/image.png"
echo ""
echo "📋 Useful commands:"
echo "   # View bucket contents:"
echo "   awslocal s3 ls s3://$BUCKET_NAME/"
echo ""
echo "   # Download the image:"
echo "   awslocal s3 cp s3://$BUCKET_NAME/image.png ./downloaded-image.png"
echo ""
echo "   # Delete the image:"
echo "   awslocal s3 rm s3://$BUCKET_NAME/image.png"
echo ""
echo "   # Clean up infrastructure:"
echo "   cd ../terraform && terraform destroy -auto-approve"
