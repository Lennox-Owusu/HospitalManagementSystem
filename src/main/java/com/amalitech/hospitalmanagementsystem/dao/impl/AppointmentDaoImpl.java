
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.AppointmentDao;
import com.amalitech.hospitalmanagementsystem.model.Appointment;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDaoImpl implements AppointmentDao {

    private Appointment map(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getLong("appointment_id"));
        a.setPatientId(rs.getLong("patient_id"));
        a.setDoctorId(rs.getLong("doctor_id"));

        Timestamp ts = rs.getTimestamp("appointment_date");
        a.setAppointmentDate(ts != null ? ts.toLocalDateTime() : null);

        a.setStatus(rs.getString("status"));
        a.setReason(rs.getString("reason"));

        Timestamp created = rs.getTimestamp("created_at");
        a.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        return a;
    }

    @Override
    public Long create(Appointment a) {
        a.validate();
        final String sql = """
            INSERT INTO appointments (patient_id, doctor_id, appointment_date, status, reason, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())
            RETURNING appointment_id
            """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, a.getPatientId());
            ps.setLong(2, a.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getReason());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create appointment: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public boolean update(Appointment a) {
        a.validate();
        final String sql = """
            UPDATE appointments SET
                patient_id = ?, doctor_id = ?, appointment_date = ?, status = ?, reason = ?
            WHERE appointment_id = ?
            """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, a.getPatientId());
            ps.setLong(2, a.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getReason());
            ps.setLong(6, a.getAppointmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(Long appointmentId) {
        final String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        final String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() {
        final String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC";
        List<Appointment> out = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        final String sql = """
            SELECT * FROM appointments
            WHERE appointment_date >= ?::date
              AND appointment_date < (?::date + INTERVAL '1 day')
            ORDER BY appointment_date ASC
            """;
        List<Appointment> out = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    @Override
    public List<Appointment> findByDoctor(Long doctorId, LocalDate fromDate, LocalDate toDate) {
        final String sql = """
            SELECT * FROM appointments
            WHERE doctor_id = ?
              AND appointment_date >= ?::date
              AND appointment_date < (?::date + INTERVAL '1 day')
            ORDER BY appointment_date ASC
            """;
        List<Appointment> out = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    @Override
    public List<Appointment> findByPatient(Long patientId, LocalDate fromDate, LocalDate toDate) {
        final String sql = """
            SELECT * FROM appointments
            WHERE patient_id = ?
              AND appointment_date >= ?::date
              AND appointment_date < (?::date + INTERVAL '1 day')
            ORDER BY appointment_date ASC
            """;
        List<Appointment> out = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    @Override
    public boolean updateStatus(Long appointmentId, String status) {
        final String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsDoctorSlot(Long doctorId, LocalDateTime dt) {
        final String sql = """
            SELECT 1 FROM appointments
            WHERE doctor_id = ?
              AND appointment_date = ?
              AND status <> 'CANCELLED'
            """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setTimestamp(2, Timestamp.valueOf(dt));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
