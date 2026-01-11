
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    Long register(Patient patient);
    boolean update(Patient patient);
    boolean remove(Long id);
    Optional<Patient> getById(Long id);
    List<Patient> getAll();
    List<Patient> search(String nameLike);
}
