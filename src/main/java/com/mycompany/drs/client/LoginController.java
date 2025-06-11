package com.mycompany.drs.client; 

// Assuming UserService is in the same package or a subpackage like 'service'
//import com.mycompany.drs.service.UserService; // If in service subpackage
// import com.mycompany.drs.UserService; // If in the same package

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    private ClientController clientController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get the single instance of our connection manager
        this.clientController = ClientController.getInstance();

        messageLabel.setText("");

        // Listener to allow pressing Enter to log in
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performLoginLogic();
            }
        });
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        performLoginLogic();
    }
    
    @FXML
    void handleForgotPassword(ActionEvent event) {
        // For now, just show a message that the feature is not implemented.
        showMessage("Forgot Password feature is not implemented in this prototype.", false);
        System.out.println("CLIENT: Forgot Password link clicked.");
    }

    private void performLoginLogic() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Username and password cannot be empty.", true);
            return;
        }

        // Use the ClientController to send the login request to the server
        boolean isAuthenticated = clientController.login(username, password);

        if (isAuthenticated) {
            showMessage("Login Successful!", false);
            System.out.println("CLIENT: Login successful for: " + clientController.getLoggedInUsername()
                             + " with Role: " + clientController.getCurrentUserRole());
            try {
                App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0);
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Critical Error: Could not load the dashboard.", true);
            }
        } else {
            showMessage("Invalid username or password. Please try again.", true);
            passwordField.clear();
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }
    
    
}