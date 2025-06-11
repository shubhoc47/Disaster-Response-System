/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.model.AssignedResource;
import com.mycompany.drs.model.DisasterReport;
import com.mycompany.drs.model.LogEntry;
import service.ReportService;
import service.UserService; // To get current user for log author etc.

import javafx.application.Platform; // For Platform.runLater
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane; // For dialogs
import javafx.geometry.Insets; // For dialogs
import javafx.scene.paint.Color; // For status label color

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.layout.VBox;

public class IncidentDetailsController implements Initializable {

    // --- FXML Elements ---
    // Top section
    @FXML private Label incidentIdLabel;
    @FXML private Label incidentTypeLabel;
    @FXML private Label incidentStatusLabel;
    @FXML private Button backToDashboardButton;

    // Overview Tab
    @FXML private TextField overviewLocationField;
    @FXML private TextField overviewReportedTimeField;
    @FXML private TextField overviewPriorityField;
    @FXML private TextField overviewReportedByField;
    @FXML private TextArea overviewDescriptionArea;
    @FXML private TextArea overviewAssessmentArea;

    // Assigned Resources Tab
    @FXML private TableView<AssignedResource> assignedResourcesTableView;
    @FXML private TableColumn<AssignedResource, String> colResourceName;
    @FXML private TableColumn<AssignedResource, String> colResourceType;
    @FXML private TableColumn<AssignedResource, String> colResourceStatus;
    @FXML private TableColumn<AssignedResource, String> colResourceContact;
    @FXML private Button assignResourceButton;
    @FXML private Button updateResourceStatusButton;

    // Communication Log Tab
    @FXML private TableView<LogEntry> communicationLogTableView;
    @FXML private TableColumn<LogEntry, String> colLogTimestamp;
    @FXML private TableColumn<LogEntry, String> colLogEntry;
    @FXML private TableColumn<LogEntry, String> colLogAuthor;
    @FXML private TextField newLogEntryField;
    @FXML private Button addLogEntryButton;

    // Right Actions Panel
    @FXML private Button updateIncidentButton;
    @FXML private Button assignReassignButton; // Can link to handleAssignResource
    @FXML private Button requestExternalAidButton;
    @FXML private Button generateSnippetButton;
    @FXML private Button closeIncidentButton;

    // --- Services & Data ---
    private ReportService reportService;
    private UserService userService;
    private DisasterReport currentIncident;
    private static int currentIncidentIdToLoad = -1; // Static way to pass ID

    // Static method called by DashboardController BEFORE loading this FXML
    public static void setCurrentIncidentId(int id) {
        currentIncidentIdToLoad = id;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportService = App.getReportService();
        userService = App.getUserService(); // Get shared UserService

        setupResourceTableColumns();
        setupLogTableColumns();

        if (currentIncidentIdToLoad != -1) {
            loadIncidentData(currentIncidentIdToLoad);
        } else {
            incidentIdLabel.setText("Incident ID: Error - No ID Provided");
            showAlert(Alert.AlertType.ERROR, "Load Error", "No incident ID was provided to display details.", "");
            disableAllActionControls(true); // Disable controls if no incident
        }
        System.out.println("IncidentDetailsController initialized for ID: " + currentIncidentIdToLoad);
    }

    private void setupResourceTableColumns() {
        colResourceName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colResourceType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colResourceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colResourceContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
    }

    private void setupLogTableColumns() {
        colLogTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colLogEntry.setCellValueFactory(new PropertyValueFactory<>("entryText"));
        colLogAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
    }

