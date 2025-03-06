module com.lanchatapp.lanchatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.lanchatapp.lanchatapp to javafx.fxml;
    exports com.lanchatapp.lanchatapp;
    exports com.lanchatapp.lanchatapp.Controllers to javafx.fxml;
    opens com.lanchatapp.lanchatapp.Controllers to javafx.fxml;

}