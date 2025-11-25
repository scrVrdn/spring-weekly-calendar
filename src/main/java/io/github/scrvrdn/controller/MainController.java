package io.github.scrvrdn.controller;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.springframework.stereotype.Component;

import io.github.scrvrdn.model.CalendarCreator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

@Component
public class MainController {
    private CalendarCreator calendarCreator;
    private Stage stage;

    @FXML private Button createButton;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    public MainController(CalendarCreator calendarCreator) {
        this.calendarCreator = calendarCreator;
    }

    
    public void initialize() {
        StringConverter<LocalDate> defaultConverter = fromDatePicker.getConverter();
        StringConverter<LocalDate> converter = new StringConverter<>() {
                @Override
            public String toString(LocalDate date) {
                return defaultConverter.toString(date);
            }

            @Override
            public LocalDate fromString(String text) {
                try {
                    return defaultConverter.fromString(text);
                } catch (Exception e) {
                    System.err.println("Invalid date format: " + e.getMessage());
                    return null;
                }
            }
        };

        fromDatePicker.setConverter(converter);
        toDatePicker.setConverter(converter);

        Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        Chronology chrono = fromDatePicker.getChronology();
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, chrono, locale);

        fromDatePicker.setPromptText(pattern.toLowerCase());
        toDatePicker.setPromptText(pattern.toLowerCase());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCreateButton() throws Exception {
        if (!validateDates()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("weekly_calendar.pdf");

        ExtensionFilter extensionFilterPDF = new ExtensionFilter("PDF - Portable Document Format", "*.pdf");
        ExtensionFilter extensionFilterAll = new ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extensionFilterPDF, extensionFilterAll);

        fileChooser.setInitialDirectory(new File("C:\\"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {            
            if (file.exists()) {
                file = getUniqueFilename(file);
            }

            // create the calendar
            calendarCreator.create(getFromDate(), getToDate(), file.getAbsolutePath());
           
            // open the containing folder
            if (!GraphicsEnvironment.isHeadless()
                && Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file.getParentFile());
            }
        }
    }

    @FXML
    private LocalDate getFromDate() {
        LocalDate date = fromDatePicker.getValue();
        return date;
    }

    @FXML
    private LocalDate getToDate() {
        LocalDate date = toDatePicker.getValue();
        return date;
    }

    private File getUniqueFilename(File file) {
        String original = file.getName();
        String name = original;
        String extension = ".pdf";
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex > 0) {
            name = original.substring(0, dotIndex);
        }

        File newFile;
        int count = 1;
        do {
            String newName = String.format("%s(%d)%s", name, count++, extension);
            newFile = new File(file.getParent(), newName);
        } while (newFile.exists());
         
        return newFile;
    }

    private boolean validateDates() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Wrong date format!");
            alert.showAndWait();
            return false;
        } else if (toDate.isBefore(fromDate)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("The end date cannot be earlier than the start date!");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
