
module com.amalitech.hospitalmanagementsystem {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // 3rd‑party JavaFX libs (you included these in pom.xml)
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // JDBC / Logging / Pool
    requires java.sql;
    requires org.slf4j;
    requires com.zaxxer.hikari;

    // ---- Reflection access for FXML & TableView ----
    // Allow FXMLLoader to access controller classes
    opens com.amalitech.hospitalmanagementsystem.controller to javafx.fxml;

    // Allow JavaFX to reflect on model getters/setters (TableView PropertyValueFactory)
    opens com.amalitech.hospitalmanagementsystem.model to javafx.base;

    // If you still use FXML in the root package (e.g., HelloController), keep this:
    opens com.amalitech.hospitalmanagementsystem to javafx.fxml;


    // Expose your root package (application entry points, etc.)
    exports com.amalitech.hospitalmanagementsystem;
}
