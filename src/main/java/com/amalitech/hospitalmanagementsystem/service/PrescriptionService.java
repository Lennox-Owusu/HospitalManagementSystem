
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.Prescription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    Long create(Prescription p);
    boolean update(Prescription p);
    boolean remove(Long id);
    Optional<Prescription> getById(Long id);
    List<Prescription> getAll();
    List<Prescription> findByPatient(Long patientId);
    List<Prescription> findByDoctor(Long doctorId);
    List<Prescription> findByDate(LocalDate day);
}
