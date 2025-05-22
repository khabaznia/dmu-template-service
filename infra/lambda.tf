resource "aws_iam_role_policy" "transaction_lambda_logging" {
  name   = "transaction_lambda_logging_policy"
  role   = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action   = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:PutLogEvents"]
        Effect   = "Allow"
        Resource = "arn:aws:logs:eu-west-1:*:log-group:/aws/lambda/transactionProcessor:*"
      }
    ]
  })
}

resource "aws_cloudwatch_log_group" "dmu_template_service_lambda_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.dmu_template_service.function_name}"
  retention_in_days = 7

  lifecycle {
    ignore_changes = [name]
  }
}

resource "aws_lambda_function" "dmu_template_service" {
  function_name = "dmu-template-service"
  s3_bucket     = var.s3_bucket_name
  s3_key        = var.lambda_s3_key
  handler       = "com.khabaznia.dmu_templates_service.JavaStreamLambdaHandler::handleRequest"
  runtime       = "java21"
  memory_size   = 1024
  timeout       = 29
  role          = aws_iam_role.lambda_role.arn
  environment {
    variables = {
      JAVA_TOOL_OPTIONS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    }
  }
  source_code_hash = filebase64sha256("../build/libs/dmu-templates-service-0.0.1-SNAPSHOT-all.jar")
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id           = aws_apigatewayv2_api.dmu_template_service_api.id
  integration_type = "AWS_PROXY"
  integration_uri  = aws_lambda_function.dmu_template_service.invoke_arn
  integration_method = "POST"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "default_route" {
  api_id    = aws_apigatewayv2_api.dmu_template_service_api.id
  route_key = "$default"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.dmu_template_service.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.dmu_template_service_api.execution_arn}/*/*"
}