/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.shared;

/**
 *
 * @author shubh
 */
import java.io.Serializable;

public class MasterResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private int masterId;
    private String unitName;
    private String unitType;
    private String homeStation;
    private String currentStatus;

    // Constructors, Getters, Setters...
    public MasterResource() {}

    // Getters and Setters for all fields...
    public int getMasterId() { return masterId; }
    public void setMasterId(int masterId) { this.masterId = masterId; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }
    public String getHomeStation() { return homeStation; }
    public void setHomeStation(String homeStation) { this.homeStation = homeStation; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
}