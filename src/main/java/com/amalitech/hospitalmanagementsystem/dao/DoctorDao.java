
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorDao {
    Long create(Doctor doctor);
    boolean update(Doctor doctor);
    boolean deleteById(Long id);
    Optional<Doctor> findById(Long id);
    List<Doctor> findAll();
    List<Doctor> searchByNameOrSpecialization(String term);
}
