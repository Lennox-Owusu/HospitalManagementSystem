
package com.amalitech.hospitalmanagementsystem.model;

import java.time.LocalDateTime;

public class PatientFeedback {
    private Long feedbackId;
    private Long patientId;
    private Long doctorId; // optional
    private Integer rating; // 1..5
    private String comments;
    private LocalDateTime createdAt;

    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void validate() {
        if (patientId == null || patientId <= 0) throw new IllegalArgumentException("Patient required");
        if (rating == null || rating < 1 || rating > 5) throw new IllegalArgumentException("Rating 1..5 required");
        if (comments != null && comments.length() > 1000) throw new IllegalArgumentException("Comments <= 1000 chars");
    }
}
