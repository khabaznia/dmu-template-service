package com.khabaznia.dmu_templates_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/docx")
class DocxTemplateController {

    @Autowired
    DocxTemplateService docxTemplateService;

    @PostMapping(value = "/fill", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> fillDocxTemplate(
            @RequestPart("template") MultipartFile templateFile,
            @RequestPart("params") String paramsJson
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> params = objectMapper.readValue(paramsJson, new TypeReference<Map<String, String>>() {});
        byte[] out = docxTemplateService.fillTemplate(templateFile, params);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled.docx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(out);
    }

}