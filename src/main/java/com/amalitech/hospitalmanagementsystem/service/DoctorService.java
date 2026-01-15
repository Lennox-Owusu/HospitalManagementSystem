
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    void register(Doctor doctor);
    boolean update(Doctor doctor);
    boolean remove(Long id);
    Optional<Doctor> getById(Long id);
    List<Doctor> getAll();
    List<Doctor> search(String term);
}
