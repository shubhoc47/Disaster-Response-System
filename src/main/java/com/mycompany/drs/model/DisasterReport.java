/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.model;

/**
 *
 * @author shubh
 */

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DisasterReport {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty type; // e.g., Fire, Earthquake
    private final SimpleStringProperty locationSummary; // e.g., "123 Main St, City"
    private final SimpleStringProperty priority; // e.g., High, Medium, Low
    private final SimpleStringProperty status; // e.g., Reported, Assessing, Responding
    private final SimpleStringProperty reportedTime; // Store as String for TableView simplicity here
    private final ObservableList<AssignedResource> assignedResources;
    private final ObservableList<LogEntry> communicationLog;

    private static int nextId = 1; // Simple ID generator

    public DisasterReport(String type, String locationSummary, String priority, String status, LocalDateTime reportedTime) {
        this.id = new SimpleIntegerProperty(nextId++);
        this.type = new SimpleStringProperty(type);
        this.locationSummary = new SimpleStringProperty(locationSummary);
        this.priority = new SimpleStringProperty(priority);
        this.status = new SimpleStringProperty(status);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.reportedTime = new SimpleStringProperty(reportedTime.format(formatter));
        this.assignedResources = FXCollections.observableArrayList();
        this.communicationLog = FXCollections.observableArrayList();
        
        this.communicationLog.add(new LogEntry(reportedTime, "Incident Reported.", "System"));
    }

    // Getters for TableView (JavaFX Properties)
    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getType() { return type.get(); }
    public SimpleStringProperty typeProperty() { return type; }

    public String getLocationSummary() { return locationSummary.get(); }
    public SimpleStringProperty locationSummaryProperty() { return locationSummary; }

    public String getPriority() { return priority.get(); }
    public SimpleStringProperty priorityProperty() { return priority; }

    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }

    public String getReportedTime() { return reportedTime.get(); }
    public SimpleStringProperty reportedTimeProperty() { return reportedTime; }

    // Basic setters (if needed, though properties are final for this example)
    public void setType(String type) { this.type.set(type); }
    public void setLocationSummary(String locationSummary) { this.locationSummary.set(locationSummary); }
    public void setPriority(String priority) { this.priority.set(priority); }
    public void setStatus(String status) { this.status.set(status); }
    
    public ObservableList<AssignedResource> getAssignedResources() {
    return assignedResources;
    }

    public ObservableList<LogEntry> getCommunicationLog() {
        return communicationLog;
    }

    @Override
    public String toString() {
        return "DisasterReport{" +
               "id=" + id.get() +
               ", type='" + type.get() + '\'' +
               ", locationSummary='" + locationSummary.get() + '\'' +
               ", priority='" + priority.get() + '\'' +
               ", status='" + status.get() + '\'' +
               ", reportedTime='" + reportedTime.get() + '\'' +
               '}';
    }
}