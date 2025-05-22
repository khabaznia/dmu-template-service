terraform {
  backend "s3" {
    bucket  = "dmu-templates-service-terraform-backend"
    key     = "terraform/state/terraform.tfstate"
    region  = "eu-west-1"
    encrypt = true
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_iam_role" "lambda_role" {
  name = "dmu-template-service-lambda-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

resource "aws_apigatewayv2_api" "dmu_template_service_api" {
  name          = "dmu-template-service-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.dmu_template_service_api.id
  name        = "$default"
  auto_deploy = true
}

