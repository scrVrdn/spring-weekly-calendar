package io.github.scrvrdn.model;

import java.time.LocalDate;

public interface CalendarCreator {
    void create(LocalDate from, LocalDate to, String filePath);
}
