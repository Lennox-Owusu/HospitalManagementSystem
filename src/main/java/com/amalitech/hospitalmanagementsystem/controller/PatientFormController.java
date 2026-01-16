
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Objects;

public class PatientFormController {

    // ========== FXML fields ==========
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderBox;
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;

    private Patient existing;

    // ========== Patterns ==========
    private static final String EMAIL_RE = "^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_RE = "^(\\+?\\d){7,15}$"; // supports +233..., digits only
    private static final String NAME_RE = "^[A-Za-z]{2,50}$";

    @FXML
    public void initialize() {
        // your original options: M / F / Other
        genderBox.getItems().setAll("M", "F", "Other");

        // Live validation listeners (field-level marking)
        firstNameField.textProperty().addListener((a,b,c) ->validateFName());
        lastNameField.textProperty().addListener((a,b,c) -> validateLastName());
        genderBox.valueProperty().addListener((a,b,c) -> validateGender());
        dobPicker.valueProperty().addListener((a,b,c) -> validateDob());
        phoneField.textProperty().addListener((a,b,c) -> validatePhone());
        emailField.textProperty().addListener((a,b,c) -> validateEmail());
        addressField.textProperty().addListener((a,b,c) -> validateAddress());

    }

    /**
     * Call this from PatientTableController after loading the DialogPane.
     * It attaches CSS and binds the OK button to the form validity.
     */
    public void enableRealtimeValidation(DialogPane pane) {
        // Attach validation stylesheet once
        var cssUrl = Objects.requireNonNull(
                getClass().getResource("/css/validation.css"),
                "Missing /css/validation.css on classpath"
        ).toExternalForm();
        if (!pane.getStylesheets().contains(cssUrl)) {
            pane.getStylesheets().add(cssUrl);
        }

        // Get the OK button from the dialog
        Button okButton = (Button) pane.lookupButton(ButtonType.OK);

        // Build a BooleanBinding for overall form validity.
        // We use our own createBooleanBinding helpers for regex instead of StringProperty.matches(...)
        BooleanBinding validForm =
                firstNameField.textProperty().isNotEmpty()
                        .and(lastNameField.textProperty().isNotEmpty())
                        .and(genderBox.valueProperty().isNotNull())
                        .and(dobPicker.valueProperty().isNotNull())
                        .and(match(phoneField, PHONE_RE))
                        .and(match(emailField, EMAIL_RE))
                        .and(match(firstNameField, NAME_RE))
                        .and(addressField.textProperty().isNotEmpty());

        okButton.disableProperty().bind(validForm.not());

        // Mark initial state (important for Edit mode)
        validateAll();
    }

    /** Preload existing values for edit mode */
    public void setExisting(Patient existing) {
        this.existing = existing;
        if (existing != null) {
            firstNameField.setText(n(existing.getFirstName()));
            lastNameField.setText(n(existing.getLastName()));
            genderBox.setValue(n(existing.getGender()));
            dobPicker.setValue(existing.getDateOfBirth());
            phoneField.setText(n(existing.getPhone()));
            emailField.setText(n(existing.getEmail()));
            addressField.setText(n(existing.getAddress()));
        }
        validateAll();
    }

    // ========== Field-level validators ==========

    private boolean validateFirstName() {
        return mark(firstNameField,
                !isBlank(firstNameField),
                "First name is required");
    }

    private boolean validateLastName() {
        return mark(lastNameField,
                !isBlank(lastNameField),
                "Last name is required");
    }

    private boolean validateGender() {
        boolean ok = genderBox.getValue() != null && !genderBox.getValue().isBlank();
        return mark(genderBox, ok, "Select gender");
    }

    private boolean validateDob() {
        LocalDate d = dobPicker.getValue();
        boolean ok = d != null && !d.isAfter(LocalDate.now());
        return mark(dobPicker, ok, d == null ? "Date of birth is required" : "Date of birth cannot be in the future");
    }

    private boolean validatePhone() {
        String v = t(phoneField);
        boolean ok = v.matches(PHONE_RE);
        return mark(phoneField, ok, "Phone must be 7–15 digits (optional +country code)");
    }

    private boolean validateEmail() {
        String v = t(emailField);
        boolean ok = v.matches(EMAIL_RE);
        return mark(emailField, ok, "Invalid email format");
    }

    private boolean validateAddress() {
        return mark(addressField,
                !isBlank(addressField),
                "Address is required");
    }

    private boolean validateFName() {
        String v = t(firstNameField);
        boolean ok = v.matches(EMAIL_RE);
        return mark(firstNameField, ok, "First name must be only alphabets)");
    }

    private void validateAll() {
        validateFName();
        validateLastName();
        validateGender();
        validateDob();
        validatePhone();
        validateEmail();
        validateAddress();
    }

    // ========== Helpers ==========

    private BooleanBinding match(TextField tf, String regex) {
        return Bindings.createBooleanBinding(
                () -> {
                    String v = tf.getText();
                    return v != null && v.matches(regex);
                },
                tf.textProperty()
        );
    }

    private boolean mark(Control c, boolean ok, String tooltipMsgIfError) {
        c.getStyleClass().removeAll("error", "valid");
        if (ok) {
            c.getStyleClass().add("valid");
            c.setTooltip(null);
            return true;
        } else {
            c.getStyleClass().add("error");
            c.setTooltip(new Tooltip(tooltipMsgIfError));
            return false;
        }
    }

    private static boolean isBlank(TextField tf) {
        String v = tf.getText();
        return v == null || v.isBlank();
    }

    private static String t(TextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }

    private static String n(String s) {
        return s == null ? "" : s;
    }

    // ========== Final model build ==========

    public Patient collectResult() {
        validateAll();
        if (!formValid()) {
            throw new IllegalArgumentException("Please correct the highlighted fields.");
        }
        Patient p = new Patient(
                t(firstNameField),
                t(lastNameField),
                genderBox.getValue(),
                dobPicker.getValue(),
                t(phoneField),
                t(emailField),
                t(addressField)
        );
        if (existing != null) {
            p.setId(existing.getId());
        }
        return p;
    }

    private boolean formValid() {
        return validateFName()
                && validateLastName()
                && validateGender()
                && validateDob()
                && validatePhone()
                && validateEmail()
                && validateAddress();
    }
}
