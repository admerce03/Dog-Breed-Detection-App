module com.example.dogbreeddetector {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.net.http;

    opens com.example.dogbreeddetector to javafx.fxml;
    exports com.example.dogbreeddetector;
}