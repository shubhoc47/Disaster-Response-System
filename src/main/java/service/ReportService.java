/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.model.DisasterReport;
import com.mycompany.drs.model.AssignedResource;
import com.mycompany.drs.model.LogEntry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportService {

    private final ObservableList<DisasterReport> disasterReportsList;
    // We can also maintain separate lists for resources and logs if they are not solely tied to a report
    // but for this DRS, they are typically associated with a specific disaster report.

    public ReportService() {
        disasterReportsList = FXCollections.observableArrayList();
        // Initialize with some sample data for easier testing and demonstration
        loadSampleData();
    }

    // In ReportService.java

private void loadSampleData() {
        DisasterReport report1 = new DisasterReport("Fire", "North Suburbs, Sector 5, Main St", "High", "Responding", LocalDateTime.now().minusHours(3).minusMinutes(30));
        report1.getAssignedResources().add(new AssignedResource("Engine 1", "Fire Engine", "On Scene", "Fire Dept HQ"));
        report1.getAssignedResources().add(new AssignedResource("Ladder 2", "Ladder Truck", "On Scene", "Fire Dept HQ"));
        report1.getAssignedResources().add(new AssignedResource("Ambulance 3", "Ambulance", "En Route", "EMS Central"));
        report1.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(3).minusMinutes(15), "First units on scene. Structure fire confirmed.", "Engine 1 Lead"));
        report1.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(3).minusMinutes(25), "Units dispatched.", "Dispatcher"));
        disasterReportsList.add(report1);

        DisasterReport report2 = new DisasterReport("Flood", "Downtown Riverfront, Pier B & C", "Medium", "Assessing", LocalDateTime.now().minusHours(6));
        report2.getAssignedResources().add(new AssignedResource("Assessment Team Alpha", "Damage Assessment", "On Scene", "Emergency Mgmt."));
        report2.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(5).minusMinutes(45), "Assessment team dispatched to evaluate water levels.", "Dispatcher"));
        report2.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(5), "Water rising slowly. Some roads near pier becoming impassable.", "Team Alpha"));
        disasterReportsList.add(report2);

        DisasterReport report3 = new DisasterReport("Earthquake", "West Valley, Zone A, Near Mall", "Critical", "Responding", LocalDateTime.now().minusDays(1).plusHours(1));
        report3.getAssignedResources().add(new AssignedResource("Search & Rescue Alpha", "SAR Team", "On Scene", "SAR Command"));
        report3.getAssignedResources().add(new AssignedResource("Search & Rescue Bravo", "SAR Team", "Staging", "SAR Command"));
        report3.getAssignedResources().add(new AssignedResource("Police Unit 7", "Police Car", "On Scene - Cordon", "Police Dept"));
        report3.getAssignedResources().add(new AssignedResource("EMS Strike Team 1", "Ambulance Group", "On Scene - Triage", "EMS Command"));
        report3.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusDays(1).plusHours(0).minusMinutes(45), "Major structural damage reported at mall. Multiple casualties suspected.", "Dispatcher"));
        report3.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusDays(1).plusHours(0), "SAR Alpha on scene. Beginning primary search.", "SAR Alpha"));
        disasterReportsList.add(report3);

        DisasterReport report4 = new DisasterReport("Chemical Spill", "Industrial Park, Unit 12B, Chemcor Plant", "Critical", "Reported", LocalDateTime.now().minusHours(1).minusMinutes(15));
        report4.getAssignedResources().add(new AssignedResource("Hazmat Team 1", "Hazmat Unit", "Dispatched", "Fire Special Ops"));
        report4.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(1).minusMinutes(10), "Hazmat team alerted and en route. Evacuation of immediate area initiated.", "Dispatcher"));
        disasterReportsList.add(report4);

        // --- 8 New Samples ---
        DisasterReport report5 = new DisasterReport("Power Outage", "Greenwood Neighborhood, Elm St to Oak St", "Medium", "Responding", LocalDateTime.now().minusHours(4));
        report5.getAssignedResources().add(new AssignedResource("Utility Crew 4", "Power Line Repair", "On Scene", "City Electric"));
        report5.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(3).minusMinutes(30), "Crew 4 on site. Fallen tree identified as cause.", "Utility Dispatch"));
        disasterReportsList.add(report5);

        DisasterReport report6 = new DisasterReport("Wildfire", "Blue Mountain National Forest, North Ridge", "High", "Assessing", LocalDateTime.now().minusDays(2).plusHours(5));
        report6.getAssignedResources().add(new AssignedResource("Air Tanker 10", "Fixed-Wing Airtanker", "Standby", "Forest Service Air Ops"));
        report6.getAssignedResources().add(new AssignedResource("Ground Crew Delta", "Hand Crew", "En Route", "Forest Service"));
        report6.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusDays(2).plusHours(4), "Smoke reported. Aerial reconnaissance dispatched.", "Forestry Dispatch"));
        disasterReportsList.add(report6);

        DisasterReport report7 = new DisasterReport("Traffic Accident", "Highway 101, Mile Marker 45 Northbound", "Low", "Closed", LocalDateTime.now().minusHours(8));
        report7.getAssignedResources().add(new AssignedResource("Highway Patrol 21", "Patrol Car", "Cleared Scene", "State Police"));
        report7.getAssignedResources().add(new AssignedResource("Tow Truck Services", "Tow Truck", "Cleared Scene", "Private Contractor"));
        report7.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(7).minusMinutes(30), "Scene cleared. Roadway reopened.", "Highway Patrol"));
        report7.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(7).minusMinutes(50), "Vehicles removed from roadway.", "Tow Dispatch"));
        disasterReportsList.add(report7);

        DisasterReport report8 = new DisasterReport("Gas Leak", "1550 Commerce Ave, Downtown Business District", "High", "Responding", LocalDateTime.now().minusHours(1));
        report8.getAssignedResources().add(new AssignedResource("Fire Engine 3", "Fire Engine", "On Scene", "City Fire Dept"));
        report8.getAssignedResources().add(new AssignedResource("Gas Utility Emergency", "Utility Van", "On Scene", "City Gas Co."));
        report8.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusMinutes(45), "Strong smell of natural gas reported. Building being evacuated.", "Engine 3 Captain"));
        disasterReportsList.add(report8);

        DisasterReport report9 = new DisasterReport("Tornado Warning", "South County Region", "Critical", "Reported", LocalDateTime.now().minusMinutes(20));
        // No resources assigned yet, just the warning
        report9.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusMinutes(15), "Tornado warning issued by National Weather Service. Sirens activated.", "Emergency Ops Center"));
        disasterReportsList.add(report9);

        DisasterReport report10 = new DisasterReport("Landslide", "Mountain Pass Rd, Section 3", "Medium", "Assessing", LocalDateTime.now().minusDays(3));
        report10.getAssignedResources().add(new AssignedResource("Geotechnical Team", "Engineering Assessment", "Scheduled", "Dept of Transportation"));
        report10.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusDays(3).plusHours(2), "Road closed due to minor landslide. Geotechnical assessment pending.", "DOT Dispatch"));
        disasterReportsList.add(report10);

        DisasterReport report11 = new DisasterReport("Fire", "Old Warehouse, 12 Industrial Way", "Low", "Closed", LocalDateTime.now().minusDays(5));
        report11.getAssignedResources().add(new AssignedResource("Engine 5 (Archive)", "Fire Engine", "Returned to Station", "City Fire Dept"));
        report11.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusDays(5).plusHours(3), "Small fire extinguished. Cause under investigation.", "Engine 5"));
        report11.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusDays(4), "Investigation complete. Incident closed.", "Fire Marshal"));
        disasterReportsList.add(report11);

        DisasterReport report12 = new DisasterReport("Other", "Missing Person - Elderly, Silver Creek Park", "Medium", "Responding", LocalDateTime.now().minusHours(2));
        report12.getAssignedResources().add(new AssignedResource("Police K9 Unit", "K9 Search", "En Route", "Police Dept"));
        report12.getAssignedResources().add(new AssignedResource("Volunteer Search Group A", "Ground Search Volunteers", "Assembling", "Community CERT"));
        report12.getCommunicationLog().add(new LogEntry(LocalDateTime.now().minusHours(1).minusMinutes(45), "Search grid established. K9 unit deployed.", "Police Command"));
        disasterReportsList.add(report12);
    }

    /**
     * Retrieves all disaster reports.
     * @return An ObservableList of all DisasterReport objects.
     */
    public ObservableList<DisasterReport> getAllReports() {
        return disasterReportsList;
    }

    /**
     * Adds a new disaster report to the system.
     * The ID for the report is typically generated within the DisasterReport constructor.
     * @param report The DisasterReport object to add.
     */
    public void addReport(DisasterReport report) {
        if (report != null) {
            disasterReportsList.add(report);
            System.out.println("ReportService: Added new report ID: " + report.getId() + " - " + report.getType());
        }
    }

    /**
     * Retrieves a specific disaster report by its ID.
     * @param id The ID of the report to retrieve.
     * @return The DisasterReport object if found, otherwise null.
     */
    public DisasterReport getReportById(int id) {
        Optional<DisasterReport> reportOpt = disasterReportsList.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
        return reportOpt.orElse(null);
    }

    /**
     * Updates the status and/or priority of an existing disaster report.
     * @param reportId The ID of the report to update.
     * @param newStatus The new status (can be null or empty if not changing).
     * @param newPriority The new priority (can be null or empty if not changing).
     */
    public void updateReportStatusAndPriority(int reportId, String newStatus, String newPriority) {
        DisasterReport report = getReportById(reportId);
        if (report != null) {
            boolean changed = false;
            if (newStatus != null && !newStatus.trim().isEmpty() && !report.getStatus().equals(newStatus.trim())) {
                report.setStatus(newStatus.trim());
                changed = true;
            }
            if (newPriority != null && !newPriority.trim().isEmpty() && !report.getPriority().equals(newPriority.trim())) {
                report.setPriority(newPriority.trim());
                changed = true;
            }
            if (changed) {
                System.out.println("ReportService: Report ID " + reportId + " updated. New Status: " + report.getStatus() + ", New Priority: " + report.getPriority());
                
            }
        } else {
            System.err.println("ReportService: Attempted to update non-existent report ID: " + reportId);
        }
    }

    /**
     * Assigns a resource to a specific disaster report.
     * @param reportId The ID of the report.
     * @param resource The AssignedResource object to assign.
     */
    public void assignResourceToReport(int reportId, AssignedResource resource) {
        DisasterReport report = getReportById(reportId);
        if (report != null && resource != null) {
            // Could add a check to prevent duplicate assignments if necessary
            report.getAssignedResources().add(resource);
            System.out.println("ReportService: Resource '" + resource.getName() + "' assigned to report ID: " + reportId);
        } else {
            System.err.println("ReportService: Could not assign resource. Report ID: " + reportId + ", Resource: " + resource);
        }
    }

    /**
     * Adds a log entry to a specific disaster report.
     * @param reportId The ID of the report.
     * @param logEntry The LogEntry object to add.
     */
    public void addLogEntryToReport(int reportId, LogEntry logEntry) {
        DisasterReport report = getReportById(reportId);
        if (report != null && logEntry != null) {
            report.getCommunicationLog().add(logEntry);
            System.out.println("ReportService: Log entry added to report ID: " + reportId + " - '" + logEntry.getEntryText() + "'");
        } else {
            System.err.println("ReportService: Could not add log entry. Report ID: " + reportId + ", LogEntry: " + logEntry);
        }
    }

    /**
     * Marks a disaster report as "Closed".
     * @param reportId The ID of the report to close.
     */
    public void closeReport(int reportId) {
        DisasterReport report = getReportById(reportId);
        if (report != null) {
            if (!report.getStatus().equalsIgnoreCase("Closed")) {
                report.setStatus("Closed");
                // Optionally, add a final log entry about closure
                addLogEntryToReport(reportId, new LogEntry(LocalDateTime.now(), "Incident officially marked as Closed.", "System"));
                System.out.println("ReportService: Report ID " + reportId + " has been closed.");
            } else {
                System.out.println("ReportService: Report ID " + reportId + " is already closed.");
            }
        } else {
            System.err.println("ReportService: Attempted to close non-existent report ID: " + reportId);
        }
    }

    /**
    * Filters the list of disaster reports based on provided criteria, including a keyword search.
    * "All" or null/empty string for a criterion means no filtering on that criterion.
    * Keyword search looks in locationSummary and description.
    *
    * @param typeFilter Filter by disaster type.
    * @param priorityFilter Filter by priority.
    * @param statusFilter Filter by status.
    * @param keywordSearch Search term (case-insensitive).
    * @return An ObservableList of filtered DisasterReport objects.
    */
   public ObservableList<DisasterReport> getFilteredReports(String typeFilter, String priorityFilter, String statusFilter, String keywordSearch) {
       String keywordLower = (keywordSearch != null && !keywordSearch.trim().isEmpty()) ? keywordSearch.trim().toLowerCase() : null;

       return disasterReportsList.stream()
               .filter(r -> typeFilter == null || typeFilter.equalsIgnoreCase("All") || typeFilter.trim().isEmpty() || r.getType().equalsIgnoreCase(typeFilter.trim()))
               .filter(r -> priorityFilter == null || priorityFilter.equalsIgnoreCase("All") || priorityFilter.trim().isEmpty() || r.getPriority().equalsIgnoreCase(priorityFilter.trim()))
               .filter(r -> statusFilter == null || statusFilter.equalsIgnoreCase("All") || statusFilter.trim().isEmpty() || r.getStatus().equalsIgnoreCase(statusFilter.trim()))
               .filter(r -> keywordLower == null || // If no keyword, pass all
                            (r.getLocationSummary() != null && r.getLocationSummary().toLowerCase().contains(keywordLower)) ||
                            // Optional: Add search in description if DisasterReport has a description field
                            // (r.getDescription() != null && r.getDescription().toLowerCase().contains(keywordLower))
                            (String.valueOf(r.getId()).equals(keywordLower)) // Allow search by ID
               )
               .collect(Collectors.toCollection(FXCollections::observableArrayList));
   }

   
   public ObservableList<DisasterReport> getFilteredReports(String typeFilter, String priorityFilter, String statusFilter) {
       return getFilteredReports(typeFilter, priorityFilter, statusFilter, null); // Call the new one with null keyword
   }

   
}