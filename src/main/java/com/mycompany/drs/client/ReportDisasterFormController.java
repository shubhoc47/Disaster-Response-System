/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.client;

/**
 *
 * @author shubh
 */

import com.mycompany.drs.shared.DisasterReport;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class ReportDisasterFormController implements Initializable {
    // --- FXML UI Elements ---
    @FXML private ComboBox<String> disasterTypeComboBox;
    @FXML private DatePicker reportDatePicker;
    @FXML private TextField reportTimeField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextArea descriptionTextArea;
    @FXML private ComboBox<String> initialSeverityComboBox;
    // Add other FXML fields if you use them, like latitudeField, peopleAffectedField, etc.
    
    private ClientController clientController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientController = ClientController.getInstance();

        // Populate ComboBoxes
        disasterTypeComboBox.setItems(FXCollections.observableArrayList(
                "Fire", "Earthquake", "Hurricane", "Flood", "Chemical Spill", "Other"));
        initialSeverityComboBox.setItems(FXCollections.observableArrayList(
                "Low", "Medium", "High", "Critical"));
        
        // Set default values
        handleClearForm(null);
    }

    @FXML
    void handleSubmitReport(ActionEvent event) {
        // --- 1. Validate Inputs ---
        String type = disasterTypeComboBox.getValue();
        LocalDate date = reportDatePicker.getValue();
        String timeStr = reportTimeField.getText();
        String address = addressField.getText().trim();

        if (type == null || date == null || timeStr.trim().isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields (*).");
            return;
        }

        LocalTime time;
        try {
            time = LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Time format is invalid. Please use HH:mm.");
            return;
        }

        // --- 2. Create DisasterReport Object ---
        String location = addressField + ", " + cityField.getText().trim();
        String priority = initialSeverityComboBox.getValue() != null ? initialSeverityComboBox.getValue() : "Low";
        LocalDateTime reportedDateTime = LocalDateTime.of(date, time);

        DisasterReport newReport = new DisasterReport(type, location, priority, "Reported", reportedDateTime);

        // --- 3. Call ClientController to send the report to the server ---
        DisasterReport submittedReport = clientController.submitNewReport(newReport);

        if (submittedReport != null && submittedReport.getId() > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Report submitted successfully with new ID: " + submittedReport.getId());
            handleCancel(null); // Go back to dashboard
        } else {
            showAlert(Alert.AlertType.ERROR, "Submission Failed", "Could not submit the report to the server.");
        }
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        disasterTypeComboBox.setValue(null);
        reportDatePicker.setValue(LocalDate.now());
        reportTimeField.setText(LocalTime.now().toString().substring(0, 5));
        addressField.clear();
        cityField.clear();
        descriptionTextArea.clear();
        initialSeverityComboBox.setValue(null);
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLocateOnMap(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Feature Placeholder", "Locate on Map feature is not yet implemented.");
    }

    @FXML
    void handleUploadFiles(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Feature Placeholder", "File Upload feature is not yet implemented.");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}