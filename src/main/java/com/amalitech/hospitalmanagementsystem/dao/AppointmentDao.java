
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    Long create(Appointment a);
    boolean update(Appointment a);
    boolean deleteById(Long appointmentId);

    Optional<Appointment> findById(Long appointmentId);
    List<Appointment> findAll();

    // Filters
    List<Appointment> findByDate(LocalDate date); // matches date only (any time on that date)
    List<Appointment> findByDoctor(Long doctorId, LocalDate fromDate, LocalDate toDate);
    List<Appointment> findByPatient(Long patientId, LocalDate fromDate, LocalDate toDate);

    boolean updateStatus(Long appointmentId, String status);

    // Optional: check for double-booking for same doctor at same minute
    boolean existsDoctorSlot(Long doctorId, LocalDateTime dateTime);
}
