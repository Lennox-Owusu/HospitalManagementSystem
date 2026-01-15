
module com.amalitech.hospitalmanagementsystem {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.kordamp.ikonli.fontawesome5;



    // 3rd‑party JavaFX libs
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
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;


    //Reflection access for FXML & TableView
    // Allow FXMLLoader to access controller classes
    opens com.amalitech.hospitalmanagementsystem.controller to javafx.fxml;

    // TableView PropertyValueFactory
    opens com.amalitech.hospitalmanagementsystem.model to javafx.base;

    // If you still use FXML in the root package (e.g., HelloController), keep this:
    opens com.amalitech.hospitalmanagementsystem to javafx.fxml;


    // application entry points
    exports com.amalitech.hospitalmanagementsystem;
    exports com.amalitech.hospitalmanagementsystem.controller;
}
