# Outputs
output "lambda_function_name" {
  description = "Name of the Lambda function"
  value       = aws_lambda_function.sqs_lambda.function_name
}

output "sqs_queue_url" {
  description = "URL of the SQS queue"
  value       = aws_sqs_queue.demo_queue.url
}

output "sqs_queue_arn" {
  description = "ARN of the SQS queue"
  value       = aws_sqs_queue.demo_queue.arn
}

output "lambda_function_arn" {
  description = "ARN of the Lambda function"
  value       = aws_lambda_function.sqs_lambda.arn
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket"
  value       = aws_s3_bucket.image_bucket.bucket
}

output "s3_bucket_url" {
  description = "URL of the S3 bucket"
  value       = "http://localhost:4566/${aws_s3_bucket.image_bucket.bucket}"
}

output "s3_bucket_arn" {
  description = "ARN of the S3 bucket"
  value       = aws_s3_bucket.image_bucket.arn
}

output "aws_sqs_send_message" {
  description = "Example SQS message"
  value       = <<EOT
awslocal sqs send-message --queue-url ${aws_sqs_queue.demo_queue.url} --message-body '{"userId":"123","action":"welcome"}'
EOT
}

output "aws_s3_upload_example" {
  description = "Example S3 upload command"
  value       = <<EOT
awslocal s3 cp ../assets/image.png s3://${aws_s3_bucket.image_bucket.bucket}/image.png
EOT
}

output "aws_s3_view_example" {
  description = "Example S3 view command"
  value       = <<EOT
# View in browser: http://localhost:4566/${aws_s3_bucket.image_bucket.bucket}/image.png
# Or use: awslocal s3 ls s3://${aws_s3_bucket.image_bucket.bucket}/
EOT
}
