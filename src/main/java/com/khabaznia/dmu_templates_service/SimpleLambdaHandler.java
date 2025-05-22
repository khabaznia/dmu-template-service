package com.khabaznia.dmu_templates_service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;
import java.io.ByteArrayInputStream;

public class SimpleLambdaHandler implements RequestHandler<Map<String, Object>, LambdaResponse> {

    @Override
    public LambdaResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            String rawBody = (String) input.get("body");
            Map body = new ObjectMapper().readValue(rawBody, Map.class);
            String templateBase64 = (String) body.get("template");
            Map<String, String> params = (Map<String, String>) body.get("params");

            if (templateBase64 == null) {
                throw new IllegalArgumentException("Missing 'template' in input");
            }

            byte[] templateBytes = Base64.getDecoder().decode(templateBase64);

            DocxTemplateService service = new DocxTemplateService();
            byte[] filledDocx = service.fillTemplate(new ByteArrayInputStream(templateBytes), params);

            return new LambdaResponse(filledDocx);
        } catch (Exception e) {
            throw new RuntimeException("Error processing Lambda request", e);
        }
    }
}
