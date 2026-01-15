
package com.amalitech.hospitalmanagementsystem.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Prescription {
    private Long prescriptionId;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime issuedAt;
    private String notes;

    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void validate() {
        if (patientId == null || patientId <= 0) throw new IllegalArgumentException("Patient is required.");
        if (doctorId == null || doctorId <= 0) throw new IllegalArgumentException("Doctor is required.");
        // issuedAt can be null; DB defaults to NOW()
        if (notes != null && notes.length() > 1000)
            throw new IllegalArgumentException("Notes must be <= 1000 chars.");
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription that)) return false;
        return Objects.equals(prescriptionId, that.prescriptionId);
    }
    @Override public int hashCode() { return Objects.hash(prescriptionId); }
}
