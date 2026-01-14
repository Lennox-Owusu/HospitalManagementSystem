
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.PrescriptionItem;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrescriptionItemFormController {
    @FXML private TextField medicationField, dosageField, frequencyField, durationField;
    @FXML private TextArea instructionsArea;

    private PrescriptionItem existing;
    private Long prescriptionId; // set by caller

    public void setContext(Long prescriptionId, PrescriptionItem existing) {
        this.prescriptionId = prescriptionId;
        this.existing = existing;
        if (existing != null) {
            medicationField.setText(existing.getMedicationName());
            dosageField.setText(existing.getDosage());
            frequencyField.setText(existing.getFrequency());
            durationField.setText(String.valueOf(existing.getDurationDays()));
            instructionsArea.setText(existing.getInstructions());
        }
    }

    public PrescriptionItem collectResult() {
        String med = val(medicationField.getText(), "Medication required");
        String dosage = val(dosageField.getText(), "Dosage required");
        String freq = val(frequencyField.getText(), "Frequency required");
        Integer days = parseDays(durationField.getText());
        PrescriptionItem out = new PrescriptionItem();
        out.setPrescriptionId(prescriptionId);
        out.setMedicationName(med);
        out.setDosage(dosage);
        out.setFrequency(freq);
        out.setDurationDays(days);
        out.setInstructions(instructionsArea.getText() == null ? "" : instructionsArea.getText().trim());
        if (existing != null) out.setItemId(existing.getItemId());
        return out;
    }

    private String val(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s.trim();
    }
    private int parseDays(String s) {
        try { int v = Integer.parseInt(val(s, "Duration required")); if (v <= 0) throw new RuntimeException(); return v; }
        catch (Exception ex) { throw new IllegalArgumentException("Duration must be a positive integer"); }
    }
}

