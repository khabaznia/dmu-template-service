package com.khabaznia.dmu_templates_service

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder
import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler

class StreamLambdaHandler implements RequestStreamHandler {
    static def handler

    static {
        try {
            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
                    .defaultProxy()
                    .asyncInit()
                    .springBootApplication(DmuTemplatesServiceApplication.class)
                    .build()
        } catch (ContainerInitializationException e) {
            // If we fail here, we rethrow to force a cold start retry
            throw new RuntimeException("Could not initialize Spring Boot application", e)
        }
    }

    @Override
    void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        handler.proxyStream(inputStream, outputStream, context)
    }
} 