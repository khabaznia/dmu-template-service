variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-1"
}

variable "s3_bucket_name" {
  description = "S3 bucket for Lambda deployment"
  type        = string
  default = "microservices-lambda-archives"
}

variable "lambda_s3_key" {
  description = "S3 key for Lambda deployment"
  type        = string
  default = "dmu-templates-service/dmu-templates-service-0.0.1-SNAPSHOT-all.jar"
}
