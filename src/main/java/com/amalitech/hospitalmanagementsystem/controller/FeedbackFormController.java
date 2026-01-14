
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

public class FeedbackFormController {
    @FXML private ComboBox<Patient> patientBox;
    @FXML private ComboBox<Doctor> doctorBox;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private TextArea commentsArea;

    private PatientFeedback existing;

    @FXML
    public void initialize() {
        PatientDao pDao = new PatientDaoImpl();
        DoctorDao dDao = new DoctorDaoImpl();
        patientBox.setItems(FXCollections.observableArrayList(pDao.findAll()));
        doctorBox.setItems(FXCollections.observableArrayList(dDao.findAll()));
        patientBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Patient p) { return p==null?"":p.getFirstName()+" "+p.getLastName()+" (ID:"+p.getId()+")"; }
            @Override public Patient fromString(String s) { return null; }
        });
        doctorBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Doctor d) { return d==null?"":d.getFirstName()+" "+d.getLastName()+" - "+d.getSpecialization(); }
            @Override public Doctor fromString(String s) { return null; }
        });
        ratingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
    }

    public void setExisting(PatientFeedback f) {
        this.existing = f;
        if (f != null) {
            if (f.getPatientId() != null) {
                patientBox.getItems().stream().filter(x -> x.getId().equals(f.getPatientId())).findFirst()
                        .ifPresent(x -> patientBox.getSelectionModel().select(x));
            }
            if (f.getDoctorId() != null) {
                doctorBox.getItems().stream().filter(x -> x.getId().equals(f.getDoctorId())).findFirst()
                        .ifPresent(x -> doctorBox.getSelectionModel().select(x));
            }
            if (f.getRating() != null) ratingSpinner.getValueFactory().setValue(f.getRating());
            commentsArea.setText(f.getComments());
        }
    }

    public PatientFeedback collectResult() {
        Patient p = patientBox.getValue();
        if (p == null) throw new IllegalArgumentException("Patient is required");

        Integer rating = ratingSpinner.getValue();
        PatientFeedback out = new PatientFeedback();
        out.setPatientId(p.getId());
        Doctor d = doctorBox.getValue();
        out.setDoctorId(d == null ? null : d.getId());
        out.setRating(rating);
        out.setComments(commentsArea.getText());

        if (existing != null) out.setFeedbackId(existing.getFeedbackId());
        return out;
    }
}
