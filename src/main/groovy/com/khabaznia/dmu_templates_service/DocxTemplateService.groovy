package com.khabaznia.dmu_templates_service

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class DocxTemplateService {

    byte[] fillTemplate(MultipartFile templateFile, params) {
        XWPFDocument docx = new XWPFDocument(templateFile.inputStream)

        fillParagraphs(docx, params)

        fillTables(docx, params)

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        docx.write(out)
        docx.close()
        return out.toByteArray()
    }

    private static void fillParagraphs(XWPFDocument docx, params) {
        docx.paragraphs.each { para ->
            // Concatenate all run texts
            def fullText = para.runs.collect { it.getText(0) ?: "" }.join("")
            // Replace all placeholders
            params.each { key, value ->
                fullText = fullText.replace("{{${key}}}", value)
            }
            // Remove all runs
            int numRuns = para.runs.size()
            for (int i = numRuns - 1; i >= 0; i--) {
                para.removeRun(i)
            }
            // Add a new run with the replaced text
            def run = para.createRun()
            run.setText(fullText)
        }
    }

    private static List<XWPFTable> fillTables(XWPFDocument docx, params) {
        docx.tables.each { table ->
            table.rows.each { row ->
                row.tableCells.each { cell ->
                    cell.paragraphs.each { para ->
                        para.runs.each { run ->
                            params.each { key, value ->
                                def text = run.getText(0)
                                if (text && text.contains("{{${key}}}")) {
                                    run.setText(text.replace("{{${key}}}", value), 0)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
