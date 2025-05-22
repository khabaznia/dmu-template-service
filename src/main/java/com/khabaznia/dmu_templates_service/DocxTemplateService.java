package com.khabaznia.dmu_templates_service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class DocxTemplateService {

    public byte[] fillTemplate(MultipartFile templateFile, Map<String, String> params) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(templateFile.getInputStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            fillParagraphs(docx, params);
            fillTables(docx, params);

            docx.write(out);
            return out.toByteArray();
        }
    }

    private static void fillParagraphs(XWPFDocument docx, Map<String, String> params) {
        for (XWPFParagraph para : docx.getParagraphs()) {
            StringBuilder fullText = new StringBuilder();

            // Concatenate all run texts
            for (XWPFRun run : para.getRuns()) {
                fullText.append(run.getText(0) != null ? run.getText(0) : "");
            }

            // Replace placeholders
            String updatedText = fullText.toString();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                updatedText = updatedText.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            // Remove old runs
            int numRuns = para.getRuns().size();
            for (int i = numRuns - 1; i >= 0; i--) {
                para.removeRun(i);
            }

            // Add new run with replaced text
            XWPFRun newRun = para.createRun();
            newRun.setText(updatedText);
        }
    }

    private static void fillTables(XWPFDocument docx, Map<String, String> params) {
        for (XWPFTable table : docx.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para : cell.getParagraphs()) {
                        for (XWPFRun run : para.getRuns()) {
                            String text = run.getText(0);
                            if (text != null) {
                                for (Map.Entry<String, String> entry : params.entrySet()) {
                                    String placeholder = "{{" + entry.getKey() + "}}";
                                    if (text.contains(placeholder)) {
                                        run.setText(text.replace(placeholder, entry.getValue()), 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
