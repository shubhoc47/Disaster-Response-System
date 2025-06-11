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

public class AssignedResource {
    private final SimpleIntegerProperty id; // Internal ID for the assignment
    private final SimpleStringProperty name; // Team/Unit Name
    private final SimpleStringProperty type; // e.g., Fire Engine, Ambulance, Police Unit
    private final SimpleStringProperty status; // e.g., Dispatched, En Route, On Scene, Standing By
    private final SimpleStringProperty contactInfo; // Optional contact

    private static int nextId = 1;

    public AssignedResource(String name, String type, String status, String contactInfo) {
        this.id = new SimpleIntegerProperty(nextId++);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
        this.contactInfo = new SimpleStringProperty(contactInfo);
    }

    // Getters for TableView
    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public String getName() { return name.get(); }
    public SimpleStringProperty nameProperty() { return name; }
    public String getType() { return type.get(); }
    public SimpleStringProperty typeProperty() { return type; }
    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
    public String getContactInfo() { return contactInfo.get(); }
    public SimpleStringProperty contactInfoProperty() { return contactInfo; }

    // Setters (if status needs to be updatable)
    public void setStatus(String status) { this.status.set(status); }
}
