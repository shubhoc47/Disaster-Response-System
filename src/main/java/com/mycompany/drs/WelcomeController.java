/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.drs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
/**
 * FXML Controller class
 *
 * @author shubh
 */
public class WelcomeController implements Initializable {


 @FXML
    private ImageView logoImageView; 
    @FXML
    private Button proceedToLoginButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("WelcomeController initialized.");
        
        proceedToLoginButton.setTooltip(new Tooltip("Click to proceed to the login screen"));
    }

    @FXML
    private void handleProceedToLogin(ActionEvent event) {
    System.out.println("Proceed to Login button clicked! Attempting to navigate...");
    try {
        // Use the App.setRoot method, keeping the window size consistent
        App.setRoot("Login", "DRS - Login", App.INITIAL_WINDOW_WIDTH, App.INITIAL_WINDOW_HEIGHT);
        System.out.println("Successfully initiated navigation to Login screen.");
    } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Error loading Login.fxml: " + e.getMessage());
        }
    }
}