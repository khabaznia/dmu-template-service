package com.khabaznia.dmu_templates_service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import groovy.json.JsonSlurper

@RestController
@RequestMapping('/api/docx')
class DocxTemplateController {

    @Autowired
    DocxTemplateService docxTemplateService

    @PostMapping(value = '/fill', consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    ResponseEntity<byte[]> fillDocxTemplate(
            @RequestPart('template') MultipartFile templateFile,
            @RequestPart('params') String paramsJson
    ) {
        def params = new JsonSlurper().parseText(paramsJson) as Map<String, String>

        byte [] out = docxTemplateService.fillTemplate(templateFile, params)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 'attachment; filename=filled.docx')
                .contentType(MediaType.parseMediaType('application/vnd.openxmlformats-officedocument.wordprocessingml.document'))
                .body(out)
    }

} 