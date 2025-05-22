# dmu-template-service

## Deployment Overview

This project can be deployed to AWS Lambda using Terraform for infrastructure and GitHub Actions for CI/CD.

### Infrastructure (Terraform)
- See `infra/` directory for Terraform files to provision:
  - AWS Lambda function
  - IAM role and permissions
  - S3 bucket for deployment artifacts
  - API Gateway for HTTP access

### CI/CD (GitHub Actions)
- See `.github/workflows/deploy.yml` for the pipeline:
  - Build the JAR
  - Package and upload to S3
  - Deploy/Update Lambda via Terraform

---

## Quick Start

1. Configure AWS credentials/secrets in your GitHub repository.
2. Push to `main` branch to trigger deployment.
3. See workflow and Terraform output for API endpoint. 