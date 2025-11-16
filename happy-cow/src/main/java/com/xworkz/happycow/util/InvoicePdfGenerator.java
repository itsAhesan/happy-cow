package com.xworkz.happycow.util;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.OrderItemDTO;
import com.xworkz.happycow.dto.PaymentViewDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class InvoicePdfGenerator {

    private InvoicePdfGenerator() {}

    public static byte[] generate(PaymentViewDTO p, AgentDTO agent) throws IOException {
        return buildInvoicePdfFromDto(p, agent);
    }

    private static byte[] buildInvoicePdfFromDto(PaymentViewDTO p, AgentDTO agent) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // Load fonts
            PDType0Font font;
            PDType0Font fontBold;
            try (InputStream r0 = InvoicePdfGenerator.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf");
                 InputStream r1 = InvoicePdfGenerator.class.getResourceAsStream("/fonts/NotoSans-Bold.ttf")) {
                if (r0 == null || r1 == null) {
                    throw new IOException("Required fonts not found under /fonts/ in classpath.");
                }
                font = PDType0Font.load(doc, r0, true);
                fontBold = PDType0Font.load(doc, r1, true);
            }

            // --- Layout constants ---
            final float M = 50f;                     // margins
            final float PAGE_W = page.getMediaBox().getWidth();
            final float PAGE_H = page.getMediaBox().getHeight();
            final float CONTENT_W = PAGE_W - 2 * M;
            final float PADDING = 6f;
            final boolean USE_TOTAL_BOX = true;      // set to false to remove the box and use a summary row

            java.util.function.BiFunction<PDPage, Boolean, PDPageContentStream> newStream =
                    (pg, append) -> {
                        try {
                            return new PDPageContentStream(
                                    doc, pg,
                                    append ? PDPageContentStream.AppendMode.APPEND
                                            : PDPageContentStream.AppendMode.OVERWRITE,
                                    true, true);
                        } catch (IOException ex) { throw new RuntimeException(ex); }
                    };

            PDPage curPage = page;
            PDPageContentStream cs = newStream.apply(curPage, false);

            float topY = PAGE_H - M;

            // ---- Logo ----
            boolean logoDrawn = false;
            float logoW = 72f, logoH = 72f;
            InputStream logoIs = InvoicePdfGenerator.class.getResourceAsStream("/images/happy-cow-logo.png");
            if (logoIs == null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) logoIs = cl.getResourceAsStream("images/happy-cow-logo.png");
            }
            if (logoIs != null) {
                try (InputStream ls = logoIs; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[4096]; int r;
                    while ((r = ls.read(buf)) != -1) baos.write(buf, 0, r);
                    PDImageXObject logoImg = PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "logo");
                    cs.drawImage(logoImg, M, topY - logoH, logoW, logoH);
                    logoDrawn = true;
                } catch (Exception ignored) { /* continue without logo */ }
            }

            // ---- Company block ----
            float companyX = M + (logoDrawn ? logoW + 12f : 0f);
            float curY = topY - 6f;

            cs.beginText(); cs.setFont(fontBold, 18f); cs.newLineAtOffset(companyX, curY);
            cs.showText("HappyCow"); cs.endText();

            curY -= 18f;
            cs.beginText(); cs.setFont(font, 10f); cs.newLineAtOffset(companyX, curY);
            cs.showText("123 Milk Street, Bengaluru, India - 560001"); cs.endText();

            curY -= 12f;
            cs.beginText(); cs.setFont(font, 9f); cs.newLineAtOffset(companyX, curY);
            cs.showText("GSTIN: 29ABCDE1234F1Z5 | Phone: +91-9876543210"); cs.endText();

            // ---- Title ----
            String title = "INVOICE";
            float titleSize = 22f;
            float titleW = stringWidth(fontBold, titleSize, title);
            float titleX = M + (CONTENT_W - titleW) / 2f;
            float titleY = topY - Math.max(logoDrawn ? logoH : 0f, 40f) - 26f;
            cs.beginText(); cs.setFont(fontBold, titleSize); cs.newLineAtOffset(titleX, titleY);
            cs.showText(title); cs.endText();

            // ---- Right meta ----
            float rightX = M + CONTENT_W;
            float metaY = topY - 6f;
            drawRightAlignedText(cs, font, 9f, "Invoice Ref: " + (p.getReferenceNo() == null ? "" : p.getReferenceNo()), rightX, metaY);
            metaY -= 12f;
            drawRightAlignedText(cs, font, 9f, "Payment ID: " + (p.getPaymentId() != null ? p.getPaymentId().toString() : ""), rightX, metaY);
            metaY -= 12f;
            drawRightAlignedText(cs, font, 9f, "Settled at: " + (p.getSettledAt() != null ? p.getSettledAt().toString() : "—"), rightX, metaY);

            // ---- Separator ----
            float sepY = titleY - 20f;
            cs.setStrokingColor(200, 200, 200); cs.setLineWidth(0.9f);
            cs.moveTo(M, sepY); cs.lineTo(M + CONTENT_W, sepY); cs.stroke();

            // ---- Billed to ----
            float billedY = sepY - 18f;
            cs.beginText(); cs.setFont(fontBold, 11f); cs.newLineAtOffset(M, billedY);
            cs.showText("Billed to:"); cs.endText();

            billedY -= 14f;
            String agentName = ((agent.getFirstName() == null ? "" : agent.getFirstName())
                    + (agent.getLastName() == null ? "" : " " + agent.getLastName())).trim();
            cs.beginText(); cs.setFont(font, 10f); cs.newLineAtOffset(M, billedY);
            cs.showText(agentName); cs.endText();

            billedY -= 12f;
            cs.beginText(); cs.setFont(font, 9f); cs.newLineAtOffset(M, billedY);
            cs.showText(agent.getEmail() != null ? agent.getEmail() : ""); cs.endText();

            billedY -= 18f;
            cs.beginText(); cs.setFont(font, 9f); cs.newLineAtOffset(M, billedY);
            String windowLbl = "Payment Window: " +
                    (p.getWindowStartDate() != null ? p.getWindowStartDate().toString() : "") +
                    " \u2192 " + (p.getWindowEndDate() != null ? p.getWindowEndDate().toString() : "");
            cs.showText(windowLbl); cs.endText();

            // ===================== TABLE =====================
            float tableTop = billedY - 28f;
            float tableX = M;
            float tableW = CONTENT_W;
            float rowHeight = 20f;
            float y = tableTop;

            // Percent-based columns that ALWAYS fit
            final float DATE_PCT = 0.16f;
            final float PROD_PCT = 0.46f;
            final float QTY_PCT  = 0.08f;
            final float RATE_PCT = 0.15f;
            float colDateW    = tableW * DATE_PCT;
            float colProductW = tableW * PROD_PCT;
            float colQtyW     = tableW * QTY_PCT;
            float colRateW    = tableW * RATE_PCT;
            float colTotalW   = tableW - (colDateW + colProductW + colQtyW + colRateW); // remainder

            float xDate    = tableX;
            float xProduct = xDate + colDateW;
            float xQty     = xProduct + colProductW;
            float xRate    = xQty + colQtyW;
            float xTotal   = xRate + colRateW;

            // Header background
            cs.setNonStrokingColor(240, 240, 240);
            cs.addRect(tableX, y - rowHeight, tableW, rowHeight); cs.fill();
            cs.setNonStrokingColor(0, 0, 0);

            float headerTextY = y - rowHeight + 6f;
            cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xDate + PADDING, headerTextY); cs.showText("Date"); cs.endText();
            cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xProduct + PADDING, headerTextY); cs.showText("Product"); cs.endText();
            String hQty = "Qty"; float hQtyW = stringWidth(fontBold, 9f, hQty);
            String hRate = "Rate"; float hRateW = stringWidth(fontBold, 9f, hRate);
            String hTotal = "Line Total"; float hTotalW = stringWidth(fontBold, 9f, hTotal);
            cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xQty + colQtyW - PADDING - hQtyW, headerTextY); cs.showText(hQty); cs.endText();
            cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xRate + colRateW - PADDING - hRateW, headerTextY); cs.showText(hRate); cs.endText();
            cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xTotal + colTotalW - PADDING - hTotalW, headerTextY); cs.showText(hTotal); cs.endText();

            float headerBottomY = y - rowHeight;
            y -= rowHeight + 6f;

            // Data rows
            List<OrderItemDTO> rows = p.getOrders() == null ? Collections.emptyList() : p.getOrders();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"));

            for (OrderItemDTO row : rows) {
                // pagination (reserve space for total area & footer)
                if (y < M + 160f) {
                    cs.close();
                    curPage = new PDPage(PDRectangle.A4);
                    doc.addPage(curPage);
                    cs = newStream.apply(curPage, false);
                    y = PAGE_H - M - 40f;

                    // redraw header
                    cs.setNonStrokingColor(240, 240, 240);
                    cs.addRect(tableX, y - rowHeight, tableW, rowHeight); cs.fill();
                    cs.setNonStrokingColor(0, 0, 0);
                    float hdrY = y - rowHeight + 6f;
                    cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xDate + PADDING, hdrY); cs.showText("Date"); cs.endText();
                    cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xProduct + PADDING, hdrY); cs.showText("Product"); cs.endText();
                    cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xQty + colQtyW - PADDING - hQtyW, hdrY); cs.showText(hQty); cs.endText();
                    cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xRate + colRateW - PADDING - hRateW, hdrY); cs.showText(hRate); cs.endText();
                    cs.beginText(); cs.setFont(fontBold, 9f); cs.newLineAtOffset(xTotal + colTotalW - PADDING - hTotalW, hdrY); cs.showText(hTotal); cs.endText();
                    headerBottomY = y - rowHeight;
                    y -= rowHeight + 6f;
                }

                String date      = row.getCollectedAt() != null ? row.getCollectedAt().toString() : "";
                String product   = row.getProductName() == null ? "" : row.getProductName();
                String qty       = row.getQuantity() == null ? "0" : String.valueOf(row.getQuantity());
                String rate      = row.getUnitPrice() == null ? nf.format(0.0) : nf.format(row.getUnitPrice());
                String lineTotal = row.getLineTotal() == null ? nf.format(0.0) : nf.format(row.getLineTotal());
                if (rate.contains("₹")) rate = rate.replace("₹", "Rs.");
                if (lineTotal.contains("₹")) lineTotal = lineTotal.replace("₹", "Rs.");

                // Date
                cs.beginText(); cs.setFont(font, 9f); cs.newLineAtOffset(xDate + PADDING, y); cs.showText(date); cs.endText();

                // Product (wrap if needed)
                float maxProductWidth = colProductW - (2 * PADDING);
                java.util.List<String> prodLines = wrapText(font, 9f, maxProductWidth, product);
                float prodY = y;
                for (String line : prodLines) {
                    cs.beginText(); cs.setFont(font, 9f); cs.newLineAtOffset(xProduct + PADDING, prodY);
                    cs.showText(line); cs.endText();
                    prodY -= 11f;
                }

                // Qty (right)
                float qtyW = stringWidth(font, 9f, qty);
                cs.beginText(); cs.setFont(font, 9f);
                cs.newLineAtOffset(xQty + colQtyW - PADDING - qtyW, y); cs.showText(qty); cs.endText();

                // Rate (right)
                float rateW = stringWidth(font, 9f, rate);
                cs.beginText(); cs.setFont(font, 9f);
                cs.newLineAtOffset(xRate + colRateW - PADDING - rateW, y); cs.showText(rate); cs.endText();

                // Line Total (right)
                float ltW = stringWidth(font, 9f, lineTotal);
                cs.beginText(); cs.setFont(font, 9f);
                cs.newLineAtOffset(xTotal + colTotalW - PADDING - ltW, y); cs.showText(lineTotal); cs.endText();

                int wrappedLines = Math.max(1, prodLines.size());
                y -= (wrappedLines * 11f) + 6f;
            }

            // Optional subtle column dividers
            float tableBottomY = y + 4f;
            cs.setStrokingColor(230, 230, 230); cs.setLineWidth(0.6f);
            cs.moveTo(xDate, headerBottomY);   cs.lineTo(xDate, tableBottomY);   cs.stroke();
            cs.moveTo(xProduct, headerBottomY);cs.lineTo(xProduct, tableBottomY);cs.stroke();
            cs.moveTo(xQty, headerBottomY);    cs.lineTo(xQty, tableBottomY);    cs.stroke();
            cs.moveTo(xRate, headerBottomY);   cs.lineTo(xRate, tableBottomY);   cs.stroke();
            cs.moveTo(xTotal, headerBottomY);  cs.lineTo(xTotal, tableBottomY);  cs.stroke();
            cs.moveTo(xTotal + colTotalW, headerBottomY); cs.lineTo(xTotal + colTotalW, tableBottomY); cs.stroke();
            cs.setStrokingColor(0, 0, 0); cs.setLineWidth(0.9f);

            // ===================== GRAND TOTAL =====================
            BigDecimal gross = p.getGrossAmount() == null ? new BigDecimal("0.00") : p.getGrossAmount();
            NumberFormat nf2 = NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"));
            String totalTextRaw = nf2.format(gross);
            if (totalTextRaw.contains("₹")) totalTextRaw = totalTextRaw.replace("₹", "Rs.");

            if (!USE_TOTAL_BOX) {
                // ---- Classic summary row (no box) ----
                float topLineY = y - 8f;
                cs.setStrokingColor(60, 60, 60); cs.setLineWidth(1.0f);
                cs.moveTo(tableX, topLineY); cs.lineTo(tableX + tableW, topLineY); cs.stroke();

                // Label on left spanning Date+Product+Qty+Rate area
                String lbl = "GRAND TOTAL";
                cs.beginText(); cs.setFont(fontBold, 11f);
                cs.newLineAtOffset(xDate + PADDING, topLineY - 14f); cs.showText(lbl); cs.endText();

                // Amount in the last column (right-aligned)
                float amtSize = 14f;
                float amtW = stringWidth(fontBold, amtSize, totalTextRaw);
                cs.beginText(); cs.setFont(fontBold, amtSize);
                cs.newLineAtOffset(xTotal + colTotalW - PADDING - amtW, topLineY - 14f);
                cs.showText(totalTextRaw); cs.endText();

                y = topLineY - 32f;
            } else {
                // ---- Boxed summary (fixed + auto-fit) ----
                if (y < M + 180f) {
                    cs.close();
                    curPage = new PDPage(PDRectangle.A4);
                    doc.addPage(curPage);
                    cs = newStream.apply(curPage, false);
                    y = PAGE_H - M - 40f;
                }

                float boxWidth  = Math.min(360f, tableW * 0.72f);
                float boxHeight = 60f;
                float boxX = tableX + tableW - boxWidth;
                float boxY = y - boxHeight - 18f;

                // border
                cs.setLineWidth(1.6f); cs.setStrokingColor(30, 30, 30);
                cs.addRect(boxX, boxY, boxWidth, boxHeight); cs.stroke();

                float insidePad = 12f;
                float innerX = boxX + insidePad;
                float innerY = boxY + boxHeight - insidePad - 10f; // lower baseline so text never hits top border

                // left label
                cs.beginText(); cs.setFont(fontBold, 13f);
                cs.newLineAtOffset(innerX, innerY); cs.showText("GRAND TOTAL"); cs.endText();

                // right amount with auto-fit font (never spills outside)
                float amountAreaX = boxX + boxWidth * 0.55f;                    // right area starts at ~55%
                float amountAreaW = boxX + boxWidth - insidePad - amountAreaX;  // width of right area
                float amtFontSize = 20f;
                float amtW = stringWidth(fontBold, amtFontSize, totalTextRaw);
                while (amtW > amountAreaW && amtFontSize > 12f) {
                    amtFontSize -= 1f;
                    amtW = stringWidth(fontBold, amtFontSize, totalTextRaw);
                }
                float amountX = amountAreaX + (amountAreaW - amtW); // right align within amount area
                cs.beginText(); cs.setFont(fontBold, amtFontSize);
                cs.newLineAtOffset(amountX, innerY); cs.showText(totalTextRaw); cs.endText();

                // caption
                cs.beginText(); cs.setFont(font, 9f);
                cs.newLineAtOffset(innerX, innerY - 18f);
                cs.showText("Amount payable to agent"); cs.endText();

                y = boxY - 36f;
            }

            // ---- Footer notes ----
            cs.setNonStrokingColor(110, 110, 110);
            cs.beginText(); cs.setFont(font, 8.5f); cs.newLineAtOffset(M, y);
            cs.showText("Payment will be processed to the bank account on record. This is a computer-generated invoice."); cs.endText();
            cs.beginText(); cs.setFont(font, 8.5f); cs.newLineAtOffset(M, y - 12f);
            cs.showText("If you have questions, contact payroll@happycow.example or call +91-9876543210."); cs.endText();

            cs.setNonStrokingColor(0, 0, 0);
            cs.close();

            doc.save(out);
            return out.toByteArray();
        }
    }


    // helper: string width
    private static float stringWidth(PDType0Font font, float fontSize, String text) throws IOException {
        if (text == null || text.isEmpty()) return 0f;
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    // draw right aligned single-line text
    private static void drawRightAlignedText(PDPageContentStream cs, PDType0Font font, float fontSize,
                                      String text, float rightX, float y) throws IOException {
        float w = stringWidth(font, fontSize, text);
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(rightX - w, y);
        cs.showText(text);
        cs.endText();
    }

    // greedy wrap
    private static List<String> wrapText(PDType0Font font, float fontSize, float maxWidth, String text) throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (text == null || text.trim().isEmpty()) return lines;
        String[] words = text.split("\\s+");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            String cand = cur.length() == 0 ? w : cur + " " + w;
            if (stringWidth(font, fontSize, cand) <= maxWidth) {
                if (cur.length() == 0) cur.append(w);
                else cur.append(" ").append(w);
            } else {
                if (cur.length() > 0) lines.add(cur.toString());
                cur = new StringBuilder(w);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }




}
