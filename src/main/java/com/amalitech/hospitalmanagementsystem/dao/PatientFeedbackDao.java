
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import java.util.List;
import java.util.Optional;

public interface PatientFeedbackDao {
    Long create(PatientFeedback f);
    boolean update(PatientFeedback f);
    boolean deleteById(Long feedbackId);
    Optional<PatientFeedback> findById(Long feedbackId);
    List<PatientFeedback> findAll();
    List<PatientFeedback> findByPatient(Long patientId);
    List<PatientFeedback> findByDoctor(Long doctorId);
}
