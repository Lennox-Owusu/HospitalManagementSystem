
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientDao {
    Long create(Patient patient);
    boolean update(Patient patient);
    boolean deleteById(Long id);
    Optional<Patient> findById(Long id);
    List<Patient> findAll();
    List<Patient> searchByName(String nameLike);
}
