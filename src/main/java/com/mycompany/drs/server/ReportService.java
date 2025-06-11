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
import com.mycompany.drs.shared.DisasterReport;
import com.mycompany.drs.shared.AssignedResource;
import com.mycompany.drs.shared.LogEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// This service now lives on the SERVER and talks to the DATABASE.
public class ReportService {
    
    // No more in-memory list. All data comes from the DB.

    public List<DisasterReport> getAllReports() {
        List<DisasterReport> reports = new ArrayList<>();
        // Note: This simple query does not load associated resources or logs.
        // A more complex implementation might use JOINs or separate queries.
        String sql = "SELECT * FROM disaster_reports ORDER BY reported_time DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                reports.add(mapResultSetToDisasterReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public DisasterReport getReportById(int reportId) {
        String reportSql = "SELECT * FROM disaster_reports WHERE report_id = ?";
        DisasterReport report = null;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(reportSql)) {
            
            pstmt.setInt(1, reportId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    report = mapResultSetToDisasterReport(rs);
                    // Now, fetch associated resources and logs
                    report.setAssignedResources(getResourcesForReport(reportId));
                    report.setCommunicationLog(getLogsForReport(reportId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
    
    public List<DisasterReport> getFilteredReports(String type, String priority, String status, String keyword) {
        List<DisasterReport> reports = new ArrayList<>();

        // Start with a base query
        StringBuilder sql = new StringBuilder("SELECT * FROM disaster_reports WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Dynamically add conditions to the WHERE clause
        if (type != null && !type.equalsIgnoreCase("All") && !type.isEmpty()) {
            sql.append(" AND disaster_type = ?");
            params.add(type);
        }
        if (priority != null && !priority.equalsIgnoreCase("All") && !priority.isEmpty()) {
            sql.append(" AND priority = ?");
            params.add(priority);
        }
        if (status != null && !status.equalsIgnoreCase("All") && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search in ID, location, and type
            sql.append(" AND (report_id = ? OR location_summary LIKE ? OR disaster_type LIKE ?)");
            String keywordParam = "%" + keyword.trim() + "%";
            // Try to parse keyword as a number for ID search
            try {
                params.add(Integer.parseInt(keyword.trim()));
            } catch (NumberFormatException e) {
                params.add(-1); // Add a value that won't match any ID if it's not a number
            }
            params.add(keywordParam);
            params.add(keywordParam);
        }

        sql.append(" ORDER BY reported_time DESC");

        System.out.println("SERVER: Executing Filter Query: " + sql);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Set the parameters for the prepared statement
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToDisasterReport(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public DisasterReport addReport(DisasterReport report) {
        String sql = "INSERT INTO disaster_reports(disaster_type, location_summary, priority, status, reported_time) VALUES(?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, report.getType());
            pstmt.setString(2, report.getLocationSummary());
            pstmt.setString(3, report.getPriority());
            pstmt.setString(4, report.getStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(report.getReportedTime()));
            
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Get the auto-generated ID from the database
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        report.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Indicate failure
        }
        return report; // Return report with new ID
    }

    // You would continue to implement all the other methods (update, close, add log, etc.)
    // by writing the appropriate SQL queries (UPDATE, INSERT).

    // --- Helper Methods ---
    
    private List<AssignedResource> getResourcesForReport(int reportId) {
        List<AssignedResource> resources = new ArrayList<>();
        String sql = "SELECT * FROM assigned_resources WHERE report_id = ?";
        // ... implementation to query DB and populate list ...
        return resources;
    }
    
    private List<LogEntry> getLogsForReport(int reportId) {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM communication_logs WHERE report_id = ? ORDER BY timestamp ASC";
        // ... implementation to query DB and populate list ...
        return logs;
    }

    private DisasterReport mapResultSetToDisasterReport(ResultSet rs) throws SQLException {
        DisasterReport report = new DisasterReport();
        report.setId(rs.getInt("report_id"));
        report.setType(rs.getString("disaster_type"));
        report.setLocationSummary(rs.getString("location_summary"));
        report.setPriority(rs.getString("priority"));
        report.setStatus(rs.getString("status"));
        report.setReportedTime(rs.getTimestamp("reported_time").toLocalDateTime());
        return report;
    }
}