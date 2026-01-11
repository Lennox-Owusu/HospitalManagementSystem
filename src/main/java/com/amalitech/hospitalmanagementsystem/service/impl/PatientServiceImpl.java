
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.service.PatientService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class PatientServiceImpl implements PatientService {
    private final PatientDao patientDao;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-()\\s]{6,20}$");

    public PatientServiceImpl(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @Override
    public Long register(Patient patient) {
        validate(patient);
        return patientDao.create(patient);
    }

    @Override
    public boolean update(Patient patient) {
        if (patient.getId() == null) throw new IllegalArgumentException("ID required for update");
        validate(patient);
        return patientDao.update(patient);
    }

    @Override
    public boolean remove(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return patientDao.deleteById(id);
    }

    @Override
    public Optional<Patient> getById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return patientDao.findById(id);
    }

    @Override
    public List<Patient> getAll() {
        return patientDao.findAll();
    }

    @Override
    public List<Patient> search(String nameLike) {
        return patientDao.searchByName(nameLike);
    }

    private void validate(Patient p) {
        if (p == null) throw new IllegalArgumentException("Patient cannot be null");
        if (p.getFirstName() == null || p.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required");
        if (p.getLastName() == null || p.getLastName().isBlank())
            throw new IllegalArgumentException("Last name is required");
        if (p.getGender() == null || p.getGender().isBlank())
            throw new IllegalArgumentException("Gender is required");

        String g = p.getGender().trim();
        if (!g.equals("M") && !g.equals("F") && !g.equalsIgnoreCase("Other"))
            throw new IllegalArgumentException("Gender must be 'M', 'F', or 'Other'");

        if (p.getDateOfBirth() == null)
            throw new IllegalArgumentException("Date of birth is required");
        if (p.getDateOfBirth().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Date of birth cannot be in the future");

        if (p.getEmail() != null && !p.getEmail().isBlank() &&
                !EMAIL_PATTERN.matcher(p.getEmail()).matches())
            throw new IllegalArgumentException("Invalid email format");

        if (p.getPhone() != null && !p.getPhone().isBlank() &&
                !PHONE_PATTERN.matcher(p.getPhone()).matches())
            throw new IllegalArgumentException("Invalid phone format");
    }
}
