terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"

  # LocalStack configuration
  access_key = "test"
  secret_key = "test"

  # LocalStack endpoints
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    lambda = "http://localhost:4566"
    iam    = "http://localhost:4566"
    sqs    = "http://localhost:4566"
    s3     = "http://localhost:4566"
  }
}

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
