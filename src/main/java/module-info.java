module com.mycompany.drs {
    // Standard requirements for JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    // --- YOUR FIX IS HERE ---

    // 1. OPEN the 'client' package to the FXML library.
    // This allows the FXML loader to access your @FXML annotated fields in your controllers.
    opens com.mycompany.drs.client to javafx.fxml;

    // 2. OPEN the 'model' package to the JavaFX base library.
    // This allows the TableView's PropertyValueFactory to access the SimpleStringProperty, etc., in your model classes.
    opens com.mycompany.drs.shared to javafx.base;

    // 3. EXPORT the 'client' package.
    // This makes your main App class (App.java) visible so it can be launched.
    exports com.mycompany.drs.client;
}