/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.server;

/**
 *
 * @author shubh
 */

import com.mycompany.drs.shared.DisasterReport;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

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

                // --- LOGIN ---
                if (requestObject instanceof String[] request && "LOGIN".equals(request[0])) {
                    System.out.println("SERVER: Processing LOGIN request for user " + request[1]);
                    String[] response = userService.authenticate(request[1], request[2]);
                    out.writeObject(response);
                    out.flush();
                }

                // --- GET ALL REPORTS ---
                else if (requestObject instanceof String[] request && "GET_ALL_REPORTS".equals(request[0])) {
                    System.out.println("SERVER: Processing GET_ALL_REPORTS request.");
                    List<DisasterReport> reports = reportService.getAllReports();
                    System.out.println("SERVER: Found " + reports.size() + " reports. Sending to client.");
                    out.writeObject(reports);
                    out.flush();
                }

                // --- GET REPORT BY ID ---
                else if (requestObject instanceof Object[] request && "GET_REPORT_BY_ID".equals(request[0])) {
                    System.out.println("SERVER: Processing GET_REPORT_BY_ID request.");
                    int reportId = (Integer) request[1];
                    DisasterReport report = reportService.getReportById(reportId);
                    out.writeObject(report);
                    out.flush();
                }

                // --- SUBMIT NEW REPORT ---
                else if (requestObject instanceof Object[] request && "SUBMIT_REPORT".equals(request[0])) {
                    System.out.println("SERVER: Processing SUBMIT_REPORT request.");
                    DisasterReport newReport = (DisasterReport) request[1];
                    DisasterReport submittedReport = reportService.addReport(newReport);
                    out.writeObject(submittedReport);
                    out.flush();
                }
                else if (requestObject instanceof Object[] request && "GET_FILTERED_REPORTS".equals(request[0])) {
                    System.out.println("SERVER: Processing GET_FILTERED_REPORTS request.");
                    String type = (String) request[1];
                    String priority = (String) request[2];
                    String status = (String) request[3];
                    String keyword = (String) request[4];

                    List<DisasterReport> reports = reportService.getFilteredReports(type, priority, status, keyword);

                    System.out.println("SERVER: Found " + reports.size() + " filtered reports. Sending to client.");
                    out.writeObject(reports);
                    out.flush();
                }

                // --- UNKNOWN COMMAND ---
                else {
                    System.out.println("SERVER: Received unknown command.");
                }
            }
        } catch (Exception e) {
            System.out.println("SERVER: Client " + clientSocket.getInetAddress() + " disconnected or an error occurred.");
            e.printStackTrace();
        }
    }
}