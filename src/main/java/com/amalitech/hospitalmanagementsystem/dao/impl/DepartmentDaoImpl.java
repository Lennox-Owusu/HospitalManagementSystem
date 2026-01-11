
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.DepartmentDao;
import com.amalitech.hospitalmanagementsystem.model.Department;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImpl implements DepartmentDao {
    private static final Logger log = LoggerFactory.getLogger(DepartmentDaoImpl.class);

    // DB column constants
    private static final String COL_ID   = "department_id";
    private static final String COL_NAME = "name";
    private static final String COL_DESC = "description";

    @Override
    public Long create(Department d) {
        final String sql = """
            INSERT INTO public.departments (name, description)
            VALUES (?, ?)
            RETURNING department_id
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getDescription());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    d.setId(id);
                    return id;
                }
            }
            return null;
        } catch (SQLException e) {
            log.error("Create department failed", e);
            throw new RuntimeException("Create department failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Department d) {
        final String sql = """
            UPDATE public.departments
            SET name=?, description=?
            WHERE department_id=?
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getDescription());
            ps.setLong(3, d.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("Update department failed", e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM public.departments WHERE department_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("Delete department failed", e);
            return false;
        }
    }

    @Override
    public Optional<Department> findById(Long id) {
        final String sql = """
            SELECT department_id, name, description
            FROM public.departments
            WHERE department_id=?
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Find department failed", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Department> findAll() {
        final String sql = """
            SELECT department_id, name, description
            FROM public.departments
            ORDER BY name
        """;
        List<Department> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("List departments failed", e);
        }
        return list;
    }

    @Override
    public List<Department> searchByName(String nameLike) {
        final String sql = """
            SELECT department_id, name, description
            FROM public.departments
            WHERE LOWER(name) LIKE LOWER(?)
            ORDER BY name
        """;
        List<Department> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String term = nameLike == null ? "" : nameLike.trim();
            String pattern = "%" + term + "%";
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Search departments failed", e);
        }
        return list;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getLong(COL_ID));
        d.setName(rs.getString(COL_NAME));
        d.setDescription(rs.getString(COL_DESC));
        return d;
    }
}
