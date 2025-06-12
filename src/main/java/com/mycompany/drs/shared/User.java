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

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId;
    private String username;
    private String password; // Only sent from client to server, not the other way
    private String role;
    private boolean isActive;

    // Constructors, Getters, Setters...
    public User() {}
    
    public User(String username, String role, boolean isActive) {
        this.username = username;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters and Setters for all fields...
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}