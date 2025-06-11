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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AssignedResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private int resourceId;
    private int reportId; // Foreign key back to the report
    private String name;
    private String type;
    private String status;
    private String contactInfo;
    
    // The database now generates the ID, so the static counter is removed.

    // Constructor, Getters, and Setters
    public AssignedResource() {}

    public AssignedResource(String name, String type, String status, String contactInfo) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.contactInfo = contactInfo;
    }

    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}