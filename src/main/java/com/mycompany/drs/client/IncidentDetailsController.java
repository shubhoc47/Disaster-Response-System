/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.client;

/**
 *
 * @author shubh
 */

import com.mycompany.drs.shared.AssignedResource;
import com.mycompany.drs.shared.DisasterReport;
import com.mycompany.drs.shared.LogEntry;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class IncidentDetailsController implements Initializable {

    // --- FXML Elements ---
    @FXML private Label incidentIdLabel, incidentTypeLabel, incidentStatusLabel;
    @FXML private TextField overviewLocationField, overviewReportedTimeField, overviewPriorityField;
    @FXML private TextArea overviewDescriptionArea, overviewAssessmentArea;
    @FXML private TableView<AssignedResource> assignedResourcesTableView;
    @FXML private TableColumn<AssignedResource, String> colResourceName, colResourceType, colResourceStatus, colResourceContact;
    @FXML private TableView<LogEntry> communicationLogTableView;
    @FXML private TableColumn<LogEntry, String> colLogTimestamp, colLogEntry, colLogAuthor;
    @FXML private TextField newLogEntryField;
    @FXML private Button addLogEntryButton, updateIncidentButton, assignResourceButton, closeIncidentButton;

    private ClientController clientController;
    private DisasterReport currentIncident;
    private static int currentIncidentIdToLoad = -1;

    public static void setCurrentIncidentId(int id) {
        currentIncidentIdToLoad = id;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientController = ClientController.getInstance();
        setupTableColumns();
        if (currentIncidentIdToLoad != -1) {
            loadIncidentData(currentIncidentIdToLoad);
        } else {
            incidentIdLabel.setText("Incident ID: Error");
            disableAllControls(true);
        }
    }
    
    private void setupTableColumns() {
        colResourceName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colResourceType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colResourceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colResourceContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        colLogTimestamp.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTimestamp().format(formatter))
        );
        colLogEntry.setCellValueFactory(new PropertyValueFactory<>("entryText"));
        colLogAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
    }
    
    private void loadIncidentData(int incidentId) {
        this.currentIncident = clientController.getReportById(incidentId);
        if (currentIncident != null) {
            populateUI();
            disableAllControls(false);
            if ("Closed".equalsIgnoreCase(currentIncident.getStatus())) {
                disablePostClosureActions(true);
            }
        } else {
            incidentIdLabel.setText("Incident ID: " + incidentId + " (Not Found)");
            disableAllControls(true);
        }
    }
    
    private void populateUI() {
        incidentIdLabel.setText("Incident ID: " + currentIncident.getId());
        incidentTypeLabel.setText("Type: " + currentIncident.getType());
        updateStatusLabel();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        overviewLocationField.setText(currentIncident.getLocationSummary());
        overviewReportedTimeField.setText(currentIncident.getReportedTime().format(formatter));
        overviewPriorityField.setText(currentIncident.getPriority());
        
        assignedResourcesTableView.setItems(FXCollections.observableArrayList(currentIncident.getAssignedResources()));
        communicationLogTableView.setItems(FXCollections.observableArrayList(currentIncident.getCommunicationLog()));
        Platform.runLater(() -> communicationLogTableView.scrollTo(communicationLogTableView.getItems().size() - 1));
    }
    
    private void updateStatusLabel() {
        incidentStatusLabel.setText("Status: " + currentIncident.getStatus());
        String status = currentIncident.getStatus().toLowerCase();
        Color color = switch (status) {
            case "reported" -> Color.ORANGE;
            case "assessing" -> Color.LIGHTBLUE;
            case "responding" -> Color.DODGERBLUE;
            case "closed" -> Color.GREEN;
            default -> Color.BLACK;
        };
        incidentStatusLabel.setTextFill(color);
    }
    
    // --- FXML Handler Methods ---
    
    @FXML void handleBackToDashboard(ActionEvent event) {
        try {
            App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML void handleAssignResource(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    @FXML void handleUpdateResourceStatus(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    @FXML void handleAddLogEntry(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    @FXML void handleUpdateIncident(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    @FXML void handleRequestExternalAid(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    @FXML void handleGenerateSnippet(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    @FXML void handleCloseIncident(ActionEvent event) { showAlert(Alert.AlertType.INFORMATION, "Placeholder", "Functionality not yet implemented."); }
    
    // --- Helper Methods ---
    
    private void disableAllControls(boolean disable) { /* ... */ }
    private void disablePostClosureActions(boolean disable) { /* ... */ }
    private void showAlert(Alert.AlertType type, String title, String msg) { new Alert(type, msg).showAndWait(); }
}