/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.model.DisasterReport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform; // For Platform.exit()
import javafx.scene.layout.HBox; // Only if paginationControls is actually used beyond placeholder

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import service.ReportService;
import service.UserService;

public class DashboardController implements Initializable {

    // --- FXML Elements ---
    @FXML private Label loggedInUserLabel;
    @FXML private Button logoutButton;
    @FXML private MenuItem menuFileExit;
    @FXML private MenuItem menuViewRefresh;
    @FXML private MenuItem menuHelpAbout;

    @FXML private Button newReportButton;
    @FXML private Button viewAllReportsButton;
    @FXML private Button generateSitRepButton;
    @FXML private Button manageUsersButton;

    @FXML private ComboBox<String> disasterTypeFilterComboBox;
    @FXML private ComboBox<String> priorityFilterComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private Button applyFiltersButton;
    @FXML private Button clearFiltersButton;

    @FXML private TableView<DisasterReport> incidentsTableView;
    @FXML private TableColumn<DisasterReport, Integer> colId;
    @FXML private TableColumn<DisasterReport, String> colType;
    @FXML private TableColumn<DisasterReport, String> colLocation;
    @FXML private TableColumn<DisasterReport, String> colPriority;
    @FXML private TableColumn<DisasterReport, String> colStatus;
    @FXML private TableColumn<DisasterReport, String> colReportedTime;
    @FXML private TextField keywordSearchField;

    @FXML private HBox paginationControls; // For future use, currently placeholder

    // --- Services & Data ---
    private ReportService reportService;
    private UserService userService;
    // No need for a separate masterReportList field if we always fetch from service
    // or if the service's list is the one we directly use and observe.

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get shared service instances from App.java
        reportService = App.getReportService();
        userService = App.getUserService();

        setupTableViewColumns();
        populateFilterComboBoxes();
        loadInitialReports(); // Load all reports initially

        updateLoggedInUserLabelAndRoles(); // Display logged-in user info

