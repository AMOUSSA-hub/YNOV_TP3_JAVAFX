module com.example.tp3 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.tp3 to javafx.fxml;
    exports com.example.tp3;
}
