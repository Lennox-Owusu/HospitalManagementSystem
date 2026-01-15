
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.PrescriptionDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.PrescriptionDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Prescription;
import com.amalitech.hospitalmanagementsystem.service.PrescriptionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionDao dao;
    public PrescriptionServiceImpl() { this.dao = new PrescriptionDaoImpl(); }

    @Override public Long create(Prescription p) { p.validate(); return dao.create(p); }
    @Override public boolean update(Prescription p) {
        if (p.getPrescriptionId() == null) throw new IllegalArgumentException("ID required");
        p.validate();
        return dao.update(p);
    }
    @Override public boolean remove(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.deleteById(id);
    }
    @Override public Optional<Prescription> getById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.findById(id);
    }
    @Override public List<Prescription> getAll() { return dao.findAll(); }
    @Override public List<Prescription> findByPatient(Long patientId) { return dao.findByPatient(patientId); }
    @Override public List<Prescription> findByDoctor(Long doctorId) { return dao.findByDoctor(doctorId); }
    @Override public List<Prescription> findByDate(LocalDate day) { return dao.findByDate(day); }
}