    /**
     * Action: loadIncidentData(): Ensure this method correctly fetches the DisasterReport
     * (including its lists of assigned resources and log entries) from reportService
     * using the passed ID and populates all relevant UI fields and tables in all tabs.
     * Handle the case where an incident is not found.
     */
    public void loadIncidentData(int incidentId) {
        currentIncident = reportService.getReportById(incidentId); // Fetch from service

        if (currentIncident != null) {
            // Populate Top Section
            incidentIdLabel.setText("Incident ID: " + currentIncident.getId());
            incidentTypeLabel.setText("Type: " + currentIncident.getType());
            updateStatusLabelAndColor();

            // Populate Overview Tab
            overviewLocationField.setText(currentIncident.getLocationSummary());
            overviewReportedTimeField.setText(currentIncident.getReportedTime()); // Assumes formatted string
            overviewPriorityField.setText(currentIncident.getPriority());
            overviewReportedByField.setText("Dispatcher/System"); // Placeholder
            overviewDescriptionArea.setText("Details for " + currentIncident.getType() + " at " + currentIncident.getLocationSummary() + "."); // Placeholder
            overviewAssessmentArea.setText("Awaiting detailed assessment."); // Placeholder


            // Populate Assigned Resources Tab
            // The TableView items are directly bound to the ObservableList in the DisasterReport model
            assignedResourcesTableView.setItems(currentIncident.getAssignedResources());
            setTablePlaceholder(assignedResourcesTableView, "No resources currently assigned.");

            // Populate Communication Log Tab
            communicationLogTableView.setItems(currentIncident.getCommunicationLog());
            setTablePlaceholder(communicationLogTableView, "Communication log is empty.");
            // Scroll to the bottom of the log
            if (!currentIncident.getCommunicationLog().isEmpty()) {
                Platform.runLater(() -> communicationLogTableView.scrollTo(currentIncident.getCommunicationLog().size() - 1));
            }


            disableAllActionControls(false); // Enable controls
            if ("Closed".equalsIgnoreCase(currentIncident.getStatus())) {
                disablePostClosureActions(true);
            }

        } else {
            incidentIdLabel.setText("Incident ID: " + incidentId + " (Not Found)");
            incidentTypeLabel.setText("Type: N/A");
            incidentStatusLabel.setText("Status: N/A");
            showAlert(Alert.AlertType.ERROR, "Load Error", "Incident with ID " + incidentId + " could not be found.", "");
            disableAllActionControls(true); // Disable controls if incident not found
        }
    }

    private void updateStatusLabelAndColor() {
        if (currentIncident == null) return;
        incidentStatusLabel.setText("Status: " + currentIncident.getStatus());
        String status = currentIncident.getStatus().toLowerCase();
        Color color = Color.BLACK; // Default
        switch (status) {
            case "reported": color = Color.ORANGE; break;
            case "assessing": color = Color.LIGHTBLUE; break;
            case "responding": color = Color.DODGERBLUE; break;
            case "mitigation": color = Color.MEDIUMPURPLE; break;
            case "recovery": color = Color.DARKSEAGREEN; break;
            case "closed": color = Color.GREEN; break;
        }
        incidentStatusLabel.setTextFill(color);
    }
    
    private void disableAllActionControls(boolean disable) {
        updateIncidentButton.setDisable(disable);
        assignReassignButton.setDisable(disable);
        assignResourceButton.setDisable(disable); // Ensure this is linked in FXML or declared
        updateResourceStatusButton.setDisable(disable); // Ensure this is linked in FXML or declared
        requestExternalAidButton.setDisable(disable);
        generateSnippetButton.setDisable(disable);
        closeIncidentButton.setDisable(disable);
        addLogEntryButton.setDisable(disable);
        newLogEntryField.setDisable(disable);
    }

    private void disablePostClosureActions(boolean disable) {
        // Actions that shouldn't be available after an incident is closed
        updateIncidentButton.setDisable(disable);
        assignReassignButton.setDisable(disable);
        assignResourceButton.setDisable(disable);
        updateResourceStatusButton.setDisable(disable);
        requestExternalAidButton.setDisable(disable);
        addLogEntryButton.setDisable(disable);
        newLogEntryField.setDisable(disable);
        // closeIncidentButton might also be disabled or change text to "Re-open"
        closeIncidentButton.setText(disable ? "Incident Closed" : "Close Incident");
        closeIncidentButton.setDisable(disable);
    }


    /**
     * Action: handleBackToDashboard(): Navigate back to the Dashboard.
     */
    @FXML
    void handleBackToDashboard(ActionEvent event) {
        System.out.println("Back to Dashboard clicked.");
        try {
            App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not return to the dashboard.", e.getMessage());
        }
    }

    /**
     * Action: handleAddLogEntry(): Create a LogEntry object and add it to the currentIncident's log
     * via reportService. The TableView should auto-update.
     */
    @FXML
    void handleAddLogEntry(ActionEvent event) {
        if (currentIncident == null) return;
        String entryText = newLogEntryField.getText();
        if (entryText == null || entryText.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Log entry cannot be empty.", "");
            newLogEntryField.requestFocus();
            return;
        }
        String author = userService.isLoggedIn() ? userService.getLoggedInUsername() : "System";
        LogEntry newEntry = new LogEntry(LocalDateTime.now(), entryText.trim(), author);
        reportService.addLogEntryToReport(currentIncident.getId(), newEntry);
        // TableView `communicationLogTableView` should update automatically because its items
        // list is the `ObservableList` from `currentIncident.getCommunicationLog()`.
        newLogEntryField.clear();
        Platform.runLater(() -> communicationLogTableView.scrollTo(currentIncident.getCommunicationLog().size() - 1)); // Scroll to new entry
        System.out.println("New log entry added for incident ID: " + currentIncident.getId());
    }

