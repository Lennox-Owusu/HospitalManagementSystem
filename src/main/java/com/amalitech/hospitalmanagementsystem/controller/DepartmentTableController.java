
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DepartmentDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DepartmentDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Department;
import com.amalitech.hospitalmanagementsystem.service.DepartmentService;
import com.amalitech.hospitalmanagementsystem.service.impl.DepartmentServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DepartmentTableController {

    @FXML private TableView<Department> deptTable;
    @FXML private TableColumn<Department, Long> colId;
    @FXML private TableColumn<Department, String> colName;
    @FXML private TableColumn<Department, String> colDesc;

    @FXML private TextField searchField;

    private final DepartmentService service;
    private final ObservableList<Department> data = FXCollections.observableArrayList();

    public DepartmentTableController() {
        DepartmentDao dao = new DepartmentDaoImpl();
        this.service = new DepartmentServiceImpl(dao);
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        deptTable.setItems(data);
        loadAll();
    }

    private void loadAll() {
        List<Department> list = service.getAll();
        data.setAll(list);
    }

    @FXML
    private void onRefresh() { loadAll(); }

    @FXML
    private void onSearch() {
        String term = searchField.getText();
        List<Department> list = service.searchByName(term);
        data.setAll(list);
    }

    @FXML
    private void onAdd() {
        Optional<Department> result = showFormDialog(null);
        result.ifPresent(d -> {
            try {
                service.create(d);
                loadAll();
            } catch (RuntimeException ex) {
                showAlert(Alert.AlertType.ERROR, "Create failed", ex.getMessage());
            }
        });
    }

    @FXML
    private void onEdit() {
        Department selected = deptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a department to edit");
            return;
        }
        Optional<Department> result = showFormDialog(selected);
        result.ifPresent(d -> {
            d.setId(selected.getId());
            boolean ok = service.update(d);
            if (!ok) showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update the department");
            loadAll();
        });
    }

    @FXML
    private void onDelete() {
        Department selected = deptTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Please select a department to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected department?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = service.delete(selected.getId());
                if (!ok) showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete the department (may be referenced by doctors)");
                loadAll();
            }
        });
    }

    private Optional<Department> showFormDialog(Department existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/department_form.fxml"));
            DialogPane pane = loader.load();

            DepartmentFormController controller = loader.getController();
            controller.setExisting(existing);

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Department" : "Edit Department");

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
