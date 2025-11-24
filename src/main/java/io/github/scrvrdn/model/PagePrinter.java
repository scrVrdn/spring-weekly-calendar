package io.github.scrvrdn.model;

import java.time.LocalDate;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface PagePrinter {
    LocalDate printPage(PDDocument document, LocalDate from);
}
