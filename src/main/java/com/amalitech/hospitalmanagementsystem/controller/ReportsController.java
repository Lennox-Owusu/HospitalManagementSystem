
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Appointment;
import com.amalitech.hospitalmanagementsystem.service.PatientService;
import com.amalitech.hospitalmanagementsystem.service.DoctorService;
import com.amalitech.hospitalmanagementsystem.service.AppointmentService;
import com.amalitech.hospitalmanagementsystem.service.impl.PatientServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.DoctorServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.AppointmentServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.application.Platform;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportsController {
    @FXML private PieChart chartPatientsByGender;
    @FXML private BarChart<String, Number> chartDoctorsPerDept;
    @FXML private LineChart<String, Number> chartAppointmentsTrend;

    private final PatientService patientService = new PatientServiceImpl(new com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl());
    private final DoctorService doctorService = new DoctorServiceImpl(new com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl());
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    @FXML
    public void initialize() {
        refreshReports();
    }

    @FXML
    public void refreshReports() {
        Platform.runLater(() -> {
            loadPatientsByGender();
            loadDoctorsPerDept();
            loadAppointmentsTrend();
        });
    }

    private void loadPatientsByGender() {
        Map<String, Integer> genderCounts = new HashMap<>();
        genderCounts.put("M", 0);
        genderCounts.put("F", 0);
        genderCounts.put("Other", 0);

        for (Patient p : patientService.getAll()) {
            String g = p.getGender() == null ? "Other" : p.getGender().trim();
            if (!genderCounts.containsKey(g)) g = "Other";
            genderCounts.put(g, genderCounts.get(g) + 1);
        }

        chartPatientsByGender.getData().clear();
        genderCounts.forEach((k, v) -> {
            if (v > 0) chartPatientsByGender.getData().add(new PieChart.Data(k, v));
        });
    }

    private void loadDoctorsPerDept() {
        Map<String, Integer> deptCounts = new TreeMap<>();
        doctorService.getAll().forEach(d -> {
            String deptName = (d.getDepartmentId() == null) ? "Unassigned" : "Dept " + d.getDepartmentId();
            deptCounts.put(deptName, deptCounts.getOrDefault(deptName, 0) + 1);
        });

        chartDoctorsPerDept.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doctors");
        deptCounts.forEach((dept, count) -> series.getData().add(new XYChart.Data<>(dept, count)));
        chartDoctorsPerDept.getData().add(series);
    }

    private void loadAppointmentsTrend() {
        Map<String, Integer> dateCounts = new TreeMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Appointment a : appointmentService.getAll()) {
            String date = a.getAppointmentDate().toLocalDate().format(fmt);
            dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);
        }

        chartAppointmentsTrend.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments");
        dateCounts.forEach((date, count) -> series.getData().add(new XYChart.Data<>(date, count)));
        chartAppointmentsTrend.getData().add(series);
    }
}
