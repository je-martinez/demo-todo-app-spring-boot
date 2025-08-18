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

output "aws_sqs_send_message" {
  description = "Example SQS message"
  value       = "awslocal sqs send-message --queue-url ${aws_sqs_queue.demo_queue.url} --message-body '{\"userId\":\"123\",\"action\":\"welcome\"}'"
}
