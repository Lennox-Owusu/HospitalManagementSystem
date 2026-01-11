
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.service.DoctorService;
import com.amalitech.hospitalmanagementsystem.service.impl.DoctorServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DoctorTableController {

    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, Long> colId;
    @FXML private TableColumn<Doctor, String> colFirstName;
    @FXML private TableColumn<Doctor, String> colLastName;
    @FXML private TableColumn<Doctor, String> colSpec;
    @FXML private TableColumn<Doctor, Long> colDept;
    @FXML private TableColumn<Doctor, String> colPhone;
    @FXML private TableColumn<Doctor, String> colEmail;

    @FXML private TextField searchField;

    private final DoctorService service;
    private final ObservableList<Doctor> data = FXCollections.observableArrayList();

    public DoctorTableController() {
        DoctorDao dao = new DoctorDaoImpl();
        this.service = new DoctorServiceImpl(dao);
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        doctorTable.setItems(data);
        loadAll();
    }

    private void loadAll() {
        List<Doctor> list = service.getAll();
        data.setAll(list);
    }

    @FXML
    private void onRefresh() { loadAll(); }

    @FXML
    private void onSearch() {
        String term = searchField.getText();
        List<Doctor> list = service.search(term);
        data.setAll(list);
    }

    @FXML
    private void onAdd() {
        Optional<Doctor> result = showFormDialog(null);
        result.ifPresent(d -> {
            try {
                service.register(d);
                loadAll();
            } catch (RuntimeException ex) {
                showAlert(Alert.AlertType.ERROR, "Create failed", ex.getMessage());
            }
        });
    }

    @FXML
    private void onEdit() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a doctor to edit");
            return;
        }
        Optional<Doctor> result = showFormDialog(selected);
        result.ifPresent(d -> {
            d.setId(selected.getId());
            boolean ok = service.update(d);
            if (!ok) showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update the doctor");
            loadAll();
        });
    }

    @FXML
    private void onDelete() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a doctor to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected doctor?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = service.remove(selected.getId());
                if (!ok) showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete the doctor");
                loadAll();
            }
        });
    }

    private Optional<Doctor> showFormDialog(Doctor existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/doctor_form.fxml"));
            DialogPane pane = loader.load();

            DoctorFormController controller = loader.getController();
            controller.setExisting(existing);

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Doctor" : "Edit Doctor");
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
