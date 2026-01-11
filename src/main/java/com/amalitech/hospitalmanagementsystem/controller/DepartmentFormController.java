
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Department;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DepartmentFormController {
    @FXML private TextField nameField;
    @FXML private TextArea descArea;

    private Department existing;

    public void setExisting(Department d) {
        this.existing = d;
        if (d != null) {
            nameField.setText(d.getName());
            descArea.setText(d.getDescription());
        }
    }

    public Department collectResult() {
        String name = val(nameField.getText(), "Name is required");
        String desc = descArea.getText() == null ? "" : descArea.getText().trim();

        Department d = new Department(null, name, desc);
        if (existing != null) d.setId(existing.getId());
        return d;
    }

    private String val(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s.trim();
    }
}
