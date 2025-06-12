/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.client;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.shared.MasterResource;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ResourceDashboardController implements Initializable {

    @FXML private TableView<MasterResource> resourcesTableView;
    @FXML private TableColumn<MasterResource, String> colUnitName;
    @FXML private TableColumn<MasterResource, String> colUnitType;
    @FXML private TableColumn<MasterResource, String> colHomeStation;
    @FXML private TableColumn<MasterResource, String> colStatus;

    private ClientController clientController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientController = ClientController.getInstance();
        
        resourcesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        setupTableColumns();
        loadResources();
    }

    private void setupTableColumns() {
        colUnitName.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        colUnitType.setCellValueFactory(new PropertyValueFactory<>("unitType"));
        colHomeStation.setCellValueFactory(new PropertyValueFactory<>("homeStation"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("currentStatus"));
    }

    private void loadResources() {
        List<MasterResource> resourceList = clientController.getAllMasterResources();
        resourcesTableView.setItems(FXCollections.observableArrayList(resourceList));
    }
    
    @FXML
    void handleBackToDashboard(ActionEvent event) {
        try {
            App.setRoot("Dashboard", "DRS - Dashboard", 1080.0, 700.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}