/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.server;

/**
 *
 * @author shubh
 */


import com.mycompany.drs.shared.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.mycompany.drs.shared.LogEntry;

import com.mycompany.drs.shared.DisasterReport;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UserService userService;
    private final ReportService reportService;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.userService = new UserService();
        this.reportService = new ReportService();
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            Object requestObject;
            while ((requestObject = in.readObject()) != null) {

                // --- First, check for simple String[] array requests ---
                if (requestObject instanceof String[] stringRequest) {
                    String command = stringRequest[0];
                    System.out.println("SERVER: Received String[] command: " + command);

                    switch (command) {
                        case "LOGIN":
                            out.writeObject(userService.authenticate(stringRequest[1], stringRequest[2]));
                            break;
                        
                        case "GET_ALL_REPORTS":
                            out.writeObject(reportService.getAllReports());
                            break;

                        case "GET_ALL_USERS":
                            out.writeObject(userService.getAllUsers());
                            break;

                        case "GET_SITREP":
                            out.writeObject(reportService.getSituationReport());
                            break;

                        default:
                            System.err.println("SERVER: Unknown String[] command received: " + command);
                            break;
                    }
                }
                // --- Second, check for more complex Object[] array requests ---
                else if (requestObject instanceof Object[] objectRequest) {
                    String command = (String) objectRequest[0];
                    System.out.println("SERVER: Received Object[] command: " + command);

                    switch (command) {
                        case "GET_REPORT_BY_ID":
                            out.writeObject(reportService.getReportById((Integer) objectRequest[1]));
                            break;

                        case "SUBMIT_REPORT":
                            out.writeObject(reportService.addReport((DisasterReport) objectRequest[1]));
                            break;

                        case "ADD_LOG_ENTRY":
                            out.writeObject(reportService.addLogEntry((Integer) objectRequest[1], (LogEntry) objectRequest[2]));
                            break;

                        case "UPDATE_INCIDENT_STATUS":
                            out.writeObject(reportService.updateIncidentStatus((Integer) objectRequest[1], (String) objectRequest[2], (String) objectRequest[3]));
                            break;

                        case "ASSIGN_RESOURCE":
                            out.writeObject(reportService.assignResource((Integer) objectRequest[1], (AssignedResource) objectRequest[2]));
                            break;
                            
                        case "UPDATE_RESOURCE_STATUS":
                            out.writeObject(reportService.updateResourceStatus((Integer) objectRequest[1], (Integer) objectRequest[2], (String) objectRequest[3]));
                            break;
                            
                        case "GET_FILTERED_REPORTS":
                            out.writeObject(reportService.getFilteredReports((String) objectRequest[1], (String) objectRequest[2], (String) objectRequest[3], (String) objectRequest[4]));
                            break;

                        // User Management Commands
                        case "ADD_USER":
                            out.writeObject(userService.addUser((User) objectRequest[1]));
                            break;
                            
                        case "UPDATE_USER":
                            out.writeObject(userService.updateUser((User) objectRequest[1]));
                            break;
                            
                        case "DELETE_USER":
                            out.writeObject(userService.deleteUser((Integer) objectRequest[1]));
                            break;

                        default:
                            System.err.println("SERVER: Unknown Object[] command received: " + command);
                            break;
                    }
                }
                // --- Final catch-all for any other unexpected data type ---
                else {
                    System.err.println("SERVER: Received object of unknown type from client: " + requestObject.getClass().getName());
                }

                // Flush the stream once after handling the command
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("SERVER: Client " + clientSocket.getInetAddress() + " disconnected or an error occurred.");
            e.printStackTrace();
        }
    }
}