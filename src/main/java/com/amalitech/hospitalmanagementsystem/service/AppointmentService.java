
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Long create(Appointment a);
    boolean update(Appointment a);
    boolean remove(Long appointmentId);
    Optional<Appointment> getById(Long appointmentId);
    List<Appointment> getAll();

    List<Appointment> findByDate(LocalDate date);
    List<Appointment> findByDoctor(Long doctorId, LocalDate from, LocalDate to);
    List<Appointment> findByPatient(Long patientId, LocalDate from, LocalDate to);

    boolean updateStatus(Long appointmentId, String status);
    boolean existsSlot(Long doctorId, LocalDateTime dateTime);
}
