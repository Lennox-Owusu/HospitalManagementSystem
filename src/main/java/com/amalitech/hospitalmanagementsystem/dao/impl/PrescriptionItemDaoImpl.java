
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.PrescriptionItemDao;
import com.amalitech.hospitalmanagementsystem.model.PrescriptionItem;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrescriptionItemDaoImpl implements PrescriptionItemDao {

    private PrescriptionItem map(ResultSet rs) throws SQLException {
        PrescriptionItem it = new PrescriptionItem();
        it.setItemId(rs.getLong("item_id"));
        it.setPrescriptionId(rs.getLong("prescription_id"));
        it.setMedicationName(rs.getString("medication_name"));
        it.setDosage(rs.getString("dosage"));
        it.setFrequency(rs.getString("frequency"));
        it.setDurationDays(rs.getInt("duration_days"));
        it.setInstructions(rs.getString("instructions"));
        return it;
    }

    @Override
    public Long create(PrescriptionItem item) {
        item.validate();
        final String sql = """
            INSERT INTO prescription_items
            (prescription_id, medication_name, dosage, frequency, duration_days, instructions)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING item_id
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, item.getPrescriptionId());
            ps.setString(2, item.getMedicationName());
            ps.setString(3, item.getDosage());
            ps.setString(4, item.getFrequency());
            ps.setInt(5, item.getDurationDays());
            ps.setString(6, item.getInstructions());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create prescription item failed: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(PrescriptionItem item) {
        if (item.getItemId() == null) throw new IllegalArgumentException("Item ID required for update");
        item.validate();
        final String sql = """
            UPDATE prescription_items
            SET prescription_id=?, medication_name=?, dosage=?, frequency=?, duration_days=?, instructions=?
            WHERE item_id=?
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, item.getPrescriptionId());
            ps.setString(2, item.getMedicationName());
            ps.setString(3, item.getDosage());
            ps.setString(4, item.getFrequency());
            ps.setInt(5, item.getDurationDays());
            ps.setString(6, item.getInstructions());
            ps.setLong(7, item.getItemId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(Long itemId) {
        final String sql = "DELETE FROM prescription_items WHERE item_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Optional<PrescriptionItem> findById(Long itemId) {
        final String sql = "SELECT * FROM prescription_items WHERE item_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { }
        return Optional.empty();
    }

    @Override
    public List<PrescriptionItem> findByPrescription(Long prescriptionId) {
        final String sql = "SELECT * FROM prescription_items WHERE prescription_id=? ORDER BY item_id";
        List<PrescriptionItem> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, prescriptionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { }
        return out;
    }

    @Override
    public boolean deleteByPrescription(Long prescriptionId) {
        final String sql = "DELETE FROM prescription_items WHERE prescription_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, prescriptionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
