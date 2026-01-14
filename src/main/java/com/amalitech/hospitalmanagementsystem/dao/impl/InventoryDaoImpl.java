
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.InventoryDao;
import com.amalitech.hospitalmanagementsystem.model.InventoryItem;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryDaoImpl implements InventoryDao {

    private InventoryItem map(ResultSet rs) throws SQLException {
        InventoryItem i = new InventoryItem();
        i.setItemId(rs.getLong("item_id"));
        i.setName(rs.getString("name"));
        i.setCategory(rs.getString("category"));
        i.setQuantity(rs.getInt("quantity"));
        i.setUnit(rs.getString("unit"));
        i.setReorderLevel(rs.getInt("reorder_level"));
        Timestamp ts = rs.getTimestamp("updated_at");
        i.setUpdatedAt(ts != null ? ts.toLocalDateTime() : null);
        return i;
    }

    @Override
    public Long create(InventoryItem i) {
        i.validate();
        final String sql = """
            INSERT INTO medical_inventory (name, category, quantity, unit, reorder_level, updated_at)
            VALUES (?, ?, ?, ?, ?, NOW())
            RETURNING item_id
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, i.getName());
            ps.setString(2, i.getCategory());
            ps.setInt(3, i.getQuantity());
            ps.setString(4, i.getUnit());
            ps.setInt(5, i.getReorderLevel());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create inventory item failed: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(InventoryItem i) {
        if (i.getItemId() == null) throw new IllegalArgumentException("ID required");
        i.validate();
        final String sql = """
            UPDATE medical_inventory
               SET name=?, category=?, quantity=?, unit=?, reorder_level=?, updated_at=NOW()
             WHERE item_id=?
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, i.getName());
            ps.setString(2, i.getCategory());
            ps.setInt(3, i.getQuantity());
            ps.setString(4, i.getUnit());
            ps.setInt(5, i.getReorderLevel());
            ps.setLong(6, i.getItemId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(Long itemId) {
        final String sql = "DELETE FROM medical_inventory WHERE item_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Optional<InventoryItem> findById(Long itemId) {
        final String sql = "SELECT * FROM medical_inventory WHERE item_id=?";
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {}
        return Optional.empty();
    }

    @Override
    public List<InventoryItem> findAll() {
        final String sql = "SELECT * FROM medical_inventory ORDER BY name";
        List<InventoryItem> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {}
        return out;
    }

    @Override
    public List<InventoryItem> searchByNameOrCategory(String term) {
        final String sql = """
            SELECT * FROM medical_inventory
             WHERE LOWER(name) LIKE LOWER(?) OR LOWER(category) LIKE LOWER(?)
             ORDER BY name
        """;
        List<InventoryItem> out = new ArrayList<>();
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String t = term == null ? "" : term.trim();
            String pattern = "%" + t + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {}
        return out;
    }

    @Override
    public boolean adjustQuantity(Long itemId, int delta) {
        final String sql = """
            UPDATE medical_inventory
               SET quantity = quantity + ?, updated_at = NOW()
             WHERE item_id = ?
        """;
        try (Connection c = DBConnectionUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}

