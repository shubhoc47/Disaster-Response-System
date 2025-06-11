/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.model;

/**
 *
 * @author shubh
 */
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private final SimpleStringProperty timestamp;
    private final SimpleStringProperty entryText;
    private final SimpleStringProperty author; // Optional: who made the entry

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogEntry(LocalDateTime timestamp, String entryText, String author) {
        this.timestamp = new SimpleStringProperty(timestamp.format(FORMATTER));
        this.entryText = new SimpleStringProperty(entryText);
        this.author = new SimpleStringProperty(author);
    }

    public LogEntry(String entryText, String author) {
        this(LocalDateTime.now(), entryText, author);
    }


    // Getters for TableView
    public String getTimestamp() { return timestamp.get(); }
    public SimpleStringProperty timestampProperty() { return timestamp; }
    public String getEntryText() { return entryText.get(); }
    public SimpleStringProperty entryTextProperty() { return entryText; }
    public String getAuthor() { return author.get(); }
    public SimpleStringProperty authorProperty() { return author; }
}
