#!/bin/bash

# Script to upload image.png to S3 bucket and display the URL
# This script should be run after terraform apply

set -e

echo "🚀 Uploading image.png to S3 bucket..."

# Get the bucket name from terraform output
BUCKET_NAME=$(cd ../terraform && terraform output -raw s3_bucket_name)

if [ -z "$BUCKET_NAME" ]; then
    echo "❌ Error: Could not get bucket name from terraform output"
    echo "Make sure you have run 'terraform apply' first"
    exit 1
fi

echo "📦 Bucket name: $BUCKET_NAME"

# Check if image.png exists
if [ ! -f "../assets/image.png" ]; then
    echo "❌ Error: ../assets/image.png not found"
    exit 1
fi

# Upload the image to S3
echo "📤 Uploading image.png to s3://$BUCKET_NAME/..."
awslocal s3 cp ../assets/image.png "s3://$BUCKET_NAME/image.png"

if [ $? -eq 0 ]; then
    echo "✅ Successfully uploaded image.png to S3!"
    
    # Display the URL to view the image
    echo ""
    echo "🌐 You can now view the image in your browser at:"
    echo "   🔗 http://localhost:4566/$BUCKET_NAME/image.png"
    echo ""
    echo "📋 Useful commands:"
    echo "   # List files in bucket:"
    echo "   awslocal s3 ls s3://$BUCKET_NAME/"
    echo ""
    echo "   # Download the image:"
    echo "   awslocal s3 cp s3://$BUCKET_NAME/image.png ./downloaded-image.png"
else
    echo "❌ Failed to upload image to S3"
    exit 1
fi
