
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.DoctorDao;
import com.amalitech.hospitalmanagementsystem.model.Doctor;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDaoImpl implements DoctorDao {
    private static final Logger log = LoggerFactory.getLogger(DoctorDaoImpl.class);

    // DB column constants
    private static final String COL_ID            = "doctor_id";
    private static final String COL_FIRST_NAME    = "first_name";
    private static final String COL_LAST_NAME     = "last_name";
    private static final String COL_SPEC          = "specialization";
    private static final String COL_DEPT_ID       = "department_id";
    private static final String COL_PHONE         = "phone";
    private static final String COL_EMAIL         = "email";

    @Override
    public Long create(Doctor d) {
        final String sql = """
            INSERT INTO public.doctors (first_name, last_name, specialization, department_id, phone, email)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING doctor_id
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            if (d.getDepartmentId() == null) ps.setNull(4, Types.BIGINT); else ps.setLong(4, d.getDepartmentId());
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getEmail());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    d.setId(id);
                    return id;
                }
            }
            return null;
        } catch (SQLException e) {
            log.error("Create doctor failed", e);
            throw new RuntimeException("Create doctor failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Doctor d) {
        final String sql = """
            UPDATE public.doctors
            SET first_name=?, last_name=?, specialization=?, department_id=?, phone=?, email=?
            WHERE doctor_id=?
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            if (d.getDepartmentId() == null) ps.setNull(4, Types.BIGINT); else ps.setLong(4, d.getDepartmentId());
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getEmail());
            ps.setLong(7, d.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("Update doctor failed", e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM public.doctors WHERE doctor_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("Delete doctor failed", e);
            return false;
        }
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        final String sql = """
            SELECT doctor_id, first_name, last_name, specialization, department_id, phone, email
            FROM public.doctors
            WHERE doctor_id=?
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Find doctor failed", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Doctor> findAll() {
        final String sql = """
            SELECT doctor_id, first_name, last_name, specialization, department_id, phone, email
            FROM public.doctors
            ORDER BY doctor_id
        """;
        List<Doctor> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("List doctors failed", e);
        }
        return list;
    }

    @Override
    public List<Doctor> searchByNameOrSpecialization(String term) {
        final String sql = """
            SELECT doctor_id, first_name, last_name, specialization, department_id, phone, email
            FROM public.doctors
            WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)
               OR LOWER(specialization) LIKE LOWER(?)
            ORDER BY last_name, first_name
        """;
        List<Doctor> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String t = term == null ? "" : term.trim();
            String pattern = "%" + t + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Search doctors failed", e);
        }
        return list;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getLong(COL_ID));
        d.setFirstName(rs.getString(COL_FIRST_NAME));
        d.setLastName(rs.getString(COL_LAST_NAME));
        d.setSpecialization(rs.getString(COL_SPEC));
        long dept = rs.getLong(COL_DEPT_ID);
        d.setDepartmentId(rs.wasNull() ? null : dept);
        d.setPhone(rs.getString(COL_PHONE));
        d.setEmail(rs.getString(COL_EMAIL));
        return d;
    }
}
