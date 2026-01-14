
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.service.*;
import com.amalitech.hospitalmanagementsystem.service.impl.*;
import com.amalitech.hospitalmanagementsystem.dao.impl.*;

import com.amalitech.hospitalmanagementsystem.model.*;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class OverviewDashboardController {

    @FXML private Label lblTotalPatients;
    @FXML private Label lblTotalDoctors;
    @FXML private Label lblTotalDepartments;
    @FXML private Label lblTodayAppointments;

    @FXML private Label lblAverageRating;
    @FXML private Label lblTotalFeedback;
    @FXML private Label lblLowStockCount;

    private final PatientService patientService =
            new PatientServiceImpl(new PatientDaoImpl());
    private final DoctorService doctorService =
            new DoctorServiceImpl(new DoctorDaoImpl());
    private final DepartmentDaoImpl departmentDao = new DepartmentDaoImpl();
    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final PatientFeedbackService feedbackService = new PatientFeedbackServiceImpl();
    private final InventoryService inventoryService = new InventoryServiceImpl();

    @FXML
    public void initialize() {
        Platform.runLater(this::loadStats);
    }

    private void loadStats() {
        lblTotalPatients.setText(String.valueOf(patientService.getAll().size()));
        lblTotalDoctors.setText(String.valueOf(doctorService.getAll().size()));
        lblTotalDepartments.setText(String.valueOf(departmentDao.findAll().size()));

        // Today’s appointments
        int todayCount = appointmentService.findByDate(LocalDate.now()).size();
        lblTodayAppointments.setText(String.valueOf(todayCount));

        // Feedback average
        var allFeedback = feedbackService.getAll();
        lblTotalFeedback.setText(allFeedback.size() + " feedback entries");

        double avg = 0;
        if (!allFeedback.isEmpty()) {
            avg = allFeedback.stream()
                    .filter(f -> f.getRating() != null)
                    .mapToInt(PatientFeedback::getRating)
                    .average().orElse(0);
        }
        lblAverageRating.setText(String.format("%.1f", avg));

        // Low stock inventory
        long lowStock = inventoryService.getAll().stream()
                .filter(i -> i.getQuantity() <= i.getReorderLevel())
                .count();
        lblLowStockCount.setText(String.valueOf(lowStock));
    }
}
