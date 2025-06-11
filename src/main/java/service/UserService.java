/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author shubh
 */
import java.util.HashMap;
import java.util.Map;

public class UserService {

    // In-memory storage for users and their passwords (and roles for future use)
    // Key: username, Value: UserData (inner class holding password and role)
    private final Map<String, UserData> users;

    // To keep track of the currently logged-in user (simple approach for A2)
    private UserData currentlyLoggedInUser;

    // Inner class to store password and role
    private static class UserData {
        String password;
        String role; // e.g., "ADMIN", "DISPATCHER", "REPORTER"

        UserData(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }

    public UserService() {
        users = new HashMap<>();
        // Hardcoded users for Assessment 2
        users.put("admin", new UserData("admin", "admin"));
        users.put("dispatcher", new UserData("disp123", "DISPATCHER"));
        users.put("reporter1", new UserData("rep123", "REPORTER")); // Example of another role
        // Add more users as needed for testing
        currentlyLoggedInUser = null; // No one logged in initially
    }

    /**
     * Authenticates a user based on username and password.
     *
     * @param username The username to authenticate.
     * @param password The password provided by the user.
     * @return true if authentication is successful, false otherwise.
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        UserData userData = users.get(username.trim());

        if (userData != null && userData.password.equals(password)) {
            currentlyLoggedInUser = userData; // Store the logged-in user's data
            System.out.println("UserService: Authentication successful for user - " + username + ", Role: " + userData.role);
            return true;
        }

        currentlyLoggedInUser = null; // Clear if authentication fails
        System.out.println("UserService: Authentication failed for user - " + username);
        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentlyLoggedInUser != null) {
            System.out.println("UserService: User " + getLoggedInUsername() + " logged out.");
        }
        currentlyLoggedInUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return currentlyLoggedInUser != null;
    }

    /**
     * Gets the role of the currently logged-in user.
     *
     * @return The role string (e.g., "ADMIN", "DISPATCHER") or null if no user is logged in.
     */
    public String getCurrentUserRole() {
        if (currentlyLoggedInUser != null) {
            return currentlyLoggedInUser.role;
        }
        return null; // Or throw an exception, or return a default "GUEST" role
    }

    /**
     * Gets the username of the currently logged-in user.
     * This method needs to iterate the map to find the username since UserData doesn't store it.
     * A more efficient approach would be to store the username string in 'currentlyLoggedInUser'
     * or have 'currentlyLoggedInUser' be of type String holding the username.
     * For this example, we'll iterate or adjust.
     *
     * @return The username string or null if no user is logged in.
     */
    public String getLoggedInUsername() {
        if (currentlyLoggedInUser == null) {
            return null;
        }
        // Find the username associated with the currentlyLoggedInUser object
        for (Map.Entry<String, UserData> entry : users.entrySet()) {
            if (entry.getValue() == currentlyLoggedInUser) { // Comparing object references
                return entry.getKey();
            }
        }
        return null; // Should not happen if currentlyLoggedInUser was set correctly
    }


    

    /**
     * (Future) Example method to add a new user (would need password hashing in a real app).
     *
     * @param username The new username.
     * @param password The new password.
     * @param role The role for the new user.
     * @return true if user was added, false if username already exists.
     */
    public boolean addUser(String username, String password, String role) {
        if (users.containsKey(username)) {
            System.out.println("UserService: Username '" + username + "' already exists.");
            return false; // Username already exists
        }
        users.put(username, new UserData(password, role));
        System.out.println("UserService: User '" + username + "' added with role '" + role + "'.");
        return true;
    }

    
}