module com.mycompany.drs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.drs to javafx.fxml;
    opens com.mycompany.drs.model to javafx.base;
    
    exports com.mycompany.drs;
}
