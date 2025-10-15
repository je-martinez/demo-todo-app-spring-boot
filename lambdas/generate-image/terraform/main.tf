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
    lambda         = "http://localhost:4566"
    iam            = "http://localhost:4566"
    sqs            = "http://localhost:4566"
    s3             = "http://localhost:4566"
    ssm            = "http://localhost:4566"
    secretsmanager = "http://localhost:4566"
  }
}

