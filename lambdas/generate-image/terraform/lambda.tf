# Lambda Function
resource "aws_lambda_function" "sqs_lambda" {
  filename      = var.lambda_zip_path
  function_name = var.lambda_name
  role          = aws_iam_role.lambda_exec_role.arn
  handler       = "handler.handler"
  runtime       = "nodejs20.x"
  timeout       = 30
  memory_size   = 256

  environment {
    variables = {
      NODE_ENV       = "production"
      LOG_LEVEL      = "info"
      APP_ENV        = "dev"
      FEATURE_FLAG_X = "true"
      API_BASE_URL   = "https://api.example.test"
      MONGODB_URI    = "mongodb://mongodb:27017/ListifyDatabase"
    }
  }

  depends_on = [aws_iam_role_policy_attachment.lambda_basic_execution]
}

# Event Source Mapping (SQS -> Lambda)
resource "aws_lambda_event_source_mapping" "sqs_lambda_mapping" {
  event_source_arn                   = aws_sqs_queue.demo_queue.arn
  function_name                      = aws_lambda_function.sqs_lambda.function_name
  batch_size                         = 10
  maximum_batching_window_in_seconds = 5
  function_response_types            = ["ReportBatchItemFailures"]
}
