
package com.amalitech.hospitalmanagementsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Appointment {
    private Long appointmentId;     // maps to appointment_id
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDate; // timestamp without time zone
    private String status;          // SCHEDULED | COMPLETED | CANCELLED
    private String reason;          // varchar(255)
    private LocalDateTime createdAt;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** Basic validation aligned to your schema */
    public void validate() {
        if (patientId == null || patientId <= 0) throw new IllegalArgumentException("Patient is required.");
        if (doctorId == null || doctorId <= 0) throw new IllegalArgumentException("Doctor is required.");
        if (appointmentDate == null) throw new IllegalArgumentException("Appointment date/time is required.");
        if (status == null || status.isBlank()) status = "SCHEDULED";
        // prevent past scheduling (optional; remove if not desired)
        if (appointmentDate.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }
        if (reason != null && reason.length() > 255) {
            throw new IllegalArgumentException("Reason must be at most 255 characters.");
        }
    }

    //Helper for building LocalDateTime from UI parts
    public static LocalDateTime combine(LocalDate d, String hhmm) {
        if (d == null) return null;
        if (hhmm == null || hhmm.isBlank()) throw new IllegalArgumentException("Time is required (HH:mm).");
        LocalTime t = LocalTime.parse(hhmm.trim()); // expects HH:mm
        return LocalDateTime.of(d, t);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(appointmentId, that.appointmentId);
    }
    @Override public int hashCode() { return Objects.hash(appointmentId); }
    @Override public String toString() {
        return "Appointment{appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", appointmentDate=" + appointmentDate +
                ", status='" + status + '\'' +
                '}';
    }
}

