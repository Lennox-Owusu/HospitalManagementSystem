
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Appointment;
import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import com.amalitech.hospitalmanagementsystem.model.InventoryItem;

import com.amalitech.hospitalmanagementsystem.service.PatientService;
import com.amalitech.hospitalmanagementsystem.service.DoctorService;
import com.amalitech.hospitalmanagementsystem.service.AppointmentService;
import com.amalitech.hospitalmanagementsystem.service.PatientFeedbackService;
import com.amalitech.hospitalmanagementsystem.service.InventoryService;

import com.amalitech.hospitalmanagementsystem.service.impl.PatientServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.DoctorServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.AppointmentServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.PatientFeedbackServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.InventoryServiceImpl;

import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.application.Platform;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController {

    // charts
    @FXML private PieChart chartPatientsByGender;
    @FXML private BarChart<String, Number> chartDoctorsPerDept;
    @FXML private LineChart<String, Number> chartAppointmentsTrend;

    // charts
    @FXML private PieChart chartFeedbackRatings;
    @FXML private LineChart<String, Number> chartFeedbackTrend; // (optional: not used in FXML now)
    @FXML private BarChart<String, Number> chartLowStockByCategory;
    @FXML private BarChart<String, Number> chartTopLowStockItems;

    // Services
    private final PatientService patientService = new PatientServiceImpl(new PatientDaoImpl());
    private final DoctorService doctorService = new DoctorServiceImpl(new DoctorDaoImpl());
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    // services
    private final PatientFeedbackService feedbackService = new PatientFeedbackServiceImpl();
    private final InventoryService inventoryService = new InventoryServiceImpl();

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

            loadFeedbackRatings();
            loadFeedbackTrend();

            loadInventoryLowStockByCategory();
            loadTopLowStockItems();
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
        genderCounts.forEach((k, v) -> { if (v > 0) chartPatientsByGender.getData().add(new PieChart.Data(k, v)); });
    }

    private void loadDoctorsPerDept() {
        Map<String, Integer> deptCounts = new TreeMap<>();
        for (Doctor d : doctorService.getAll()) {
            String deptName = (d.getDepartmentId() == null) ? "Unassigned" : "Dept " + d.getDepartmentId();
            deptCounts.put(deptName, deptCounts.getOrDefault(deptName, 0) + 1);
        }

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

    //Feedback analytics
    private void loadFeedbackRatings() {
        // Ratings distribution (1..5)
        Map<Integer, Integer> ratingCounts = new TreeMap<>();
        for (int r = 1; r <= 5; r++) ratingCounts.put(r, 0);

        for (PatientFeedback f : feedbackService.getAll()) {
            Integer r = f.getRating();
            if (r != null && r >= 1 && r <= 5) ratingCounts.put(r, ratingCounts.get(r) + 1);
        }

        chartFeedbackRatings.getData().clear();
        ratingCounts.forEach((r, count) -> {
            if (count > 0) chartFeedbackRatings.getData().add(new PieChart.Data(String.valueOf(r), count));
        });
    }

    @SuppressWarnings("unused")
    private void loadFeedbackTrend() {
        // Optional: daily count trend (if you add chartFeedbackTrend to FXML)
        Map<String, Integer> dateCounts = new TreeMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (PatientFeedback f : feedbackService.getAll()) {
            if (f.getCreatedAt() == null) continue;
            String d = f.getCreatedAt().toLocalDate().format(fmt);
            dateCounts.put(d, dateCounts.getOrDefault(d, 0) + 1);
        }
        if (chartFeedbackTrend == null) return; // guard if not in FXML

        chartFeedbackTrend.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Feedback");
        dateCounts.forEach((date, count) -> series.getData().add(new XYChart.Data<>(date, count)));
        chartFeedbackTrend.getData().add(series);
    }

    //Inventory analytics
    private void loadInventoryLowStockByCategory() {
        List<InventoryItem> all = inventoryService.getAll();
        // Filter: below or equal to reorder level
        List<InventoryItem> low = all.stream()
                .filter(i -> i.getQuantity() != null && i.getReorderLevel() != null && i.getQuantity() <= i.getReorderLevel())
                .toList();

        Map<String, Integer> perCategory = new TreeMap<>();
        for (InventoryItem i : low) {
            String cat = (i.getCategory() == null || i.getCategory().isBlank()) ? "Uncategorized" : i.getCategory().trim();
            perCategory.put(cat, perCategory.getOrDefault(cat, 0) + 1);
        }

        chartLowStockByCategory.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Items <= Reorder");
        perCategory.forEach((cat, count) -> series.getData().add(new XYChart.Data<>(cat, count)));
        chartLowStockByCategory.getData().add(series);
    }

    private void loadTopLowStockItems() {
        final int TOP_N = 10; // show up to 10 items
        List<InventoryItem> low = inventoryService.getAll().stream()
                .filter(i -> i.getQuantity() != null && i.getReorderLevel() != null && i.getQuantity() <= i.getReorderLevel())
                .collect(Collectors.toList());

        // Sorting
        low.sort((a, b) -> {
            int defA = Math.max(0, a.getReorderLevel() - a.getQuantity());
            int defB = Math.max(0, b.getReorderLevel() - b.getQuantity());
            return Integer.compare(defB, defA);
        });

        List<InventoryItem> top = low.stream().limit(TOP_N).toList();

        chartTopLowStockItems.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Units Needed");

        for (InventoryItem it : top) {
            int deficit = Math.max(0, it.getReorderLevel() - it.getQuantity());
            String name = it.getName() == null ? ("ID:" + it.getItemId()) : it.getName();
            String label = name.length() > 20 ? name.substring(0, 17) + "…" : name;
            series.getData().add(new XYChart.Data<>(label, deficit));
        }
        chartTopLowStockItems.getData().add(series);
        chartTopLowStockItems.getXAxis().setTickLabelRotation(45);
    }
}
