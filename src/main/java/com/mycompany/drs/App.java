package com.mycompany.drs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.io.IOException;

import service.ReportService;
import service.UserService; 

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage; // Keep a reference to the primary stage

    // Define a common initial window size (can be used by controllers)
    public static final double INITIAL_WINDOW_WIDTH = 600.0;
    public static final double INITIAL_WINDOW_HEIGHT = 400.0;

    // --- Shared Service Instances ---
    private static UserService userServiceInstance;
    private static ReportService reportServiceInstance;
    // --- End Shared Service Instances ---


    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // --- Initialize Shared Services ONCE ---
        userServiceInstance = new service.UserService(); 
        reportServiceInstance = new service.ReportService(); // Assuming this package
        // --- End Initialize Shared Services ---

        Parent welcomeRoot = loadFXML("Welcome"); // Assumes Welcome.fxml is in com/mycompany/drs/
        scene = new Scene(welcomeRoot, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);

        primaryStage.setTitle("DRS - Welcome");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // --- Getter methods for Shared Service Instances ---
    public static UserService getUserService() {
        if (userServiceInstance == null) {
            // This is a fallback, ideally it's always initialized in start()
            // but good for robustness if a controller tries to access it too early
            // though that shouldn't happen with JavaFX lifecycle.
            userServiceInstance = new service.UserService();
        }
        return userServiceInstance;
    }

    public static ReportService getReportService() {
        if (reportServiceInstance == null) {
            // Fallback initialization
            reportServiceInstance = new service.ReportService();
        }
        return reportServiceInstance;
    }
    // --- End Getter methods ---


    public static void setRoot(String fxmlBaseName, String title, double width, double height) throws IOException {
        Parent root = loadFXML(fxmlBaseName);
        scene.setRoot(root);

        if (primaryStage != null) {
            primaryStage.setTitle(title);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
        }
    }

    public static void setRoot(String fxmlBaseName) throws IOException {
        scene.setRoot(loadFXML(fxmlBaseName));
    }

    private static Parent loadFXML(String fxmlBaseName) throws IOException {
        String fxmlPath = fxmlBaseName + ".fxml";
        URL fxmlUrl = App.class.getResource(fxmlPath);

        if (fxmlUrl == null) {
            System.err.println("Cannot find FXML file: " + fxmlPath + " (relative to " + App.class.getPackage().getName() + ")");
                throw new IOException("Cannot load FXML file: " + fxmlPath + ". Checked package path and classpath root.");
       
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}