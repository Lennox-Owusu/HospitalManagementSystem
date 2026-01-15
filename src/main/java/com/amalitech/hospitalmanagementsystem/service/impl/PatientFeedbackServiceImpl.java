
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.PatientFeedbackDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.PatientFeedbackDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import com.amalitech.hospitalmanagementsystem.service.PatientFeedbackService;

import java.util.List;
import java.util.Optional;

public class PatientFeedbackServiceImpl implements PatientFeedbackService {
    private final PatientFeedbackDao dao;
    public PatientFeedbackServiceImpl() { this.dao = new PatientFeedbackDaoImpl(); }

    @Override public Long create(PatientFeedback f) { f.validate(); return dao.create(f); }
    @Override public boolean update(PatientFeedback f) {
        if (f.getFeedbackId() == null) throw new IllegalArgumentException("ID required");
        f.validate();
        return dao.update(f);
    }
    @Override public boolean remove(Long feedbackId) {
        if (feedbackId == null || feedbackId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.deleteById(feedbackId);
    }
    @Override public Optional<PatientFeedback> getById(Long feedbackId) {
        if (feedbackId == null || feedbackId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.findById(feedbackId);
    }
    @Override public List<PatientFeedback> getAll() { return dao.findAll(); }
    @Override public List<PatientFeedback> findByPatient(Long patientId) { return dao.findByPatient(patientId); }
    @Override public List<PatientFeedback> findByDoctor(Long doctorId) { return dao.findByDoctor(doctorId); }
}
