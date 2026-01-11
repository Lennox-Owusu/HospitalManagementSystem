
package com.amalitech.hospitalmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainDashboardController {
    @FXML private TabPane tabPane;

    @FXML public void switchToPatients()    { tabPane.getSelectionModel().select(0); }
    @FXML public void switchToDoctors()     { tabPane.getSelectionModel().select(1); }
    @FXML public void switchToDepartments() { tabPane.getSelectionModel().select(2); }
    @FXML public void switchToAppointments() { tabPane.getSelectionModel().select(3); } // adjust index
}
