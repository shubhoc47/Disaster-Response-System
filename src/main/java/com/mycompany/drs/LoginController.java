package com.mycompany.drs; 

// Assuming UserService is in the same package or a subpackage like 'service'
//import com.mycompany.drs.service.UserService; // If in service subpackage
// import com.mycompany.drs.UserService; // If in the same package

import service.UserService;
import service.UserService; 

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Hyperlink forgotPasswordLink;

    private UserService userService; // This will hold the shared instance

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get the shared UserService instance from App.java
        this.userService = App.getUserService(); // KEY CHANGE HERE

        messageLabel.setText(""); // Clear any previous messages

      
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.fire(); // Programmatically "click" the login button
                event.consume();    // Consume the event
            }
        });

       
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
                event.consume();
            }
        });

        System.out.println("LoginController initialized and using shared UserService.");
    }


    @FXML
    void handlePasswordKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loginButton.fire();
            event.consume();
        }
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        performLoginLogic();
    }

    private void performLoginLogic() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showMessage("Username cannot be empty.", true);
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showMessage("Password cannot be empty.", true);
            passwordField.requestFocus();
            return;
        }

        // Use the shared userService instance
        boolean isAuthenticated = userService.authenticate(username, password);

        if (isAuthenticated) {
            showMessage("Login Successful!", false);
            String role = userService.getCurrentUserRole(); // Get role from shared service
            String loggedInUsername = userService.getLoggedInUsername(); // Get username from shared service
            System.out.println("Login successful for: " + loggedInUsername + " with Role: " + role);
      
        
            try {
                // Use the App.INITIAL_WINDOW_WIDTH/HEIGHT for consistency or specific Dashboard sizes
                App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0); // Navigate to Dashboard.fxml
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Critical Error: Could not load the dashboard.", true);
            }
        } else {
            showMessage("Invalid username or password. Please try again.", true);
            passwordField.clear();
            passwordField.requestFocus();
        }
    }


    @FXML
    void handleForgotPassword(ActionEvent event) {
        showMessage("Forgot Password feature is not implemented in this prototype.", false);
        System.out.println("Forgot Password link clicked.");
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        if (isError) {
            messageLabel.setTextFill(Color.RED);
        } else {
            messageLabel.setTextFill(Color.GREEN);
        }
    }
}