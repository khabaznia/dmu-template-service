package com.khabaznia.dmu_templates_service;

import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
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
            List<XWPFRun> runs = para.getRuns();
            if (runs == null || runs.isEmpty()) continue;

            StringBuilder fullText = new StringBuilder();
            for (XWPFRun run : runs) {
                fullText.append(run.getText(0) != null ? run.getText(0) : "");
            }

            String combinedText = fullText.toString();
            boolean replaced = false;

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                if (combinedText.contains(placeholder)) {
                    combinedText = combinedText.replace(placeholder, entry.getValue());
                    replaced = true;
                }
            }

            if (replaced) {
                // Copy style info from the first run BEFORE modifying anything
                XWPFRun styleSource = runs.get(0);
                int fontSize = styleSource.getFontSize();
                String fontFamily = styleSource.getFontFamily();
                boolean isBold = styleSource.isBold();
                boolean isItalic = styleSource.isItalic();
                String color = styleSource.getColor();

                // Remove old runs
                int size = para.getRuns().size();
                for (int i = size - 1; i >= 0; i--) {
                    para.removeRun(i);
                }

                // Add new run with stored style
                XWPFRun newRun = para.createRun();
                newRun.setText(combinedText);
                newRun.setFontSize(14);
                newRun.setFontFamily("Times New Roman");
                newRun.setBold(isBold);
                newRun.setItalic(isItalic);
                newRun.setColor(color);
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
