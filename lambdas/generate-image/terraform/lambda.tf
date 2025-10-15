# Lambda Function
resource "aws_lambda_function" "sqs_lambda" {
  filename         = var.lambda_zip_path
  function_name    = var.lambda_name
  role             = aws_iam_role.lambda_exec_role.arn
  handler          = "handler.handler"
  runtime          = "nodejs20.x"
  timeout          = 60
  memory_size      = 512
  source_code_hash = filebase64sha256(var.lambda_zip_path)

  environment {
    variables = {
      NODE_ENV       = aws_ssm_parameter.node_env.value
      LOG_LEVEL      = aws_ssm_parameter.log_level.value
      APP_ENV        = aws_ssm_parameter.app_env.value
      S3_BUCKET_NAME = aws_ssm_parameter.s3_bucket_name.value
      S3_REGION      = aws_ssm_parameter.s3_region.value
      S3_BASE_URL    = aws_ssm_parameter.s3_base_url.value
      # Secrets will be retrieved at runtime from Secrets Manager
      SECRETS_MANAGER_SECRET_NAME = aws_secretsmanager_secret.application_secrets.name
      # AWS Configuration
      AWS_ACCESS_KEY_ID     = var.aws_access_key_id
      AWS_SECRET_ACCESS_KEY = var.aws_secret_access_key
      AWS_ENDPOINT_URL      = var.aws_endpoint_url
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
