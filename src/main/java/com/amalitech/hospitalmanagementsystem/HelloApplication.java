
package com.amalitech.hospitalmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 650);
        stage.setTitle("Hospital Management System - Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
