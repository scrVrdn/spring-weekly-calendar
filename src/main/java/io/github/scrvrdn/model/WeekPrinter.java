package io.github.scrvrdn.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

@Component
public class WeekPrinter implements PagePrinter {
    private static final float X_OFFSET = 30;
    private static final float Y_OFFSET = PDRectangle.A4.getHeight() / 8;

    private static final int DAYS_FONT_SIZE = 10;
    private static final PDType1Font DAYS_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    
    private static final int MONTH_FONT_SIZE = 13;
    private static final PDType1Font MONTH_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final int NUMBER_OF_DAYS = 7;
    
    public LocalDate printPage(PDDocument document, LocalDate from) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);       

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            printLines(contentStream);
            LocalDate to = printDays(contentStream, from);
            printMonth(contentStream, from, to);
            printWeekNum(contentStream, from);

            contentStream.close();
            return to.plusDays(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private void printLines(PDPageContentStream contentStream) throws IOException {
        float x = PDRectangle.A4.getWidth();
        for (int i = 1; i <= NUMBER_OF_DAYS; i++) {
            float y = i * Y_OFFSET;
            contentStream.moveTo(0, y);
            contentStream.lineTo(x, y);
            contentStream.stroke();
        }
    }

    private LocalDate printDays(PDPageContentStream contentStream, LocalDate from) throws IOException {
        float offset = 28;
        float newLineOffset = DAYS_FONT_SIZE * 1.2f;
        contentStream.setLeading(newLineOffset);
        contentStream.beginText();
        contentStream.newLineAtOffset(X_OFFSET, PDRectangle.A4.getHeight() - offset);
        contentStream.setFont(DAYS_FONT, DAYS_FONT_SIZE);
        
        for (int d = 0; d < NUMBER_OF_DAYS; d++) {
            contentStream.newLineAtOffset(0, -Y_OFFSET + newLineOffset);
            contentStream.showText(from.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            contentStream.newLine();
            contentStream.showText(String.valueOf(from.getDayOfMonth()));
            from = from.plusDays(1);
        }

        contentStream.endText();
        return from.minusDays(1);
    }

    private void printMonth(PDPageContentStream contentStream, LocalDate from, LocalDate to) throws IOException {
        String month = from.getMonth().toString();
        
        contentStream.beginText();
        contentStream.setFont(MONTH_FONT, MONTH_FONT_SIZE);
        contentStream.newLineAtOffset(X_OFFSET, PDRectangle.A4.getHeight() - X_OFFSET);
        contentStream.showText(month);
        
        if (from.getMonth() != to.getMonth()) {
            contentStream.setLeading(MONTH_FONT_SIZE * 1.2f);
            contentStream.setNonStrokingColor(java.awt.Color.LIGHT_GRAY);
            contentStream.newLine();
            contentStream.showText(to.getMonth().toString());
            contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        }
        contentStream.endText();
    }

    private void printWeekNum(PDPageContentStream contentStream, LocalDate from) throws IOException {
        String week = "week " + from.get(WeekFields.ISO.weekOfYear());
        float textWidth = DAYS_FONT.getStringWidth(week) / 1000 * DAYS_FONT_SIZE;
        float startX = PDRectangle.A4.getWidth() - X_OFFSET - textWidth;

        contentStream.beginText();
        contentStream.setFont(DAYS_FONT, DAYS_FONT_SIZE);
        contentStream.newLineAtOffset(startX, PDRectangle.A4.getHeight() - X_OFFSET);
        contentStream.showText(week);
        contentStream.endText();
    }
}
