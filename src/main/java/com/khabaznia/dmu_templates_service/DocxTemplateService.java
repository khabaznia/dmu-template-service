package com.khabaznia.dmu_templates_service;

import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class DocxTemplateService {

    public byte[] fillTemplate(java.io.InputStream templateStream, Map<String, String> params) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            fillParagraphs(docx, params);
            fillTables(docx, params);

            docx.write(out);
            return out.toByteArray();
        }
    }

    private static void fillParagraphs(XWPFDocument docx, Map<String, String> params) {
        for (XWPFParagraph para : docx.getParagraphs()) {
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