        // Add listener for double-click on TableView row
        incidentsTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                DisasterReport selectedReport = incidentsTableView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    navigateToIncidentDetails(selectedReport);
                }
            }
        });
        
        // Add listener for keyword search field for real-time filtering
        keywordSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleApplyFilters(null); // Trigger filtering whenever text changes
        });
        
        System.out.println("DashboardController initialized.");
    }

    private void setupTableViewColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("locationSummary"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colReportedTime.setCellValueFactory(new PropertyValueFactory<>("reportedTime"));
    }

    private void populateFilterComboBoxes() {
        disasterTypeFilterComboBox.setItems(FXCollections.observableArrayList("All", "Fire", "Flood", "Earthquake", "Chemical Spill", "Other"));
        priorityFilterComboBox.setItems(FXCollections.observableArrayList("All", "Critical", "High", "Medium", "Low")); // Ordered typically
        statusFilterComboBox.setItems(FXCollections.observableArrayList("All", "Reported", "Assessing", "Responding", "Closed")); // Add more as needed

        // Set default "All" selection
        disasterTypeFilterComboBox.setValue("All");
        priorityFilterComboBox.setValue("All");
        statusFilterComboBox.setValue("All");
    }

    private void loadInitialReports() {
        ObservableList<DisasterReport> allReports = reportService.getAllReports();
        incidentsTableView.setItems(allReports);
        if (allReports.isEmpty()) {
            incidentsTableView.setPlaceholder(new Label("No incidents reported yet."));
        }
    }

    private void updateLoggedInUserLabelAndRoles() {
    if (userService.isLoggedIn()) {
        String username = userService.getLoggedInUsername();
        String role = userService.getCurrentUserRole();
        loggedInUserLabel.setText("Logged in as: " + (username != null ? username : "User") + " - Role: " + (role != null ? role : "N/A"));

        // Basic Role-Based Access Control (RBAC)
        if ("ADMIN".equalsIgnoreCase(role)) {
            manageUsersButton.setVisible(true);
            manageUsersButton.setManaged(true); // So it takes up space when visible
        } else {
            manageUsersButton.setVisible(false);
            manageUsersButton.setManaged(false); // So it doesn't take space when hidden
        }
      

    } else {
        loggedInUserLabel.setText("Not logged in.");
        manageUsersButton.setVisible(false);
        manageUsersButton.setManaged(false);
    }
}

    @FXML
    void handleNewReport(ActionEvent event) {
        System.out.println("New Report button clicked.");
        try {
            // Set appropriate dimensions for the ReportDisasterForm
            App.setRoot("ReportDisasterForm", "DRS - New Disaster Report", 800.0, 830.0); 
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not load the New Report form.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewAllReports(ActionEvent event) { // This acts as a full refresh and clears filters
        System.out.println("View All Reports / Refresh clicked.");
        clearFiltersUI(); // Reset filter UI to "All"
        loadInitialReports(); // Load all reports from the service
        incidentsTableView.refresh(); // Explicitly ask TableView to redraw
        System.out.println("Table refreshed. Displaying all reports. Items: " + incidentsTableView.getItems().size());
    }

    @FXML
    void handleGenerateSitRep(ActionEvent event) {
        System.out.println("Generate Situation Report button clicked - Placeholder.");
        showAlert(Alert.AlertType.INFORMATION, "Feature Placeholder", "Generate Situation Report feature is not yet implemented.");
    }

    @FXML
    void handleApplyFilters(ActionEvent event) {
        String type = disasterTypeFilterComboBox.getValue();
        String priority = priorityFilterComboBox.getValue();
        String status = statusFilterComboBox.getValue();
        String keyword = keywordSearchField.getText(); // Get keyword

        System.out.println("Applying filters: Type=" + type + ", Priority=" + priority + ", Status=" + status + ", Keyword=" + keyword);
        // Call the updated ReportService method
        ObservableList<DisasterReport> filteredList = reportService.getFilteredReports(type, priority, status, keyword);
        loadReportsIntoTable(filteredList);
        if (filteredList.isEmpty()) {
            incidentsTableView.setPlaceholder(new Label("No incidents match the current filter/search criteria."));
        }
        System.out.println("Filters applied. Displaying items: " + filteredList.size());
    }

    @FXML
    void handleClearFilters(ActionEvent event) {
        System.out.println("Clear Filters button clicked.");
        keywordSearchField.clear(); // Clear keyword field
        clearFiltersUI(); // Clears ComboBoxes
        loadInitialReports(); // Load all reports (which internally calls service's getAllReports)
        incidentsTableView.refresh();
    }
    
    @FXML
    void handleManageUsers(ActionEvent event) {
        System.out.println("Manage Users button clicked (Admin Only Action).");
        showAlert(Alert.AlertType.INFORMATION, "Admin Action",
                "User Management Feature Placeholder",
                "This feature would allow administrators to manage user accounts.");
    }

    private void clearFiltersUI() {
        disasterTypeFilterComboBox.setValue("All");
        priorityFilterComboBox.setValue("All");
        statusFilterComboBox.setValue("All");
    }

    private void navigateToIncidentDetails(DisasterReport report) {
        if (report == null) return;
        System.out.println("Navigating to details for incident ID: " + report.getId());
        try {
            IncidentDetailsController.setCurrentIncidentId(report.getId()); // Set the ID for the next controller
            App.setRoot("IncidentDetails", "DRS - Incident Details (ID: " + report.getId() + ")", 1000.0, 700.0); // Adjust size
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not load Incident Details form.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked.");
        userService.logout(); // Clear current user session in UserService
        try {
            App.setRoot("Login", "DRS - Login", App.INITIAL_WINDOW_WIDTH, App.INITIAL_WINDOW_HEIGHT);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not load the Login screen.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleMenuFileExit(ActionEvent event) {
        System.out.println("Menu File > Exit clicked.");
        Platform.exit(); // Correct way to close a JavaFX application
    }

    @FXML
    void handleMenuViewRefresh(ActionEvent event) {
        System.out.println("Menu View > Refresh Data clicked.");
        // This is similar to viewAllReports, ensures we have the latest from the service
        // and re-applies current filters if any were set, or shows all.
        // For simplicity here, let's re-apply filters which internally uses the service.
        // If no filters are set (all are "All"), it effectively shows all data.
        handleApplyFilters(null); // Re-apply current filters (which fetches from service)
        incidentsTableView.refresh();
        showAlert(Alert.AlertType.INFORMATION, "Data Refreshed", "Incident data has been refreshed based on current filters.");
    }

    @FXML
    void handleMenuHelpAbout(ActionEvent event) {
        System.out.println("Menu Help > About clicked.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About DRS");
        alert.setHeaderText("Disaster Response System - Initial Prototype");
        alert.setContentText("Version: A2 Prototype\nDeveloped by: Shubh0"); // Customize as needed
        alert.showAndWait();
    }

    // Helper methods for alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text for simple messages
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Maybe some owner setting logic here...

        alert.showAndWait();
    }

    private void loadReportsIntoTable(ObservableList<DisasterReport> reports) {
        incidentsTableView.setItems(reports); // This line should be fine
        if (reports.isEmpty()) {
            incidentsTableView.setPlaceholder(new Label("No incidents to display.")); // This should also be fine
        } else {
            incidentsTableView.setPlaceholder(null); // Clear placeholder if there's data
        }
       
    }
}