    /**
     * Action: handleAssignResource() & handleUpdateResourceStatus(): Implement the dialogs
     * to add/update AssignedResource objects for the currentIncident.
     * These should update the assignedResourcesTableView.
     */
    @FXML
    void handleAssignResource(ActionEvent event) {
        if (currentIncident == null) return;
        System.out.println("Assign Resource button clicked.");

        Dialog<AssignedResource> dialog = new Dialog<>();
        dialog.setTitle("Assign New Resource");
        dialog.setHeaderText("Enter details for resource to assign to Incident ID: " + currentIncident.getId());
        dialog.initOwner(assignResourceButton.getScene().getWindow()); // Set owner for proper modality

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Name (e.g., Engine 1)");
        TextField typeField = new TextField(); typeField.setPromptText("Type (e.g., Fire Engine)");
        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList("Dispatched", "En Route", "On Scene", "Staging", "Returning"));
        statusCb.setPromptText("Initial Status");
        TextField contactField = new TextField(); contactField.setPromptText("Contact (Optional)");

        grid.add(new Label("Name*:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Type*:"), 0, 1); grid.add(typeField, 1, 1);
        grid.add(new Label("Status*:"), 0, 2); grid.add(statusCb, 1, 2);
        grid.add(new Label("Contact:"), 0, 3); grid.add(contactField, 1, 3);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                if (nameField.getText().trim().isEmpty() || typeField.getText().trim().isEmpty() || statusCb.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Name, Type, and Status are required.", "");
                    return null; // Keep dialog open
                }
                return new AssignedResource(nameField.getText().trim(), typeField.getText().trim(), statusCb.getValue(), contactField.getText().trim());
            }
            return null;
        });

        Optional<AssignedResource> result = dialog.showAndWait();
        result.ifPresent(resource -> {
            reportService.assignResourceToReport(currentIncident.getId(), resource);
            // TableView should update automatically.
            reportService.addLogEntryToReport(currentIncident.getId(), new LogEntry("Resource Assigned: " + resource.getName() + " (" + resource.getType() + ")", getCurrentUserForLog()));
        });
    }

    @FXML
    void handleUpdateResourceStatus(ActionEvent event) {
        if (currentIncident == null) return;
        AssignedResource selectedResource = assignedResourcesTableView.getSelectionModel().getSelectedItem();
        if (selectedResource == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a resource from the table to update its status.", "");
            return;
        }

        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList("Dispatched", "En Route", "On Scene", "Staging", "Task Complete", "Returning", "Available"));
        statusCb.setValue(selectedResource.getStatus());

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Resource Status");
        dialog.setHeaderText("Update status for: " + selectedResource.getName() + " (Type: " + selectedResource.getType() + ")");
        dialog.initOwner(updateResourceStatusButton.getScene().getWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        VBox vbox = new VBox(10, new Label("New Status:"), statusCb);
        vbox.setPadding(new Insets(20,20,20,20));
        dialog.getDialogPane().setContent(vbox);
        
        Platform.runLater(statusCb::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return statusCb.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStatus -> {
            if (newStatus != null && !newStatus.equals(selectedResource.getStatus())) {
                String oldStatus = selectedResource.getStatus();
                selectedResource.setStatus(newStatus); // Update the model object
                assignedResourcesTableView.refresh(); // Tell TableView to redraw the row
                reportService.addLogEntryToReport(currentIncident.getId(),
                        new LogEntry("Status of resource '" + selectedResource.getName() + "' changed from '" + oldStatus + "' to: " + newStatus, getCurrentUserForLog()));
            }
        });
    }


    /**
     * Action: handleUpdateIncident(): Implement the dialog to change the currentIncident's
     * status and priority via reportService. Refresh the displayed data.
     */
    @FXML
    void handleUpdateIncident(ActionEvent event) {
        if (currentIncident == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Incident Status & Priority");
        dialog.setHeaderText("Update for Incident ID: " + currentIncident.getId());
        dialog.initOwner(updateIncidentButton.getScene().getWindow());
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList("Reported", "Assessing", "Responding", "Mitigation", "Recovery", "Closed"));
        statusCb.setValue(currentIncident.getStatus());
        ComboBox<String> priorityCb = new ComboBox<>(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        priorityCb.setValue(currentIncident.getPriority());

        grid.add(new Label("New Status:"), 0, 0); grid.add(statusCb, 1, 0);
        grid.add(new Label("New Priority:"), 0, 1); grid.add(priorityCb, 1, 1);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(statusCb::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == updateButtonType) {
            String newStatus = statusCb.getValue();
            String newPriority = priorityCb.getValue();
            reportService.updateReportStatusAndPriority(currentIncident.getId(), newStatus, newPriority);
            loadIncidentData(currentIncident.getId()); // Reload to reflect all changes and refresh UI
            reportService.addLogEntryToReport(currentIncident.getId(),
                        new LogEntry("Incident status updated to: " + newStatus + ", Priority to: " + newPriority, getCurrentUserForLog()));
        }
    }

    /**
     * Action: handleCloseIncident(): Update the currentIncident's status to "Closed"
     * via reportService. Refresh displayed data.
     */
    @FXML
    void handleCloseIncident(ActionEvent event) {
        if (currentIncident == null) return;
        if ("Closed".equalsIgnoreCase(currentIncident.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Already Closed", "This incident is already marked as closed.", "");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Closure");
        confirmDialog.setHeaderText("Close Incident ID: " + currentIncident.getId());
        confirmDialog.setContentText("Are you sure you want to mark this incident as 'Closed'?");
        confirmDialog.initOwner(closeIncidentButton.getScene().getWindow());

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            reportService.closeReport(currentIncident.getId());
            loadIncidentData(currentIncident.getId()); // Refresh data to show updated status and disable buttons
        }
    }

    /**
     * Action: Other Placeholder Buttons: Implement as informational alerts for now.
     */
    @FXML
    void handleRequestExternalAid(ActionEvent event) {
        if (currentIncident == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Request External Aid");
        dialog.setHeaderText("Specify aid request for Incident ID: " + currentIncident.getId());
        dialog.setContentText("Request details (e.g., 'Police for traffic control x2 units'):");
        dialog.initOwner(requestExternalAidButton.getScene().getWindow());
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(details -> {
            if (!details.trim().isEmpty()) {
                reportService.addLogEntryToReport(currentIncident.getId(), new LogEntry("External Aid Requested: " + details, getCurrentUserForLog()));
                showAlert(Alert.AlertType.INFORMATION, "Aid Requested", "Request logged: " + details, "");
            }
        });
    }

    @FXML
    void handleGenerateSnippet(ActionEvent event) {
        if (currentIncident == null) return;
        StringBuilder snippet = new StringBuilder();
        snippet.append("Incident Snippet for ID: ").append(currentIncident.getId()).append("\n");
        snippet.append("Type: ").append(currentIncident.getType()).append("\n");
        snippet.append("Location: ").append(currentIncident.getLocationSummary()).append("\n");
        snippet.append("Status: ").append(currentIncident.getStatus()).append("\n");
        snippet.append("Priority: ").append(currentIncident.getPriority()).append("\n");
        snippet.append("Reported: ").append(currentIncident.getReportedTime()).append("\n");
        snippet.append("Assigned Resources: ").append(currentIncident.getAssignedResources().size()).append("\n");
        if (!currentIncident.getCommunicationLog().isEmpty()) {
            snippet.append("Last Log Entry: ").append(currentIncident.getCommunicationLog().get(currentIncident.getCommunicationLog().size()-1).getEntryText());
        } else {
            snippet.append("Last Log Entry: N/A");
        }

        TextArea textArea = new TextArea(snippet.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Incident Snippet");
        alert.setHeaderText("Summary for Incident ID: " + currentIncident.getId());
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true); // Allow resizing the alert dialog
        alert.initOwner(generateSnippetButton.getScene().getWindow());
        alert.showAndWait();
    }
    
    private String getCurrentUserForLog() {
        return userService.isLoggedIn() ? userService.getLoggedInUsername() : "System";
    }

    private void setTablePlaceholder(TableView<?> tableView, String message) {
        if (tableView.getItems() == null || tableView.getItems().isEmpty()) {
            tableView.setPlaceholder(new Label(message));
        } else {
            tableView.setPlaceholder(null); // Clear placeholder if there's data
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        // Try to set owner for better modality
        if (incidentIdLabel != null && incidentIdLabel.getScene() != null && incidentIdLabel.getScene().getWindow() != null) {
           alert.initOwner(incidentIdLabel.getScene().getWindow());
        }
        alert.showAndWait();
    }

    private void showErrorAlert(String navigation_Error, String could_not_return_to_the_dashboard, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}