
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.Prescription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrescriptionDao {
    Long create(Prescription p);
    boolean update(Prescription p);
    boolean deleteById(Long id);
    Optional<Prescription> findById(Long id);
    List<Prescription> findAll();

    // queries for convenience
    List<Prescription> findByPatient(Long patientId);
    List<Prescription> findByDoctor(Long doctorId);
    List<Prescription> findByDate(LocalDate day);
}
