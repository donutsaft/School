module com.example.scrollingplatformer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.scrollingplatformer to javafx.fxml;
    exports com.example.scrollingplatformer;
}