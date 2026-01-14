
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import java.util.List;
import java.util.Optional;

public interface PatientFeedbackService {
    Long create(PatientFeedback f);
    boolean update(PatientFeedback f);
    boolean remove(Long feedbackId);
    Optional<PatientFeedback> getById(Long feedbackId);
    List<PatientFeedback> getAll();
    List<PatientFeedback> findByPatient(Long patientId);
    List<PatientFeedback> findByDoctor(Long doctorId);
}
