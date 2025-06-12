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
import java.time.LocalDateTime;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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


    @FXML
    void handleAssignResource(ActionEvent event) {
        if (currentIncident == null) return;

        // --- Create a custom dialog to get resource details ---
        Dialog<AssignedResource> dialog = new Dialog<>();
        dialog.setTitle("Assign New Resource");
        dialog.setHeaderText("Enter details for resource to assign to Incident ID: " + currentIncident.getId());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Engine 5, SAR Team Bravo");
        TextField typeField = new TextField();
        typeField.setPromptText("e.g., Fire Engine, Search Team");
        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList("Dispatched", "En Route", "Staging"));
        statusCb.setValue("Dispatched"); // Default status

        grid.add(new Label("Name/Unit:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeField, 1, 1);
        grid.add(new Label("Initial Status:"), 0, 2);
        grid.add(statusCb, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an AssignedResource object when OK is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (nameField.getText().trim().isEmpty() || typeField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Input Error", "Name and Type cannot be empty.");
                    return null; // Prevents dialog from closing
                }
                return new AssignedResource(nameField.getText().trim(), typeField.getText().trim(), statusCb.getValue(), "");
            }
            return null;
        });

        Optional<AssignedResource> result = dialog.showAndWait();

        result.ifPresent(resource -> {
            if (resource != null) {
                // Send the new resource to the server to be saved
                boolean success = clientController.assignResource(currentIncident.getId(), resource);
                if (success) {
                    loadIncidentData(currentIncident.getId()); // Refresh the view
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to assign resource on the server.");
                }
            }
        });
    }
    
    @FXML
    void handleUpdateResourceStatus(ActionEvent event) {
        AssignedResource selectedResource = assignedResourcesTableView.getSelectionModel().getSelectedItem();

        // 1. Check if a resource is actually selected
        if (selectedResource == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a resource from the table to update its status.");
            return;
        }

        // 2. Create a dialog to get the new status
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Resource Status");
        dialog.setHeaderText("Update status for: " + selectedResource.getName());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList(
                "Dispatched", "En Route", "Staging", "On Scene", "Task Complete", "Returning", "Available"));
        statusCb.setValue(selectedResource.getStatus()); // Pre-select the current status

        VBox vbox = new VBox(10, new Label("New Status:"), statusCb);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        // Convert the result to a string when OK is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return statusCb.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        // 3. If a new status was chosen, send the update request to the server
        result.ifPresent(newStatus -> {
            if (newStatus != null && !newStatus.equals(selectedResource.getStatus())) {
                boolean success = clientController.updateResourceStatus(selectedResource.getResourceId(), currentIncident.getId(), newStatus);
                if (success) {
                    loadIncidentData(currentIncident.getId()); // Refresh the entire view
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update resource status on the server.");
                }
            }
        });
    }
    
    
    
    @FXML
    void handleAddLogEntry(ActionEvent event) {
        String entryText = newLogEntryField.getText().trim();
        if (entryText.isEmpty() || currentIncident == null) {
            return;
        }

        // Create the LogEntry object
        String author = clientController.getLoggedInUsername();
        LogEntry newLog = new LogEntry(LocalDateTime.now(), entryText, author);

        // Send the request to the server
        boolean success = clientController.addLogEntry(currentIncident.getId(), newLog);

        if (success) {
            System.out.println("CLIENT: Log entry submitted successfully.");
            // Clear the input field and refresh the data from the server
            newLogEntryField.clear();
            loadIncidentData(currentIncident.getId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add log entry on the server.");
        }
    }
    
    @FXML
    void handleUpdateIncident(ActionEvent event) {
        if (currentIncident == null) return;

        // --- Create a custom dialog ---
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Incident Status/Priority");
        dialog.setHeaderText("Update details for Incident ID: " + currentIncident.getId());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList("Reported", "Assessing", "Responding", "Closed"));
        statusCb.setValue(currentIncident.getStatus());
        ComboBox<String> priorityCb = new ComboBox<>(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        priorityCb.setValue(currentIncident.getPriority());

        VBox vbox = new VBox(10, new Label("New Status:"), statusCb, new Label("New Priority:"), priorityCb);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newStatus = statusCb.getValue();
            String newPriority = priorityCb.getValue();

            // Send the update request to the server
            boolean success = clientController.updateIncidentStatus(currentIncident.getId(), newStatus, newPriority);

            if (success) {
                loadIncidentData(currentIncident.getId()); // Refresh
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update incident on the server.");
            }
        }
    }
    @FXML
    void handleRequestExternalAid(ActionEvent event) {
        if (currentIncident == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Request External Aid");
        dialog.setHeaderText("Specify aid request for Incident ID: " + currentIncident.getId());
        dialog.setContentText("Request details (e.g., 'Police for traffic control x2 units'):");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(details -> {
            if (!details.trim().isEmpty()) {
                String logText = "EXTERNAL AID REQUEST: " + details.trim();
                LogEntry newLog = new LogEntry(LocalDateTime.now(), logText, clientController.getLoggedInUsername());

                boolean success = clientController.addLogEntry(currentIncident.getId(), newLog);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Aid Requested", "The request has been logged.");
                    loadIncidentData(currentIncident.getId()); // Refresh to see the new log
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to log the aid request on the server.");
                }
            }
        });
    }
    
    @FXML
    void handleGenerateSnippet(ActionEvent event) {
        if (currentIncident == null) {
            showAlert(Alert.AlertType.WARNING, "No Incident", "No incident data is loaded.");
            return;
        }

        // --- Build the Snippet String ---
        StringBuilder snippet = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        snippet.append("--- INCIDENT SNIPPET ---\n");
        snippet.append("ID: ").append(currentIncident.getId()).append("\n");
        snippet.append("Type: ").append(currentIncident.getType()).append("\n");
        snippet.append("Location: ").append(currentIncident.getLocationSummary()).append("\n");
        snippet.append("Priority: ").append(currentIncident.getPriority()).append("\n");
        snippet.append("Status: ").append(currentIncident.getStatus()).append("\n");
        snippet.append("Reported: ").append(currentIncident.getReportedTime().format(formatter)).append("\n\n");

        snippet.append("--- ASSIGNED RESOURCES (").append(currentIncident.getAssignedResources().size()).append(") ---\n");
        if (currentIncident.getAssignedResources().isEmpty()) {
            snippet.append("None\n");
        } else {
            currentIncident.getAssignedResources().forEach(res -> 
                snippet.append(String.format("- %s (%s) - Status: %s\n", res.getName(), res.getType(), res.getStatus()))
            );
        }

        // --- Create and Show the Dialog ---
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Snippet");
        alert.setHeaderText("Summary for Incident ID: " + currentIncident.getId());

        TextArea textArea = new TextArea(snippet.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setContent(expContent);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(500, 350);

        alert.showAndWait();
    }
    
    @FXML
    void handleCloseIncident(ActionEvent event) {
        if (currentIncident == null || "Closed".equalsIgnoreCase(currentIncident.getStatus())) {
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Closure");
        confirmDialog.setHeaderText("Are you sure you want to close Incident ID: " + currentIncident.getId() + "?");
        confirmDialog.setContentText("This action cannot be easily undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // We reuse the existing update status method, just with a "Closed" status.
            boolean success = clientController.updateIncidentStatus(
                currentIncident.getId(), 
                "Closed", 
                currentIncident.getPriority() // Keep the priority the same
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Incident Closed", "Incident ID " + currentIncident.getId() + " has been marked as closed.");
                loadIncidentData(currentIncident.getId()); // Refresh to disable buttons
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to close the incident on the server.");
            }
        }
    }
    
    // --- Helper Methods ---
    
    private void disableAllControls(boolean disable) {
        // Disables buttons that require an active incident
        updateIncidentButton.setDisable(disable);
        assignResourceButton.setDisable(disable);
        closeIncidentButton.setDisable(disable);
        addLogEntryButton.setDisable(disable);
        newLogEntryField.setDisable(disable);

        // Also disable the button on the resource tab
        Button updateResourceBtn = (Button) assignedResourcesTableView.getParent().lookup("#updateResourceStatusButton");
        if (updateResourceBtn != null) {
            updateResourceBtn.setDisable(disable);
        }

        // Also disable buttons on the right-hand panel
        VBox coordinationActions = (VBox) updateIncidentButton.getParent();
        coordinationActions.getChildren().forEach(node -> node.setDisable(disable));
    }

    private void disablePostClosureActions(boolean disable) {
        if (disable) {
            // Disable everything except the "Back to Dashboard" and "Generate Snippet" buttons
            disableAllControls(true);
            closeIncidentButton.setText("Incident Closed"); // Change button text

            // Re-enable the snippet button as it's a read-only action
            Button snippetBtn = (Button) closeIncidentButton.getParent().lookup("#generateSnippetButton");
            if(snippetBtn != null) {
                snippetBtn.setDisable(false);
            }
        }
    }
    private void showAlert(Alert.AlertType type, String title, String msg) { new Alert(type, msg).showAndWait(); }
}