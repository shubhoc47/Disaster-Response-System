/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.shared;
import java.io.Serializable;
/**
 *
 * @author shubh
 */
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int logId;
    private int reportId; // Foreign key back to the report
    private LocalDateTime timestamp;
    private String entryText;
    private String author;
    
    public LogEntry() {}

    public LogEntry(LocalDateTime timestamp, String entryText, String author) {
        this.timestamp = timestamp;
        this.entryText = entryText;
        this.author = author;
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEntryText() { return entryText; }
    public void setEntryText(String entryText) { this.entryText = entryText; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}