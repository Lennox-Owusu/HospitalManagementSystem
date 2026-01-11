
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DepartmentDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DepartmentDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Department;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DoctorFormController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField specField;
    @FXML private ComboBox<Department> deptBox;  // <-- ComboBox instead of TextField
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    private Doctor existing;

    @FXML
    public void initialize() {
        // Load departments from DB into ComboBox
        DepartmentDao deptDao = new DepartmentDaoImpl();
        deptBox.setItems(FXCollections.observableArrayList(deptDao.findAll()));
        // Department.toString() returns its name, so the ComboBox shows names automatically
    }

    public void setExisting(Doctor d) {
        this.existing = d;
        if (d != null) {
            firstNameField.setText(d.getFirstName());
            lastNameField.setText(d.getLastName());
            specField.setText(d.getSpecialization());
            phoneField.setText(d.getPhone());
            emailField.setText(d.getEmail());

            // Pre-select the current department if present
            if (d.getDepartmentId() != null) {
                for (Department dep : deptBox.getItems()) {
                    if (d.getDepartmentId().equals(dep.getId())) {
                        deptBox.getSelectionModel().select(dep);
                        break;
                    }
                }
            }
        }
    }

    public Doctor collectResult() {
        String fn = val(firstNameField.getText(), "First name required");
        String ln = val(lastNameField.getText(), "Last name required");
        String spec = val(specField.getText(), "Specialization required");

        Department selected = deptBox.getValue();
        Long deptId = (selected == null ? null : selected.getId()); // <-- selected department_id

        String phone = safe(phoneField.getText());
        String email = safe(emailField.getText());

        Doctor d = new Doctor(fn, ln, spec, deptId, phone, email);
        if (existing != null) d.setId(existing.getId());
        return d;
    }

    private String val(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s.trim();
    }
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
