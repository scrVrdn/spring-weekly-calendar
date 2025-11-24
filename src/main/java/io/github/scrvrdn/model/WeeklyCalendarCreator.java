package io.github.scrvrdn.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

@Component
public class WeeklyCalendarCreator implements CalendarCreator {
    private PagePrinter pagePrinter;

    WeeklyCalendarCreator(PagePrinter pagePrinter) {
        this.pagePrinter = pagePrinter;
    }

    public void create(LocalDate from, LocalDate to, String filePath) {
        if (from == null || to == null || filePath == null || filePath.trim().isEmpty()) throw new IllegalArgumentException();
        
        from = adjustFrom(from);
        to = adjustTo(to);        
        try {
            PDDocument document = new PDDocument();

            while (from != null && !from.isAfter(to)) {
                from = pagePrinter.printPage(document, from);
            }

            document.save(filePath);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDate adjustFrom(LocalDate from) {
        return from.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate adjustTo(LocalDate to) {
        return to.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
}
