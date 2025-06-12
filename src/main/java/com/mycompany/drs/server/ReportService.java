package com.mycompany.drs.server;

import com.mycompany.drs.database.DatabaseConnector;
import com.mycompany.drs.shared.AssignedResource;
import com.mycompany.drs.shared.DisasterReport;
import com.mycompany.drs.shared.LogEntry;
import com.mycompany.drs.shared.SituationReport;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    // No changes needed in this method
    public List<DisasterReport> getAllReports() {
        List<DisasterReport> reports = new ArrayList<>();
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

    // --- MAJOR CORRECTIONS START HERE ---

    public DisasterReport getReportById(int reportId) {
        String reportSql = "SELECT * FROM disaster_reports WHERE report_id = ?";
        DisasterReport report = null;
        
        // Use a single connection for all parts of this operation
        try (Connection conn = DatabaseConnector.getConnection()) {
            // 1. Fetch the main report
            try (PreparedStatement pstmt = conn.prepareStatement(reportSql)) {
                pstmt.setInt(1, reportId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        report = mapResultSetToDisasterReport(rs);
                    }
                }
            }
            
            // If the report was found, fetch its children using the same connection
            if (report != null) {
                // 2. Fetch and attach the assigned resources
                report.setAssignedResources(getResourcesForReport(conn, reportId));
                
                // 3. Fetch and attach the communication logs
                report.setCommunicationLog(getLogsForReport(conn, reportId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
    
    // --- Fully implemented helper methods ---

    private List<AssignedResource> getResourcesForReport(Connection conn, int reportId) throws SQLException {
        List<AssignedResource> resources = new ArrayList<>();
        String sql = "SELECT * FROM assigned_resources WHERE report_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AssignedResource res = new AssignedResource();
                    res.setResourceId(rs.getInt("resource_id"));
                    res.setReportId(rs.getInt("report_id"));
                    res.setName(rs.getString("resource_name"));
                    res.setType(rs.getString("resource_type"));
                    res.setStatus(rs.getString("resource_status"));
                    res.setContactInfo(rs.getString("contact_info"));
                    resources.add(res);
                }
            }
        }
        return resources;
    }
    
    private List<LogEntry> getLogsForReport(Connection conn, int reportId) throws SQLException {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM communication_logs WHERE report_id = ? ORDER BY timestamp ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LogEntry log = new LogEntry();
                    log.setLogId(rs.getInt("log_id"));
                    log.setReportId(rs.getInt("report_id"));
                    log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    log.setEntryText(rs.getString("entry_text"));
                    log.setAuthor(rs.getString("author"));
                    logs.add(log);
                }
            }
        }
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

    // --- THE REST OF YOUR WORKING METHODS (NO CHANGES NEEDED) ---

    public List<DisasterReport> getFilteredReports(String type, String priority, String status, String keyword) {
        List<DisasterReport> reports = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM disaster_reports WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (type != null && !type.equalsIgnoreCase("All") && !type.isEmpty()) { sql.append(" AND disaster_type = ?"); params.add(type); }
        if (priority != null && !priority.equalsIgnoreCase("All") && !priority.isEmpty()) { sql.append(" AND priority = ?"); params.add(priority); }
        if (status != null && !status.equalsIgnoreCase("All") && !status.isEmpty()) { sql.append(" AND status = ?"); params.add(status); }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (report_id = ? OR location_summary LIKE ? OR disaster_type LIKE ?)");
            String keywordParam = "%" + keyword.trim() + "%";
            try { params.add(Integer.parseInt(keyword.trim())); } catch (NumberFormatException e) { params.add(-1); }
            params.add(keywordParam); params.add(keywordParam);
        }
        sql.append(" ORDER BY reported_time DESC");
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) { pstmt.setObject(i + 1, params.get(i)); }
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) { reports.add(mapResultSetToDisasterReport(rs)); } }
        } catch (SQLException e) { e.printStackTrace(); }
        return reports;
    }

    public DisasterReport addReport(DisasterReport report) {
        String sql = "INSERT INTO disaster_reports(disaster_type, location_summary, priority, status, reported_time) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, report.getType());
            pstmt.setString(2, report.getLocationSummary());
            pstmt.setString(3, report.getPriority());
            pstmt.setString(4, report.getStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(report.getReportedTime()));
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { if (generatedKeys.next()) { report.setId(generatedKeys.getInt(1)); } }
            }
        } catch (SQLException e) { e.printStackTrace(); return null; }
        return report;
    }
    
    public SituationReport getSituationReport() {
        String countByStatusSql = "SELECT status, COUNT(*) as count FROM disaster_reports WHERE status != 'Closed' GROUP BY status";
        String countByPrioritySql = "SELECT priority, COUNT(*) as count FROM disaster_reports WHERE status != 'Closed' GROUP BY priority";
        String highPrioritySql = "SELECT * FROM disaster_reports WHERE priority IN ('High', 'Critical') AND status != 'Closed' ORDER BY priority DESC, reported_time ASC";
        Map<String, Long> byStatus = new HashMap<>();
        Map<String, Long> byPriority = new HashMap<>();
        List<DisasterReport> highPriorityIncidents = new ArrayList<>();
        int totalActive = 0;
        try (Connection conn = DatabaseConnector.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(countByStatusSql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { long count = rs.getLong("count"); byStatus.put(rs.getString("status"), count); totalActive += count; }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(countByPrioritySql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { byPriority.put(rs.getString("priority"), rs.getLong("count")); }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(highPrioritySql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { highPriorityIncidents.add(mapResultSetToDisasterReport(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); return null; }
        return new SituationReport(totalActive, byStatus, byPriority, highPriorityIncidents);
    }
    
    public boolean addLogEntry(int reportId, LogEntry log) {
        String sql = "INSERT INTO communication_logs(report_id, timestamp, entry_text, author) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            pstmt.setTimestamp(2, Timestamp.valueOf(log.getTimestamp()));
            pstmt.setString(3, log.getEntryText());
            pstmt.setString(4, log.getAuthor());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateIncidentStatus(int reportId, String newStatus, String newPriority) {
        String sql = "UPDATE disaster_reports SET status = ?, priority = ? WHERE report_id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, newPriority);
            pstmt.setInt(3, reportId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                String logText = "Incident status updated to '" + newStatus + "' and priority to '" + newPriority + "'.";
                addLogEntry(reportId, new LogEntry(LocalDateTime.now(), logText, "System"));
            }
            return affectedRows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean assignResource(int reportId, AssignedResource resource) {
        String sql = "INSERT INTO assigned_resources(report_id, resource_name, resource_type, resource_status, contact_info) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            pstmt.setString(2, resource.getName());
            pstmt.setString(3, resource.getType());
            pstmt.setString(4, resource.getStatus());
            pstmt.setString(5, resource.getContactInfo());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                String logText = "Resource '" + resource.getName() + "' (" + resource.getType() + ") has been assigned to the incident.";
                addLogEntry(reportId, new LogEntry(LocalDateTime.now(), logText, "System"));
            }
            return affectedRows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean updateResourceStatus(int resourceId, int reportId, String newStatus) {
        String sql = "UPDATE assigned_resources SET resource_status = ? WHERE resource_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, resourceId);

            int affectedRows = pstmt.executeUpdate();

            // If the update was successful, create a log entry for it.
            // We no longer need to look up the reportId, it was passed in.
            if (affectedRows > 0) {
                // Note: This log won't have the resource name or old status, but it's simpler and won't deadlock.
                // A more advanced solution would use a transaction.
                String logText = "Status for a resource (ID: " + resourceId + ") was updated to '" + newStatus + "'.";
                addLogEntry(reportId, new LogEntry(LocalDateTime.now(), logText, "System"));
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}