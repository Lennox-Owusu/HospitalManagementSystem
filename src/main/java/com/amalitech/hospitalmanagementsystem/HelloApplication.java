
package com.amalitech.hospitalmanagementsystem;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;


import java.util.Objects;
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/view/main_dashboard.fxml"))
        );

        Scene scene = new Scene(root);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void stop() {
        System.out.println("Closing MongoDB connection...");
        com.amalitech.hospitalmanagementsystem.nosql.MongoConnectionUtil.shutdown();
    }


}
