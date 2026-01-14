
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.PrescriptionDao;
import com.amalitech.hospitalmanagementsystem.model.Prescription;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrescriptionDaoImpl implements PrescriptionDao {

    private Prescription map(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setPrescriptionId(rs.getLong("prescription_id"));
        p.setPatientId(rs.getLong("patient_id"));
        p.setDoctorId(rs.getLong("doctor_id"));
        Timestamp ts = rs.getTimestamp("issued_at");
        p.setIssuedAt(ts != null ? ts.toLocalDateTime() : null);
        p.setNotes(rs.getString("notes"));
        return p;
    }

    @Override
    public Long create(Prescription p) {
        p.validate();
        final String sql = """
            INSERT INTO prescriptions (patient_id, doctor_id, issued_at, notes)
            VALUES (?, ?, COALESCE(?, NOW()), ?)
            RETURNING prescription_id
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.getPatientId());
            ps.setLong(2, p.getDoctorId());
            if (p.getIssuedAt() == null) ps.setNull(3, Types.TIMESTAMP);
            else ps.setTimestamp(3, Timestamp.valueOf(p.getIssuedAt()));
            ps.setString(4, p.getNotes());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create prescription failed: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(Prescription p) {
        if (p.getPrescriptionId() == null) throw new IllegalArgumentException("ID required for update");
        p.validate();
        final String sql = """
            UPDATE prescriptions
            SET patient_id=?, doctor_id=?, issued_at=?, notes=?
            WHERE prescription_id=?
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.getPatientId());
            ps.setLong(2, p.getDoctorId());
            ps.setTimestamp(3, p.getIssuedAt() == null ? null : Timestamp.valueOf(p.getIssuedAt()));
            ps.setString(4, p.getNotes());
            ps.setLong(5, p.getPrescriptionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM prescriptions WHERE prescription_id = ?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Optional<Prescription> findById(Long id) {
        final String sql = "SELECT * FROM prescriptions WHERE prescription_id = ?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { }
        return Optional.empty();
    }

    @Override
    public List<Prescription> findAll() {
        final String sql = "SELECT * FROM prescriptions ORDER BY issued_at DESC";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { }
        return out;
    }

    @Override
    public List<Prescription> findByPatient(Long patientId) {
        final String sql = "SELECT * FROM prescriptions WHERE patient_id=? ORDER BY issued_at DESC";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { }
        return out;
    }

    @Override
    public List<Prescription> findByDoctor(Long doctorId) {
        final String sql = "SELECT * FROM prescriptions WHERE doctor_id=? ORDER BY issued_at DESC";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { }
        return out;
    }

    @Override
    public List<Prescription> findByDate(LocalDate day) {
        final String sql = """
            SELECT * FROM prescriptions
            WHERE issued_at >= ?::date
              AND issued_at <  (?::date + INTERVAL '1 day')
            ORDER BY issued_at DESC
        """;
        List<Prescription> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(day));
            ps.setDate(2, Date.valueOf(day));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { }
        return out;
    }
}
