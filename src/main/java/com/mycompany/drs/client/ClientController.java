/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.client;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.shared.DisasterReport;
import com.mycompany.drs.shared.LogEntry;
import com.mycompany.drs.shared.AssignedResource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    // Singleton pattern: ensures only one instance of the connection manager exists.
    private static ClientController instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // --- User Session State ---
    private String currentUserRole;
    private String currentUsername;

    private ClientController() {
        try {
            // Establish connection to the server.
            // In a real app, this address and port would be in a config file.
            this.socket = new Socket("localhost", 8888); // Use the port from your DRSServer
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("CLIENT: Connected to the DRS Server.");
        } catch (IOException e) {
            System.err.println("CLIENT: Could not connect to the server.");
            e.printStackTrace();
            // In a real app, you would show an error dialog to the user.
        }
    }

    /**
     * Gets the single instance of the ClientController.
     * @return The singleton instance.
     */
    public static synchronized ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    // --- User Session Methods ---

    public boolean login(String username, String password) {
        try {
            // A request is an array: { "COMMAND", "param1", "param2", ... }
            String[] request = {"LOGIN", username, password};
            out.writeObject(request);
            out.flush();

            // The server sends back a String array: { "true/false", "ROLE" }
            String[] response = (String[]) in.readObject();
            boolean success = Boolean.parseBoolean(response[0]);

            if (success) {
                this.currentUsername = username;
                this.currentUserRole = response[1];
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        this.currentUsername = null;
        this.currentUserRole = null;
        // Optionally, send a "LOGOUT" message to the server if it needs to track sessions.
    }

    public boolean isLoggedIn() { return this.currentUsername != null; }
    public String getLoggedInUsername() { return this.currentUsername; }
    public String getCurrentUserRole() { return this.currentUserRole; }

    // --- Data Request Methods ---

    @SuppressWarnings("unchecked") // Suppress warning for the cast which we know is correct
    public List<DisasterReport> getAllReports() {
        try {
            String[] request = {"GET_ALL_REPORTS"};
            out.writeObject(request);
            out.flush();
            // The server sends back a List<DisasterReport>
            return (List<DisasterReport>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list on error
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<DisasterReport> getFilteredReports(String type, String priority, String status, String keyword) {
        try {
            // Create a request object containing all filter parameters
            Object[] request = {"GET_FILTERED_REPORTS", type, priority, status, keyword};
            out.writeObject(request);
            out.flush();
            return (List<DisasterReport>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
    }

    public DisasterReport getReportById(int reportId) {
        try {
            // A request can also be a mix of types in an Object array
            Object[] request = {"GET_REPORT_BY_ID", reportId};
            out.writeObject(request);
            out.flush();
            return (DisasterReport) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null on error
        }
    }
    
    public DisasterReport submitNewReport(DisasterReport newReport) {
        try {
            Object[] request = {"SUBMIT_REPORT", newReport};
            out.writeObject(request);
            out.flush();
            return (DisasterReport) in.readObject(); // Server returns the report with its new ID
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // You would add more methods here to handle every other action:
    // - updateReportStatus(int reportId, String newStatus)
    // - addLogEntry(int reportId, LogEntry newLog)
    // - assignResource(int reportId, AssignedResource newResource)
    // etc.
}