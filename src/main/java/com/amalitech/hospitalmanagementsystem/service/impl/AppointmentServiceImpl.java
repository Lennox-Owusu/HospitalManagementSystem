
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.AppointmentDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.AppointmentDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.Appointment;
import com.amalitech.hospitalmanagementsystem.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDao dao;

    public AppointmentServiceImpl() {
        this.dao = new AppointmentDaoImpl();
    }
    public AppointmentServiceImpl(AppointmentDao dao) {
        this.dao = dao;
    }

    @Override
    public Long create(Appointment a) {
        a.validate();
        // Optional: prevent exact-minute double-booking for a doctor
        if (existsSlot(a.getDoctorId(), a.getAppointmentDate())) {
            throw new IllegalArgumentException("Doctor already has an appointment at this time.");
        }
        return dao.create(a);
    }

    @Override
    public boolean update(Appointment a) {
        if (a.getAppointmentId() == null) throw new IllegalArgumentException("ID required for update");
        a.validate();
        return dao.update(a);
    }

    @Override
    public boolean remove(Long appointmentId) {
        if (appointmentId == null || appointmentId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.deleteById(appointmentId);
    }

    @Override
    public Optional<Appointment> getById(Long appointmentId) {
        if (appointmentId == null || appointmentId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.findById(appointmentId);
    }

    @Override
    public List<Appointment> getAll() {
        return dao.findAll();
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        return dao.findByDate(date);
    }

    @Override
    public List<Appointment> findByDoctor(Long doctorId, LocalDate from, LocalDate to) {
        return dao.findByDoctor(doctorId, from, to);
    }

    @Override
    public List<Appointment> findByPatient(Long patientId, LocalDate from, LocalDate to) {
        return dao.findByPatient(patientId, from, to);
    }

    @Override
    public boolean updateStatus(Long appointmentId, String status) {
        if (appointmentId == null || appointmentId <= 0) throw new IllegalArgumentException("Valid ID required");
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status required");
        return dao.updateStatus(appointmentId, status);
    }

    @Override
    public boolean existsSlot(Long doctorId, LocalDateTime dateTime) {
        return dao.existsDoctorSlot(doctorId, dateTime);
    }
}
