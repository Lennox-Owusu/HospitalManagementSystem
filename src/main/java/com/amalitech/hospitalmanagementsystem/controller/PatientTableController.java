
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.service.PatientService;
import com.amalitech.hospitalmanagementsystem.service.impl.PatientServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PatientTableController {

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Long> colId;
    @FXML private TableColumn<Patient, String> colFirstName;
    @FXML private TableColumn<Patient, String> colLastName;
    @FXML private TableColumn<Patient, String> colGender;
    @FXML private TableColumn<Patient, LocalDate> colDob;
    @FXML private TableColumn<Patient, String> colPhone;
    @FXML private TableColumn<Patient, String> colEmail;
    @FXML private TableColumn<Patient, String> colAddress;

    @FXML private TextField searchField;

    private final PatientService service;
    private final ObservableList<Patient> data = FXCollections.observableArrayList();

    public PatientTableController() {
        PatientDao dao = new PatientDaoImpl();
        this.service = new PatientServiceImpl(dao);
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colDob.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : fmt.format(item));
            }
        });

        patientTable.setItems(data);
        loadAll();
    }

    private void loadAll() {
        List<Patient> list = service.getAll();
        data.setAll(list);
    }

    @FXML
    private void onRefresh() {
        loadAll();
    }

    @FXML
    private void onSearch() {
        String term = searchField.getText();
        List<Patient> list = service.search(term);
        data.setAll(list);
    }

    @FXML
    private void onAdd() {
        Optional<Patient> result = showFormDialog(null);
        result.ifPresent(p -> {
            service.register(p);
            loadAll();
        });
    }

    @FXML
    private void onEdit() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a patient to edit");
            return;
        }
        Optional<Patient> result = showFormDialog(selected);
        result.ifPresent(p -> {
            p.setId(selected.getId());
            boolean ok = service.update(p);
            if (!ok) {
                showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update the patient");
            }
            loadAll();
        });
    }

    @FXML
    private void onDelete() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a patient to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected patient?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = service.remove(selected.getId());
                if (!ok) {
                    showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete the patient");
                }
                loadAll();
            }
        });
    }

    private Optional<Patient> showFormDialog(Patient existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patient_form.fxml"));
            DialogPane pane = loader.load();

            PatientFormController controller = loader.getController();
            controller.setExisting(existing);

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Patient" : "Edit Patient");

            Optional<ButtonType> res = dlg.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                try {
                    return Optional.of(controller.collectResult());
                } catch (IllegalArgumentException ex) {
                    showAlert(Alert.AlertType.ERROR, "Validation error", ex.getMessage());
                    return Optional.empty();
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



    @FXML
    private void onNotes() {
        var selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a patient first");
            return;
        }
        try {
            System.out.println("[onNotes] Loading /view/patient_notes_dialog.fxml ...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patient_notes_dialog.fxml"));
            DialogPane pane = loader.load();
            System.out.println("[onNotes] FXML loaded OK");

            PatientNotesDialogController ctrl = loader.getController();
            ctrl.setPatientId(selected.getId());
            System.out.println("[onNotes] setPatientId -> " + selected.getId());

            Dialog<PatientNotesDialogController.Result> dlg = new Dialog<>();
            dlg.setTitle("Patient Notes — ID " + selected.getId());
            dlg.setDialogPane(pane);
            dlg.setResultConverter(param -> ctrl.handleDialogResult(param));
            dlg.showAndWait();

        } catch (Exception e) {
            // Print full diagnostic to console
            System.err.println("=== Notes dialog error ===");
            e.printStackTrace();
            Throwable cause = e.getCause();
            int depth = 1;
            while (cause != null && depth <= 5) {
                System.err.println("Cause " + depth + ": " + cause.getClass().getName() + " - " + cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
            // Show a more informative alert
            showAlert(
                    Alert.AlertType.ERROR,
                    "Notes dialog error",
                    (e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "(no message)" : e.getMessage()))
            );
        }
    }



}
