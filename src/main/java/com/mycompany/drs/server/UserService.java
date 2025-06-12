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
import com.mycompany.drs.shared.User;
import java.util.ArrayList;
import java.util.List;

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
    
    
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, role, is_active FROM users"; // NEVER send password to client
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, role, is_active) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // In a real app, hash this first
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isActive());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUser(User user) {
        // Note: This simple version doesn't handle password updates.
        // A real app would have a separate "change password" feature.
        String sql = "UPDATE users SET username = ?, role = ?, is_active = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getRole());
            pstmt.setBoolean(3, user.isActive());
            pstmt.setInt(4, user.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteUser(int userId) {
        // A "soft delete" by deactivating is often safer than a hard DELETE.
        String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}