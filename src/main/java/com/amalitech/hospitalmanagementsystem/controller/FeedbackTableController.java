
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import com.amalitech.hospitalmanagementsystem.service.PatientFeedbackService;
import com.amalitech.hospitalmanagementsystem.service.impl.PatientFeedbackServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FeedbackTableController {
    @FXML private TableView<PatientFeedback> table;
    @FXML private TableColumn<PatientFeedback, Long> colId;
    @FXML private TableColumn<PatientFeedback, Long> colPatient;
    @FXML private TableColumn<PatientFeedback, Long> colDoctor;
    @FXML private TableColumn<PatientFeedback, Integer> colRating;
    @FXML private TableColumn<PatientFeedback, String> colComments;
    @FXML private TableColumn<PatientFeedback, java.time.LocalDateTime> colCreated;
    @FXML private TextField searchField;

    private final PatientFeedbackService service = new PatientFeedbackServiceImpl();
    private final ObservableList<PatientFeedback> data = FXCollections.observableArrayList();

    // name caches for display & search
    private final Map<Long, String> patientNames = new HashMap<>();
    private final Map<Long, String> doctorNames  = new HashMap<>();

    @FXML
    public void initialize() {
        preloadNames();

        colId.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colComments.setCellValueFactory(new PropertyValueFactory<>("comments"));
        colCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        colPatient.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : patientNames.getOrDefault(id, "ID:" + id));
            }
        });
        colDoctor.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) { setText(null); return; }
                setText(doctorNames.getOrDefault(id, "ID:" + id));
            }
        });
        colCreated.setCellFactory(col -> new TableCell<>() {
            final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override protected void updateItem(java.time.LocalDateTime dt, boolean empty) {
                super.updateItem(dt, empty);
                setText(empty || dt == null ? null : fmt.format(dt));
            }
        });

        table.setItems(data);
        loadAll();
    }

    private void preloadNames() {
        PatientDao pDao = new PatientDaoImpl();
        for (Patient p : pDao.findAll()) {
            patientNames.put(p.getId(), p.getFirstName() + " " + p.getLastName());
        }
        DoctorDao dDao = new DoctorDaoImpl();
        for (Doctor d : dDao.findAll()) {
            doctorNames.put(d.getId(), d.getFirstName() + " " + d.getLastName());
        }
    }

    private void loadAll() { data.setAll(service.getAll()); }

    @FXML private void onRefresh() { preloadNames(); loadAll(); }

    @FXML private void onSearch() {
        String t = searchField.getText();
        if (t == null || t.isBlank()) { loadAll(); return; }
        String q = t.toLowerCase();

        List<PatientFeedback> all = service.getAll();
        List<PatientFeedback> filtered = new ArrayList<>();
        for (PatientFeedback f : all) {
            String pn = patientNames.getOrDefault(f.getPatientId(), "");
            String dn = f.getDoctorId() == null ? "" : doctorNames.getOrDefault(f.getDoctorId(), "");
            boolean match = pn.toLowerCase().contains(q) ||
                    dn.toLowerCase().contains(q) ||
                    (f.getComments() != null && f.getComments().toLowerCase().contains(q));
            if (!match) {
                try {
                    int r = Integer.parseInt(q);
                    match = Objects.equals(f.getRating(), r);
                } catch (NumberFormatException ignore) {}
            }
            if (match) filtered.add(f);
        }
        data.setAll(filtered);
    }

    @FXML private void onAdd() {
        Optional<PatientFeedback> res = showFormDialog(null);
        res.ifPresent(f -> {
            try { service.create(f); loadAll(); }
            catch (RuntimeException ex) { showAlert(Alert.AlertType.ERROR, "Create failed", ex.getMessage()); }
        });
    }

    @FXML private void onEdit() {
        PatientFeedback selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select feedback to edit"); return; }
        Optional<PatientFeedback> res = showFormDialog(selected);
        res.ifPresent(f -> {
            f.setFeedbackId(selected.getFeedbackId());
            boolean ok = service.update(f);
            if (!ok) showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update feedback");
            loadAll();
        });
    }

    @FXML private void onDelete() {
        PatientFeedback selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select feedback to delete"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected feedback?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = service.remove(selected.getFeedbackId());
                if (!ok) showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete feedback");
                loadAll();
            }
        });
    }

    private Optional<PatientFeedback> showFormDialog(PatientFeedback existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/feedback_form.fxml"));
            DialogPane pane = loader.load();
            FeedbackFormController ctrl = loader.getController();
            ctrl.setExisting(existing);
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Feedback" : "Edit Feedback");
            Optional<ButtonType> res = dlg.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                try { return Optional.of(ctrl.collectResult()); }
                catch (IllegalArgumentException ex) { showAlert(Alert.AlertType.ERROR, "Validation error", ex.getMessage()); }
            }
            return Optional.empty();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Dialog error", e.getMessage());
            return Optional.empty();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}

