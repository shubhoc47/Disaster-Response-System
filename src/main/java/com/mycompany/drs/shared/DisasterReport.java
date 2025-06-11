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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Must be Serializable to be sent over the network
public class DisasterReport implements Serializable {
    // serialVersionUID is good practice for Serializable classes
    private static final long serialVersionUID = 1L;

    private int id; // No longer a JavaFX property
    private String type;
    private String locationSummary;
    private String priority;
    private String status;
    private LocalDateTime reportedTime; // Use the actual object, not a formatted String
    private List<AssignedResource> assignedResources; // Use standard List
    private List<LogEntry> communicationLog;

    // Constructor for creating a new report before it has an ID from the DB
    public DisasterReport(String type, String locationSummary, String priority, String status, LocalDateTime reportedTime) {
        this.type = type;
        this.locationSummary = locationSummary;
        this.priority = priority;
        this.status = status;
        this.reportedTime = reportedTime;
        this.assignedResources = new ArrayList<>();
        this.communicationLog = new ArrayList<>();
    }

    // Default constructor for frameworks/libraries
    public DisasterReport() {
        this.assignedResources = new ArrayList<>();
        this.communicationLog = new ArrayList<>();
    }


    // --- Standard Getters and Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocationSummary() { return locationSummary; }
    public void setLocationSummary(String locationSummary) { this.locationSummary = locationSummary; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReportedTime() { return reportedTime; }
    public void setReportedTime(LocalDateTime reportedTime) { this.reportedTime = reportedTime; }
    
    public List<AssignedResource> getAssignedResources() { return assignedResources; }
    public void setAssignedResources(List<AssignedResource> assignedResources) { this.assignedResources = assignedResources; }
    
    public List<LogEntry> getCommunicationLog() { return communicationLog; }
    public void setCommunicationLog(List<LogEntry> communicationLog) { this.communicationLog = communicationLog; }

    @Override
    public String toString() {
        return "DisasterReport{" + "id=" + id + ", type='" + type + '\'' + '}';
    }
}