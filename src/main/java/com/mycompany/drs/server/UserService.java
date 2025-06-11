/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.server;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.database.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// This service now lives on the SERVER and talks to the DATABASE.
public class UserService {
    
    // No more in-memory maps or currentlyLoggedInUser. The server is stateless.
    
    /**
     * Authenticates a user against the database.
     * @param username The username to check.
     * @param password The password to check.
     * @return A String array containing { "true", "ROLE" } on success, 
     *         or { "false", "null" } on failure.
     */
    public String[] authenticate(String username, String password) {
        String sql = "SELECT password, role FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // User was found
                    String dbPassword = rs.getString("password");
                    String dbRole = rs.getString("role");
                    if (dbPassword.equals(password)) {
                        System.out.println("SERVER: Authentication successful for " + username);
                        return new String[] { "true", dbRole }; // Success
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error on the server
        }
        
        System.out.println("SERVER: Authentication failed for " + username);
        return new String[] { "false", "null" }; // Failure
    }
    
    // Placeholder for new functionality (User Management for Admin)
    public boolean addUser(String username, String password, String role) {
        // TODO: Implement the SQL INSERT query to add a new user to the 'users' table.
        return false;
    }
}