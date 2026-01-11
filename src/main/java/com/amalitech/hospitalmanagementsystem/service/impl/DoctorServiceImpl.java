
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.service.DoctorService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class DoctorServiceImpl implements DoctorService {
    private final DoctorDao doctorDao;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-()\\s]{6,20}$");

    public DoctorServiceImpl(DoctorDao doctorDao) {
        this.doctorDao = doctorDao;
    }

    @Override
    public Long register(Doctor doctor) {
        validate(doctor, true);
        return doctorDao.create(doctor);
    }

    @Override
    public boolean update(Doctor doctor) {
        if (doctor.getId() == null) throw new IllegalArgumentException("ID required for update");
        validate(doctor, false);
        return doctorDao.update(doctor);
    }

    @Override
    public boolean remove(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return doctorDao.deleteById(id);
    }

    @Override
    public Optional<Doctor> getById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return doctorDao.findById(id);
    }

    @Override
    public List<Doctor> getAll() {
        return doctorDao.findAll();
    }

    @Override
    public List<Doctor> search(String term) {
        return doctorDao.searchByNameOrSpecialization(term);
    }

    private void validate(Doctor d, boolean isCreate) {
        if (d == null) throw new IllegalArgumentException("Doctor cannot be null");
        if (d.getFirstName() == null || d.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required");
        if (d.getLastName() == null || d.getLastName().isBlank())
            throw new IllegalArgumentException("Last name is required");
        if (d.getSpecialization() == null || d.getSpecialization().isBlank())
            throw new IllegalArgumentException("Specialization is required");

        if (d.getEmail() != null && !d.getEmail().isBlank() &&
                !EMAIL_PATTERN.matcher(d.getEmail()).matches())
            throw new IllegalArgumentException("Invalid email format");

        if (d.getPhone() != null && !d.getPhone().isBlank() &&
                !PHONE_PATTERN.matcher(d.getPhone()).matches())
            throw new IllegalArgumentException("Invalid phone format");
    }
}
