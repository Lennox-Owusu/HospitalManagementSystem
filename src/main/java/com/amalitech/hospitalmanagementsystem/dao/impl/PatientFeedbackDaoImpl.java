
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.PatientFeedbackDao;
import com.amalitech.hospitalmanagementsystem.model.PatientFeedback;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientFeedbackDaoImpl implements PatientFeedbackDao {

    private PatientFeedback map(ResultSet rs) throws SQLException {
        PatientFeedback f = new PatientFeedback();
        f.setFeedbackId(rs.getLong("feedback_id"));
        f.setPatientId(rs.getLong("patient_id"));
        long did = rs.getLong("doctor_id");
        f.setDoctorId(rs.wasNull() ? null : did);
        f.setRating(rs.getInt("rating"));
        f.setComments(rs.getString("comments"));
        Timestamp ts = rs.getTimestamp("created_at");
        f.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return f;
    }

    @Override
    public Long create(PatientFeedback f) {
        f.validate();
        final String sql = """
            INSERT INTO patient_feedback (patient_id, doctor_id, rating, comments, created_at)
            VALUES (?, ?, ?, ?, NOW())
            RETURNING feedback_id
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, f.getPatientId());
            if (f.getDoctorId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, f.getDoctorId());
            ps.setInt(3, f.getRating());
            ps.setString(4, f.getComments());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create feedback failed: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(PatientFeedback f) {
        if (f.getFeedbackId() == null) throw new IllegalArgumentException("ID required");
        f.validate();
        final String sql = """
            UPDATE patient_feedback
               SET patient_id=?, doctor_id=?, rating=?, comments=?
             WHERE feedback_id=?
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, f.getPatientId());
            if (f.getDoctorId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, f.getDoctorId());
            ps.setInt(3, f.getRating());
            ps.setString(4, f.getComments());
            ps.setLong(5, f.getFeedbackId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(Long feedbackId) {
        final String sql = "DELETE FROM patient_feedback WHERE feedback_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, feedbackId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Optional<PatientFeedback> findById(Long feedbackId) {
        final String sql = "SELECT * FROM patient_feedback WHERE feedback_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, feedbackId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {}
        return Optional.empty();
    }

    @Override
    public List<PatientFeedback> findAll() {
        final String sql = "SELECT * FROM patient_feedback ORDER BY created_at DESC";
        List<PatientFeedback> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {}
        return out;
    }

    @Override
    public List<PatientFeedback> findByPatient(Long patientId) {
        final String sql = "SELECT * FROM patient_feedback WHERE patient_id=? ORDER BY created_at DESC";
        List<PatientFeedback> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {}
        return out;
    }

    @Override
    public List<PatientFeedback> findByDoctor(Long doctorId) {
        final String sql = "SELECT * FROM patient_feedback WHERE doctor_id=? ORDER BY created_at DESC";
        List<PatientFeedback> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {}
        return out;
    }
}
