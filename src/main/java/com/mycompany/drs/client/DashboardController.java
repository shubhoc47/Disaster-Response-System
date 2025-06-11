/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.client;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.shared.DisasterReport; // Use shared model
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;

public class DashboardController implements Initializable {
    // --- FXML Elements ---
    @FXML private Label loggedInUserLabel;
    @FXML private Button newReportButton;
    @FXML private Button manageUsersButton; // For new functionality
    @FXML private TableView<DisasterReport> incidentsTableView;
    @FXML private TableColumn<DisasterReport, Integer> colId;
    @FXML private TableColumn<DisasterReport, String> colType;
    @FXML private TableColumn<DisasterReport, String> colLocation;
    @FXML private TableColumn<DisasterReport, String> colPriority;
    @FXML private TableColumn<DisasterReport, String> colStatus;
    @FXML private TableColumn<DisasterReport, String> colReportedTime;
    @FXML private TextField keywordSearchField;
    @FXML private ComboBox<String> disasterTypeFilterComboBox;
    @FXML private ComboBox<String> priorityFilterComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;

    private ClientController clientController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientController = ClientController.getInstance();

        setupTableViewColumns();
        populateFilterComboBoxes(); // <-- ADD THIS CALL
        loadInitialReports();
        updateLoggedInUserLabelAndRoles();

        incidentsTableView.setOnMouseClicked(this::handleTableDoubleClick);

