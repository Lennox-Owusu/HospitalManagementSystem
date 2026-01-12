
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.DoctorDaoImpl;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Appointment;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentFormController {

    @FXML private ComboBox<Patient> patientBox;
    @FXML private ComboBox<Doctor>  doctorBox;
    @FXML private DatePicker        datePicker;
    @FXML private TextField         timeField;   // HH:mm
    @FXML private ComboBox<String>  statusBox;
    @FXML private TextField         reasonField;

    private Appointment existing;

    @FXML
    public void initialize() {
        // Load patients & doctors
        PatientDao pDao = new PatientDaoImpl();
        DoctorDao  dDao = new DoctorDaoImpl();
        patientBox.setItems(FXCollections.observableArrayList(pDao.findAll()));
        doctorBox.setItems(FXCollections.observableArrayList(dDao.findAll()));

        // Show names in combo boxes
        patientBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Patient p) {
                return p == null ? "" : p.getFirstName() + " " + p.getLastName() + " (ID:" + p.getId() + ")";
            }
            @Override public Patient fromString(String s) { return null; }
        });
        doctorBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Doctor d) {
                return d == null ? "" : d.getFirstName() + " " + d.getLastName() + " - " + d.getSpecialization();
            }
            @Override public Doctor fromString(String s) { return null; }
        });

        statusBox.setItems(FXCollections.observableArrayList("SCHEDULED", "COMPLETED", "CANCELLED"));
        statusBox.getSelectionModel().select("SCHEDULED");
    }

    public void setExisting(Appointment appt) {
        this.existing = appt;
        if (appt != null) {
            // Pre-select patient & doctor by ID
            if (appt.getPatientId() != null) {
                for (Patient p : patientBox.getItems()) {
                    if (appt.getPatientId().equals(p.getId())) { patientBox.getSelectionModel().select(p); break; }
                }
            }
            if (appt.getDoctorId() != null) {
                for (Doctor d : doctorBox.getItems()) {
                    if (appt.getDoctorId().equals(d.getId())) { doctorBox.getSelectionModel().select(d); break; }
                }
            }
            if (appt.getAppointmentDate() != null) {
                datePicker.setValue(appt.getAppointmentDate().toLocalDate());
                timeField.setText(String.format("%02d:%02d",
                        appt.getAppointmentDate().getHour(),
                        appt.getAppointmentDate().getMinute()));
            }
            if (appt.getStatus() != null) statusBox.getSelectionModel().select(appt.getStatus());
            reasonField.setText(appt.getReason());
        }
    }

    public Appointment collectResult() {
        Patient p = patientBox.getValue();
        Doctor  d = doctorBox.getValue();
        if (p == null) throw new IllegalArgumentException("Patient is required");
        if (d == null) throw new IllegalArgumentException("Doctor is required");

        LocalDate date = datePicker.getValue();            // do NOT call isBlank() on dates
        String hhmm    = timeField.getText() == null ? "" : timeField.getText().trim();
        LocalDateTime dt = Appointment.combine(date, hhmm); // throws if invalid

        String status = statusBox.getValue();
        if (status == null || status.isBlank()) status = "SCHEDULED";

        Appointment out = new Appointment();
        out.setPatientId(p.getId());
        out.setDoctorId(d.getId());
        out.setAppointmentDate(dt);
        out.setStatus(status);
        out.setReason(reasonField.getText());

        if (existing != null) out.setAppointmentId(existing.getAppointmentId());
        return out;
    }
}
