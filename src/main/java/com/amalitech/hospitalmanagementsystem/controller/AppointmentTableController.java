
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Appointment;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.service.AppointmentService;
import com.amalitech.hospitalmanagementsystem.service.impl.AppointmentServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AppointmentTableController {

    @FXML private TableView<Appointment> apptTable;
    @FXML private TableColumn<Appointment, Long>           colId;
    @FXML private TableColumn<Appointment, Long>           colPatient;
    @FXML private TableColumn<Appointment, Long>           colDoctor;
    @FXML private TableColumn<Appointment, LocalDateTime>  colDateTime;
    @FXML private TableColumn<Appointment, String>         colStatus;
    @FXML private TableColumn<Appointment, String>         colReason;

    @FXML private TextField searchField;

    private final AppointmentService service = new AppointmentServiceImpl();
    private final ObservableList<Appointment> data = FXCollections.observableArrayList();

    // caches for patient/doctor display names
    private final Map<Long, String> patientNames = new HashMap<>();
    private final Map<Long, String> doctorNames = new HashMap<>();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));

        // Load names for fast mapping
        preloadNames();

        // Render names for patient/doctor columns
        colPatient.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : patientNames.getOrDefault(id, "ID:" + id));
            }
        });
        colDoctor.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : doctorNames.getOrDefault(id, "ID:" + id));
            }
        });
        colDateTime.setCellFactory(col -> new TableCell<>() {
            final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override protected void updateItem(LocalDateTime dt, boolean empty) {
                super.updateItem(dt, empty);
                setText(empty || dt == null ? null : fmt.format(dt));
            }
        });

        apptTable.setItems(data);
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

    private void loadAll() {
        data.setAll(service.getAll());
    }

    @FXML private void onRefresh() { preloadNames(); loadAll(); }

    @FXML
    private void onSearch() {
        String term = searchField.getText();
        if (term == null || term.isBlank()) { loadAll(); return; }
        String t = term.toLowerCase();

        List<Appointment> filtered = new ArrayList<>();
        for (Appointment a : service.getAll()) {
            String pName = patientNames.getOrDefault(a.getPatientId(), "");
            String dName = doctorNames.getOrDefault(a.getDoctorId(), "");
            if (pName.toLowerCase().contains(t) ||
                    dName.toLowerCase().contains(t) ||
                    (a.getStatus() != null && a.getStatus().toLowerCase().contains(t)) ||
                    (a.getReason() != null && a.getReason().toLowerCase().contains(t))) {
                filtered.add(a);
            }
        }
        data.setAll(filtered);
    }

    @FXML
    private void onAdd() {
        Optional<Appointment> result = showFormDialog(null);
        result.ifPresent(a -> {
            try {
                service.create(a);
                loadAll();
            } catch (RuntimeException ex) {
                showAlert(Alert.AlertType.ERROR, "Create failed", ex.getMessage());
            }
        });
    }

    @FXML
    private void onEdit() {
        Appointment selected = apptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select an appointment to edit");
            return;
        }
        Optional<Appointment> result = showFormDialog(selected);
        result.ifPresent(a -> {
            a.setAppointmentId(selected.getAppointmentId());
            boolean ok = service.update(a);
            if (!ok) showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update the appointment");
            loadAll();
        });
    }

    @FXML
    private void onDelete() {
        Appointment selected = apptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select an appointment to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected appointment?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = service.remove(selected.getAppointmentId());
                if (!ok) showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete the appointment");
                loadAll();
            }
        });
    }

    private Optional<Appointment> showFormDialog(Appointment existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/appointment_form.fxml"));
            DialogPane pane = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setExisting(existing);
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Appointment" : "Edit Appointment");
            Optional<ButtonType> res = dlg.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                try {
                    return Optional.of(controller.collectResult());
                } catch (IllegalArgumentException ex) {
                    showAlert(Alert.AlertType.ERROR, "Validation error", ex.getMessage());
                }
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
