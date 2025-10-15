# Variables
variable "lambda_name" {
  description = "Name of the Lambda function"
  type        = string
  default     = "sqs-ts-lambda"
}

variable "queue_name" {
  description = "Name of the SQS queue"
  type        = string
  default     = "demo-queue"
}

variable "role_name" {
  description = "Name of the IAM role"
  type        = string
  default     = "lambda-exec-role"
}

variable "lambda_zip_path" {
  description = "Path to the Lambda function ZIP file"
  type        = string
  default     = "../dist.zip"
}

variable "s3_bucket_name" {
  description = "Name of the S3 bucket"
  type        = string
  default     = "demo-image-bucket"
}

variable "lambda_environment" {
  description = "Environment variables for the Lambda function"
  type = object({
    NODE_ENV       = string
    LOG_LEVEL      = string
    APP_ENV        = string
    S3_BUCKET_NAME = string
    S3_REGION      = string
    S3_BASE_URL    = string
  })
}

variable "ssm_parameter_prefix" {
  description = "Prefix for SSM parameters"
  type        = string
  default     = "/listify/generate-image"
}

variable "secrets_manager_secret_name" {
  description = "Name of the Secrets Manager secret for application secrets"
  type        = string
  default     = "listify/generate-image"
  sensitive   = true
}

variable "mongodb_uri" {
  description = "MongoDB URI"
  type        = string
  default     = "mongodb://mongodb:27017/ListifyDatabase"
}

variable "database_name" {
  description = "Database name"
  type        = string
  default     = "ListifyDatabase"
}

variable "google_api_key" {
  description = "Google API key"
  type        = string
  default     = "YOUR_GOOGLE_API_KEY"
}

variable "aws_access_key_id" {
  description = "AWS access key ID"
  type        = string
  default     = "test"
}

variable "aws_secret_access_key" {
  description = "AWS secret access key"
  type        = string
  default     = "test"
}

variable "aws_endpoint_url" {
  description = "AWS endpoint URL"
  type        = string
  default     = "http://localhost:4566"
}
