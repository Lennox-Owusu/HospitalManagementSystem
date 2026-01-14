
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.model.Prescription;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PrescriptionFormController {
    @FXML private ComboBox<Patient> patientBox;
    @FXML private ComboBox<Doctor> doctorBox;
    @FXML private TextArea notesArea;

    private Prescription existing;

    @FXML
    public void initialize() {
        PatientDao pDao = new PatientDaoImpl();
        DoctorDao dDao = new DoctorDaoImpl();
        patientBox.setItems(FXCollections.observableArrayList(pDao.findAll()));
        doctorBox.setItems(FXCollections.observableArrayList(dDao.findAll()));
        // show nice names
        patientBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Patient p) { return p == null ? "" : p.getFirstName()+" "+p.getLastName()+" (ID:"+p.getId()+")"; }
            @Override public Patient fromString(String s) { return null; }
        });
        doctorBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Doctor d) { return d == null ? "" : d.getFirstName()+" "+d.getLastName()+" - "+d.getSpecialization(); }
            @Override public Doctor fromString(String s) { return null; }
        });
    }

    public void setExisting(Prescription p) {
        this.existing = p;
        if (p != null) {
            if (p.getPatientId() != null) {
                patientBox.getItems().stream().filter(x -> x.getId().equals(p.getPatientId())).findFirst()
                        .ifPresent(x -> patientBox.getSelectionModel().select(x));
            }
            if (p.getDoctorId() != null) {
                doctorBox.getItems().stream().filter(x -> x.getId().equals(p.getDoctorId())).findFirst()
                        .ifPresent(x -> doctorBox.getSelectionModel().select(x));
            }
            notesArea.setText(p.getNotes());
        }
    }

    public Prescription collectResult() {
        Patient p = patientBox.getValue();
        Doctor d = doctorBox.getValue();
        if (p == null) throw new IllegalArgumentException("Patient is required");
        if (d == null) throw new IllegalArgumentException("Doctor is required");

        Prescription out = new Prescription();
        out.setPatientId(p.getId());
        out.setDoctorId(d.getId());
        out.setNotes(notesArea.getText());
        if (existing != null) out.setPrescriptionId(existing.getPrescriptionId());
        return out;
    }
}
