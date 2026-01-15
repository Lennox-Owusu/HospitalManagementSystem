
package com.amalitech.hospitalmanagementsystem.nosql;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.List;

public class PatientNote {
    private ObjectId id;        // Mongo _id
    private Long patientId;     // Link to SQL patient
    private Long doctorId;      // Optional
    private String noteType;    // Diagnosis, Observation, LabResult, etc.
    private String content;     // Unstructured text
    private List<String> tags;  // Optional labels
    private LocalDateTime createdAt;

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getNoteType() { return noteType; }
    public void setNoteType(String noteType) { this.noteType = noteType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
