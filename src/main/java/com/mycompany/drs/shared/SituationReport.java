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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SituationReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime generationTime;
    private final int totalActiveIncidents;
    private final Map<String, Long> incidentsByStatus; // e.g., {"Responding": 5, "Assessing": 3}
    private final Map<String, Long> incidentsByPriority; // e.g., {"Critical": 4, "High": 4}
    private final List<DisasterReport> highPriorityIncidents;

    public SituationReport(int totalActive, Map<String, Long> byStatus, Map<String, Long> byPriority, List<DisasterReport> highPriority) {
        this.generationTime = LocalDateTime.now();
        this.totalActiveIncidents = totalActive;
        this.incidentsByStatus = byStatus;
        this.incidentsByPriority = byPriority;
        this.highPriorityIncidents = highPriority;
    }

    // --- Getters ---
    public LocalDateTime getGenerationTime() { return generationTime; }
    public int getTotalActiveIncidents() { return totalActiveIncidents; }
    public Map<String, Long> getIncidentsByStatus() { return incidentsByStatus; }
    public Map<String, Long> getIncidentsByPriority() { return incidentsByPriority; }
    public List<DisasterReport> getHighPriorityIncidents() { return highPriorityIncidents; }
}