        // --- ADD THIS LISTENER FOR REAL-TIME SEARCH ---
        keywordSearchField.textProperty().addListener((obs, oldText, newText) -> {
            handleApplyFilters(null); // Trigger filtering every time the text changes
        });
    }

    private void populateFilterComboBoxes() {
        // Add "All" as the first option for easy clearing of filters
        disasterTypeFilterComboBox.getItems().add("All");
        priorityFilterComboBox.getItems().add("All");
        statusFilterComboBox.getItems().add("All");

        // Add the specific options
        disasterTypeFilterComboBox.getItems().addAll("Fire", "Flood", "Earthquake", "Chemical Spill", "Power Outage", "Wildfire", "Traffic Accident", "Gas Leak", "Tornado Warning", "Landslide", "Infrastructure Failure", "Train Derailment", "Hurricane", "Other");
        priorityFilterComboBox.getItems().addAll("Low", "Medium", "High", "Critical");
        statusFilterComboBox.getItems().addAll("Reported", "Assessing", "Responding", "Closed");

        // Set "All" as the default selection
        disasterTypeFilterComboBox.setValue("All");
        priorityFilterComboBox.setValue("All");
        statusFilterComboBox.setValue("All");
    }
    
    private void setupTableViewColumns() {
        // Since our model no longer has JavaFX properties, we adapt.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("locationSummary"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // For LocalDateTime, we need a custom cell value factory.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        colReportedTime.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReportedTime() != null) {
                return new SimpleStringProperty(cellData.getValue().getReportedTime().format(formatter));
            }
            return new SimpleStringProperty("");
        });
    }

    private void loadInitialReports() {
        // Request the data from the server via the ClientController
        List<DisasterReport> reportsFromServer = clientController.getAllReports();
        
        // Convert the standard List to an ObservableList for the TableView
        ObservableList<DisasterReport> reportsForTable = FXCollections.observableArrayList(reportsFromServer);
        incidentsTableView.setItems(reportsForTable);
        
        if (reportsForTable.isEmpty()) {
            incidentsTableView.setPlaceholder(new Label("No incidents to display."));
        }
    }

    private void updateLoggedInUserLabelAndRoles() {
        if (clientController.isLoggedIn()) {
            loggedInUserLabel.setText("Logged in as: " + clientController.getLoggedInUsername() +
                                      " - Role: " + clientController.getCurrentUserRole());
            // Role-Based Access Control (RBAC)
            boolean isAdmin = "ADMIN".equalsIgnoreCase(clientController.getCurrentUserRole());
            manageUsersButton.setVisible(isAdmin);
            manageUsersButton.setManaged(isAdmin);
        } else {
            loggedInUserLabel.setText("Not logged in.");
            manageUsersButton.setVisible(false);
            manageUsersButton.setManaged(false);
        }
    }

    private void handleTableDoubleClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            DisasterReport selectedReport = incidentsTableView.getSelectionModel().getSelectedItem();
            if (selectedReport != null) {
                navigateToIncidentDetails(selectedReport);
            }
        }
    }

    private void navigateToIncidentDetails(DisasterReport report) {
        if (report == null) return;
        try {
            // Pass the ID to the next controller so it can fetch its own data.
            IncidentDetailsController.setCurrentIncidentId(report.getId());
            App.setRoot("IncidentDetails", "DRS - Incident Details (ID: " + report.getId() + ")", 1000.0, 700.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleNewReport(ActionEvent event) {
        try {
            App.setRoot("ReportDisasterForm", "DRS - New Disaster Report", 800.0, 830.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void handleLogout(ActionEvent event) {
        clientController.logout();
        try {
            App.setRoot("Login", "DRS - Login", App.INITIAL_WINDOW_WIDTH, App.INITIAL_WINDOW_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void handleMenuFileExit(ActionEvent event) {
        System.out.println("CLIENT: Menu File > Exit clicked.");
        // This is the correct way to close a JavaFX application.
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void handleMenuViewRefresh(ActionEvent event) {
        System.out.println("CLIENT: Menu View > Refresh Data clicked.");
        // Reloads all reports from the server.
        loadInitialReports(); 
        incidentsTableView.refresh();
        showAlert(Alert.AlertType.INFORMATION, "Data Refreshed", "Incident data has been refreshed from the server.");
    }
    
    @FXML
    void handleMenuHelpAbout(ActionEvent event) {
        System.out.println("CLIENT: Menu Help > About clicked.");
        showAlert(Alert.AlertType.INFORMATION, "About DRS", 
            "Disaster Response System - v3.0\nThis is the client-server version of the application.");
    }

    @FXML
    void handleViewAllReports(ActionEvent event) {
        System.out.println("CLIENT: View All Reports / Refresh clicked.");
        // This action is identical to the menu refresh.
        handleMenuViewRefresh(event);
    }
    
    @FXML
    void handleGenerateSitRep(ActionEvent event) {
        System.out.println("CLIENT: Generate Situation Report button clicked - Placeholder.");
        showAlert(Alert.AlertType.INFORMATION, "Feature Placeholder", "Generate Situation Report feature is not yet implemented.");
    }

    @FXML
    void handleManageUsers(ActionEvent event) {
        System.out.println("CLIENT: Manage Users button clicked (Admin Only Action).");
        showAlert(Alert.AlertType.INFORMATION, "Admin Action", "This feature would allow administrators to manage user accounts.");
    }
    
    @FXML
    void handleApplyFilters(ActionEvent event) {
        String type = disasterTypeFilterComboBox.getValue();
        String priority = priorityFilterComboBox.getValue();
        String status = statusFilterComboBox.getValue();
        String keyword = keywordSearchField.getText();

        // The ClientController will need a new method to handle this
        List<DisasterReport> filteredReports = clientController.getFilteredReports(type, priority, status, keyword);

        incidentsTableView.setItems(FXCollections.observableArrayList(filteredReports));
        if (filteredReports.isEmpty()) {
            incidentsTableView.setPlaceholder(new Label("No incidents match the filter criteria."));
        }
    }

    @FXML
    void handleClearFilters(ActionEvent event) {
        // Reset the UI controls
        keywordSearchField.clear();
        disasterTypeFilterComboBox.setValue("All");
        priorityFilterComboBox.setValue("All");
        statusFilterComboBox.setValue("All");
        // Reload all reports from the server
        loadInitialReports();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Implement other button handlers like filters, refresh, etc.
    // They will all call methods on clientController to get filtered data from the server.
}