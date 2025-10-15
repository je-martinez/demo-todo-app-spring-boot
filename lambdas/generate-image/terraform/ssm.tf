# SSM Parameters for environment configuration
resource "aws_ssm_parameter" "node_env" {
  name      = "${var.ssm_parameter_prefix}/node-env"
  type      = "String"
  value     = var.lambda_environment.NODE_ENV
  overwrite = true

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }
}

resource "aws_ssm_parameter" "log_level" {
  name      = "${var.ssm_parameter_prefix}/log-level"
  type      = "String"
  value     = var.lambda_environment.LOG_LEVEL
  overwrite = true

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }
}

resource "aws_ssm_parameter" "app_env" {
  name      = "${var.ssm_parameter_prefix}/app-env"
  type      = "String"
  value     = var.lambda_environment.APP_ENV
  overwrite = true

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }
}

resource "aws_ssm_parameter" "s3_bucket_name" {
  name      = "${var.ssm_parameter_prefix}/s3-bucket-name"
  type      = "String"
  value     = var.lambda_environment.S3_BUCKET_NAME
  overwrite = true

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }
}

resource "aws_ssm_parameter" "s3_region" {
  name      = "${var.ssm_parameter_prefix}/s3-region"
  type      = "String"
  value     = var.lambda_environment.S3_REGION
  overwrite = true

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }
}

resource "aws_ssm_parameter" "s3_base_url" {
  name      = "${var.ssm_parameter_prefix}/s3-base-url"
  type      = "String"
  value     = var.lambda_environment.S3_BASE_URL
  overwrite = true

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }
}

# AWS Secrets Manager secret for application secrets
resource "aws_secretsmanager_secret" "application_secrets" {
  name                    = var.secrets_manager_secret_name
  description             = "Application secrets for Listify generate-image service"
  recovery_window_in_days = 7

  tags = {
    Environment = var.lambda_environment.APP_ENV
    Service     = "generate-image"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_secretsmanager_secret_version" "application_secrets" {
  secret_id = aws_secretsmanager_secret.application_secrets.id
  secret_string = jsonencode({
    mongodb_uri    = var.mongodb_uri
    database_name  = var.database_name
    google_api_key = var.google_api_key
  })
}
