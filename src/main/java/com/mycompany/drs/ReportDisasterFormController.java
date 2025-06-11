/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs;

/**
 *
 * @author shubh
 */

import com.mycompany.drs.model.DisasterReport; // Assuming model package
import service.ReportService;  // Assuming service package

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
// No need for javafx.scene.paint.Color here if using Alerts

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class ReportDisasterFormController implements Initializable {

    // --- FXML UI Elements ---
    @FXML private ComboBox<String> disasterTypeComboBox;
    @FXML private DatePicker reportDatePicker;
    @FXML private TextField reportTimeField; // Expects HH:mm
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button locateOnMapButton;
    @FXML private TextArea descriptionTextArea;
    @FXML private TextField reportSourceField;
    @FXML private TextField reportedByField;
    @FXML private ComboBox<String> initialSeverityComboBox;
    @FXML private TextField peopleAffectedField;
    @FXML private Button uploadFilesButton;
    @FXML private Button submitReportButton;
    @FXML private Button clearFormButton;
    @FXML private Button cancelButton;

    // --- Services ---
    private ReportService reportService;

    // Formatter for time input/output
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get shared service instance from App.java
        reportService = App.getReportService();

        // Populate ComboBoxes
        disasterTypeComboBox.setItems(FXCollections.observableArrayList(
                "Fire", "Earthquake", "Hurricane", "Flood", "Chemical Spill", "Landslide", "Tornado", "Other"
        ));
        initialSeverityComboBox.setItems(FXCollections.observableArrayList(
                "Low", "Medium", "High", "Critical"
        ));

        // Set default values for date and time
        resetDateTimeDefaults();

        // Add listener to peopleAffectedField to allow only numbers (basic)
        peopleAffectedField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                peopleAffectedField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Add listener to latitude/longitude to allow numbers and decimal point (basic)
        latitudeField.textProperty().addListener((obs, ov, nv) -> {
            if (!nv.matches("-?\\d*\\.?\\d*")) { // Allows negative, digits, one optional dot, then digits
                latitudeField.setText(ov);
            }
        });
        longitudeField.textProperty().addListener((obs, ov, nv) -> {
            if (!nv.matches("-?\\d*\\.?\\d*")) {
                longitudeField.setText(ov);
            }
        });


        System.out.println("ReportDisasterFormController initialized.");
    }

    private void resetDateTimeDefaults() {
        reportDatePicker.setValue(LocalDate.now());
        reportTimeField.setText(LocalTime.now().format(TIME_FORMATTER));
    }

    @FXML
    void handleSubmitReport(ActionEvent event) {
        // --- 1. Validate Inputs ---
        StringBuilder errors = new StringBuilder();

        String type = disasterTypeComboBox.getValue();
        LocalDate date = reportDatePicker.getValue();
        String timeStr = reportTimeField.getText();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String description = descriptionTextArea.getText().trim();

        // Optional fields (get them but don't necessarily validate for emptiness unless business logic requires)
        String source = reportSourceField.getText().trim();
        String reportedBy = reportedByField.getText().trim();
        String severity = initialSeverityComboBox.getValue();
        String peopleAffectedStr = peopleAffectedField.getText().trim();
        String latitudeStr = latitudeField.getText().trim();
        String longitudeStr = longitudeField.getText().trim();

        // Mandatory field checks
        if (type == null || type.isEmpty()) {
            errors.append("- Disaster Type is required.\n");
        }
        if (date == null) {
            errors.append("- Report Date is required.\n");
        }
        LocalTime time = null;
        if (timeStr == null || timeStr.isEmpty()) {
            errors.append("- Report Time is required.\n");
        } else {
            try {
                time = LocalTime.parse(timeStr, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                errors.append("- Report Time format is invalid (must be HH:mm).\n");
            }
        }
        if (address.isEmpty()) {
            errors.append("- Address/Location Description is required.\n");
        }
        if (city.isEmpty()) {
            errors.append("- City/Area is required.\n");
        }
        if (description.isEmpty()) {
            errors.append("- Description is required.\n");
        }

        // Validate optional numeric/coordinate fields if they are not empty
        if (!peopleAffectedStr.isEmpty()) {
            try {
                Integer.parseInt(peopleAffectedStr); // Just check if it's a valid integer
            } catch (NumberFormatException e) {
                errors.append("- Estimated People Affected must be a whole number.\n");
            }
        }
        if (!latitudeStr.isEmpty()) {
            try {
                Double.parseDouble(latitudeStr); // Check for valid double
            } catch (NumberFormatException e) {
                errors.append("- Latitude must be a valid number (e.g., 34.0522).\n");
            }
        }
        if (!longitudeStr.isEmpty()) {
            try {
                Double.parseDouble(longitudeStr); // Check for valid double
            } catch (NumberFormatException e) {
                errors.append("- Longitude must be a valid number (e.g., -118.2437).\n");
            }
        }


        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please correct the following errors:", errors.toString());
            return;
        }

        // --- 2. Create DisasterReport Object ---
        LocalDateTime reportedDateTime = LocalDateTime.of(date, time);
        String locationSummary = address + (city.isEmpty() ? "" : ", " + city);

        // Determine priority based on severity (example logic)
        String priorityLevel = "Low"; // Default
        if (severity != null) {
            switch (severity) {
                case "Critical": priorityLevel = "Critical"; break;
                case "High": priorityLevel = "High"; break;
                case "Medium": priorityLevel = "Medium"; break;
            }
        }

        // Assuming DisasterReport constructor takes: type, location, priority, status, reportedTime
        DisasterReport newReport = new DisasterReport(
                type,
                locationSummary,
                priorityLevel,
                "Reported",      // Default status for new reports
                reportedDateTime
        );


        // --- 3. Call reportService.addReport() ---
        reportService.addReport(newReport);

        // --- 4. Show a success message ---
        showAlert(Alert.AlertType.INFORMATION, "Report Submitted",
                "Disaster report submitted successfully!",
                "ID: " + newReport.getId() + "\nType: " + newReport.getType() + "\nLocation: " + locationSummary);

        // --- 5. Clear the form or navigate back ---
        handleClearForm(null); // Clear the form for the next entry
        // OR, navigate back to the dashboard:
        // handleCancel(null);

        System.out.println("New Report Submitted: ID " + newReport.getId());
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        disasterTypeComboBox.setValue(null); // Clears selection
        disasterTypeComboBox.getSelectionModel().clearSelection(); // More robust clear
        resetDateTimeDefaults(); // Resets date and time to current
        addressField.clear();
        cityField.clear();
        latitudeField.clear();
        longitudeField.clear();
        descriptionTextArea.clear();
        reportSourceField.clear();
        reportedByField.clear();
        initialSeverityComboBox.setValue(null);
        initialSeverityComboBox.getSelectionModel().clearSelection();
        peopleAffectedField.clear();

        // Scroll to top (if inside a ScrollPane, this helps UI)
        // if (addressField.getScene() != null && addressField.getScene().getRoot() instanceof ScrollPane) {
        //     ((ScrollPane)addressField.getScene().getRoot()).setVvalue(0.0);
        // }
        disasterTypeComboBox.requestFocus(); // Set focus to the first field
        System.out.println("Form cleared.");
    }

    @FXML
    void handleCancel(ActionEvent event) {
        System.out.println("Cancel button clicked. Navigating back to Dashboard.");
        try {
            App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0); // Use dashboard dimensions
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not return to the dashboard.", e.getMessage());
        }
    }

    @FXML
    void handleLocateOnMap(ActionEvent event) {
        System.out.println("Locate on Map button clicked - Placeholder.");
        showAlert(Alert.AlertType.INFORMATION, "Feature Placeholder",
                "Locate on Map feature is not yet implemented.", "");
    }

    @FXML
    void handleUploadFiles(ActionEvent event) {
        System.out.println("Upload Files button clicked - Placeholder.");
        showAlert(Alert.AlertType.INFORMATION, "Feature Placeholder",
                "File Upload feature is not yet implemented.", "");
    }

    // Helper method for showing alerts
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        // Set an owner if possible for better modality behavior
        // if (submitReportButton != null && submitReportButton.getScene() != null && submitReportButton.getScene().getWindow() != null) {
        //    alert.initOwner(submitReportButton.getScene().getWindow());
        // }
        alert.showAndWait();
    }
}