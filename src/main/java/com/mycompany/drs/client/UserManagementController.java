/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.client;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.shared.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Boolean> colActive;
    
    private ClientController clientController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientController = ClientController.getInstance();
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));
    }

    private void loadUsers() {
        List<User> userList = clientController.getAllUsers();
        usersTableView.setItems(FXCollections.observableArrayList(userList));
    }
    
    @FXML
    void handleBackToDashboard(ActionEvent event) {
        try {
            App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddUser(ActionEvent event) {
        showUserDialog(null); // Pass null to indicate a new user
    }

    @FXML
    void handleEditUser(ActionEvent event) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to edit.");
            return;
        }
        showUserDialog(selectedUser); // Pass the selected user to pre-fill the form
    }

    @FXML
    void handleDeactivateUser(ActionEvent event) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to deactivate.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to deactivate user '" + selectedUser.getUsername() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = clientController.deleteUser(selectedUser.getUserId());
                if (success) {
                    loadUsers(); // Refresh the table
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to deactivate user on the server.");
                }
            }
        });
    }
    
    private void showUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Add New User" : "Edit User");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "DISPATCHER", "REPORTER"));
        CheckBox activeCheckBox = new CheckBox("Is Active");

        usernameField.setText(user != null ? user.getUsername() : "");
        passwordField.setPromptText(user != null ? "(Unchanged)" : "Required");
        roleComboBox.setValue(user != null ? user.getRole() : "REPORTER");
        activeCheckBox.setSelected(user != null ? user.isActive() : true);
        
        grid.add(new Label("Username:"), 0, 0); grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passwordField, 1, 1);
        grid.add(new Label("Role:"), 0, 2); grid.add(roleComboBox, 1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(activeCheckBox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                User resultUser = (user == null) ? new User() : user;
                resultUser.setUsername(usernameField.getText());
                resultUser.setRole(roleComboBox.getValue());
                resultUser.setActive(activeCheckBox.isSelected());
                if (!passwordField.getText().isEmpty()) {
                    resultUser.setPassword(passwordField.getText()); // Only set if provided
                }
                return resultUser;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(u -> {
            boolean success;
            if (user == null) { // This is a new user
                success = clientController.addUser(u);
            } else { // This is an existing user
                success = clientController.updateUser(u);
            }
            if (success) loadUsers();
            else showAlert(Alert.AlertType.ERROR, "Error", "Operation failed on the server.");
        });
    }
    
    private void showAlert(Alert.AlertType type, String title, String msg) {
        new Alert(type, msg).showAndWait();
    }
}