
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class PatientFormController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderBox;
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;

    private Patient existing;

    @FXML
    public void initialize() {
        genderBox.getItems().setAll("M", "F", "Other");
    }

    public void setExisting(Patient existing) {
        this.existing = existing;
        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            genderBox.setValue(existing.getGender());
            dobPicker.setValue(existing.getDateOfBirth());
            phoneField.setText(existing.getPhone());
            emailField.setText(existing.getEmail());
            addressField.setText(existing.getAddress());
        }
    }

    public Patient collectResult() {
        String fn = firstNameField.getText();
        String ln = lastNameField.getText();
        String gender = genderBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressField.getText();

        if (fn == null || fn.isBlank()) throw new IllegalArgumentException("First name required");
        if (ln == null || ln.isBlank()) throw new IllegalArgumentException("Last name required");
        if (gender == null || gender.isBlank()) throw new IllegalArgumentException("Gender required");
        if (dob == null) throw new IllegalArgumentException("Date of birth required");

        Patient p = new Patient(fn, ln, gender, dob, phone, email, address);
        if (existing != null) {
            p.setId(existing.getId());
        }
        return p;
    }
}
