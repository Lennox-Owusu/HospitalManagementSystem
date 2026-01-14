
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.service.PatientService;
import com.amalitech.hospitalmanagementsystem.service.DoctorService;
import com.amalitech.hospitalmanagementsystem.service.impl.PatientServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.DoctorServiceImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.DepartmentDaoImpl;

import com.amalitech.hospitalmanagementsystem.service.AppointmentService;
import com.amalitech.hospitalmanagementsystem.service.impl.AppointmentServiceImpl;

import com.amalitech.hospitalmanagementsystem.service.PrescriptionService;
import com.amalitech.hospitalmanagementsystem.service.impl.PrescriptionServiceImpl;

import com.amalitech.hospitalmanagementsystem.service.InventoryService;
import com.amalitech.hospitalmanagementsystem.service.impl.InventoryServiceImpl;

import com.amalitech.hospitalmanagementsystem.util.QueryTimer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class AdminPanelController {

    @FXML private Label lblPatientCount, lblDoctorCount, lblDeptCount;
    @FXML private Label lblPatientQueryMs, lblDoctorQueryMs;
    @FXML private Label lblAppointmentQueryMs, lblPrescriptionQueryMs, lblInventoryQueryMs;
    @FXML private Label lblMemory, lblThreads, lblLastRefresh;

    @FXML private PieChart chartPatientsByGender;
    @FXML private BarChart<String, Number> chartDoctorsPerDept;

    private final PatientService patientService = new PatientServiceImpl(new com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl());
    private final DoctorService doctorService = new DoctorServiceImpl(new com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl());
    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final PrescriptionService prescriptionService = new PrescriptionServiceImpl();
    private final InventoryService inventoryService = new InventoryServiceImpl();

    private final DepartmentDaoImpl departmentDao = new DepartmentDaoImpl();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AdminPanelRefresher");
        t.setDaemon(true);
        return t;
    });

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        refreshAll();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                Map<String, Object> snapshot = collectMetricsSnapshot();
                Platform.runLater(() -> applySnapshot(snapshot));
            } catch (Exception ignored) {}
        }, 10, 10, TimeUnit.SECONDS);
    }

    @FXML
    public void refreshAll() {
        CompletableFuture
                .supplyAsync(this::collectMetricsSnapshot)
                .thenAccept(snapshot -> Platform.runLater(() -> applySnapshot(snapshot)));
    }

    /**
     * ============================
     * METRICS COLLECTION
     * ============================
     */
    private Map<String, Object> collectMetricsSnapshot() {
        Map<String, Object> m = new HashMap<>();

        // Timed queries using QueryTimer
        double patientMs = QueryTimer.measure(() -> patientService.getAll());
        List<Patient> patients = patientService.getAll();

        double doctorMs = QueryTimer.measure(() -> doctorService.getAll());
        List<Doctor> doctors = doctorService.getAll();

        double apptMs = QueryTimer.measure(() -> appointmentService.getAll());
        double prescMs = QueryTimer.measure(() -> prescriptionService.getAll());
        double invMs   = QueryTimer.measure(() -> inventoryService.getAll());

        int deptCount = departmentDao.findAll().size();

        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        int threads = Thread.activeCount();

        m.put("patientCount", patients.size());
        m.put("doctorCount", doctors.size());
        m.put("patientMs", patientMs);
        m.put("doctorMs", doctorMs);
        m.put("appointmentMs", apptMs);
        m.put("prescriptionMs", prescMs);
        m.put("inventoryMs", invMs);

        m.put("deptCount", deptCount);
        m.put("usedMb", usedMb);
        m.put("threads", threads);
        m.put("timestamp", LocalDateTime.now().format(TS_FMT));

        m.put("genderBreakdown", countPatientsByGender(patients));
        m.put("doctorsPerDept", countDoctorsPerDepartment(doctors));

        return m;
    }

    /**
     * ============================
     * APPLY SNAPSHOT TO UI
     * ============================
     */
    private void applySnapshot(Map<String, Object> s) {
        lblPatientCount.setText("Patients: " + s.get("patientCount"));
        lblDoctorCount.setText("Doctors: " + s.get("doctorCount"));
        lblDeptCount.setText("Departments: " + s.get("deptCount"));

        lblPatientQueryMs.setText(String.format("Patient Query: %.2f ms", s.get("patientMs")));
        lblDoctorQueryMs.setText(String.format("Doctor Query: %.2f ms", s.get("doctorMs")));

        lblAppointmentQueryMs.setText("Appointment Query: " + String.format("%.2f ms", s.get("appointmentMs")));
        lblPrescriptionQueryMs.setText("Prescription Query: " + String.format("%.2f ms", s.get("prescriptionMs")));
        lblInventoryQueryMs.setText("Inventory Query: " + String.format("%.2f ms", s.get("inventoryMs")));

        lblMemory.setText("Memory Usage: " + s.get("usedMb") + " MB");
        lblThreads.setText("Active Threads: " + s.get("threads"));
        lblLastRefresh.setText("Last Refresh: " + s.get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> genderCounts = (Map<String, Integer>) s.get("genderBreakdown");
        populateGenderPie(genderCounts);

        @SuppressWarnings("unchecked")
        Map<String, Integer> perDept = (Map<String, Integer>) s.get("doctorsPerDept");
        populateDoctorsBar(perDept);
    }

    /**
     * ============================
     * DATA HELPERS
     * ============================
     */
    private Map<String, Integer> countPatientsByGender(List<Patient> patients) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("M", 0);
        map.put("F", 0);
        map.put("Other", 0);

        for (Patient p : patients) {
            String g = p.getGender() == null ? "Other" : p.getGender().trim();
            if (!map.containsKey(g)) g = "Other";
            map.put(g, map.get(g) + 1);
        }
        return map;
    }

    private Map<String, Integer> countDoctorsPerDepartment(List<Doctor> doctors) {
        Map<String, Integer> out = new TreeMap<>();
        departmentDao.findAll().forEach(d -> out.put(d.getName(), 0));

        for (Doctor d : doctors) {
            String deptName = (d.getDepartmentId() == null) ? "Unassigned" : "Dept " + d.getDepartmentId();
            out.put(deptName, out.getOrDefault(deptName, 0) + 1);
        }
        return out;
    }

    private void populateGenderPie(Map<String, Integer> counts) {
        chartPatientsByGender.getData().clear();
        counts.forEach((k, v) -> {
            if (v > 0) chartPatientsByGender.getData().add(new PieChart.Data(k, v));
        });
    }

    private void populateDoctorsBar(Map<String, Integer> perDept) {
        chartDoctorsPerDept.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doctors");

        perDept.forEach((dept, count) -> series.getData().add(new XYChart.Data<>(dept, count)));
        chartDoctorsPerDept.getData().add(series);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
