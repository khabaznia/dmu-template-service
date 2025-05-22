package com.khabaznia.dmu_templates_service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class LambdaResponse {
    public int statusCode;
    public Map<String, String> headers;
    public String body;
    public boolean isBase64Encoded;

    public LambdaResponse(byte[] fileBytes) {
        this.statusCode = 200;
        this.headers = new HashMap<>();
        headers.put("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        headers.put("Content-Disposition", "attachment; filename=\"filled.docx\"");
        this.body = Base64.getEncoder().encodeToString(fileBytes);
        this.isBase64Encoded = true;
    }
}

