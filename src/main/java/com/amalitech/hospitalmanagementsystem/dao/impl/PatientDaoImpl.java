
package com.amalitech.hospitalmanagementsystem.dao.impl;

import com.amalitech.hospitalmanagementsystem.dao.PatientDao;
import com.amalitech.hospitalmanagementsystem.model.Patient;
import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDaoImpl implements PatientDao {
    private static final Logger log = LoggerFactory.getLogger(PatientDaoImpl.class);

    // Column names (DB)
    private static final String COL_ID         = "patient_id";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME  = "last_name";
    private static final String COL_SEX        = "sex";               // maps from Java 'gender'
    private static final String COL_DOB        = "date_of_birth";
    private static final String COL_PHONE      = "phone";
    private static final String COL_EMAIL      = "email";
    private static final String COL_ADDRESS    = "address";

    @Override
    public Long create(Patient p) {
        final String sql = """
            INSERT INTO patients (first_name, last_name, sex, date_of_birth, phone, email, address)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING patient_id
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setString(3, p.getGender());           // Java 'gender' -> DB 'sex'
            ps.setObject(4, p.getDateOfBirth());      // LocalDate supported by Pg JDBC
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getEmail());
            ps.setString(7, p.getAddress());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);          // patient_id
                    p.setId(id);
                    return id;
                }
            }
            return null;
        } catch (SQLException e) {
            log.error("Create patient failed", e);
            throw new RuntimeException("Create patient failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Patient p) {
        final String sql = """
            UPDATE patients
            SET first_name=?, last_name=?, sex=?, date_of_birth=?, phone=?, email=?, address=?
            WHERE patient_id=?
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setString(3, p.getGender());
            ps.setObject(4, p.getDateOfBirth());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getEmail());
            ps.setString(7, p.getAddress());
            ps.setLong(8, p.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("Update patient failed", e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM patients WHERE patient_id=?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("Delete patient failed", e);
            return false;
        }
    }

    @Override
    public Optional<Patient> findById(Long id) {
        final String sql = """
            SELECT patient_id, first_name, last_name, sex, date_of_birth, phone, email, address
            FROM patients
            WHERE patient_id=?
        """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("FindById failed", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Patient> findAll() {
        final String sql = """
            SELECT patient_id, first_name, last_name, sex, date_of_birth, phone, email, address
            FROM patients
            ORDER BY patient_id
        """;
        List<Patient> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return list;
    }

    @Override
    public List<Patient> searchByName(String nameLike) {
        final String sql = """
            SELECT patient_id, first_name, last_name, sex, date_of_birth, phone, email, address
            FROM patients
            WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)
            ORDER BY last_name, first_name
        """;
        List<Patient> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String term = nameLike == null ? "" : nameLike.trim();
            String pattern = "%" + term + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("searchByName failed", e);
        }
        return list;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getLong(COL_ID));                  // patient_id -> id
        p.setFirstName(rs.getString(COL_FIRST_NAME));
        p.setLastName(rs.getString(COL_LAST_NAME));
        p.setGender(rs.getString(COL_SEX));           // sex -> gender
        Date dob = rs.getDate(COL_DOB);
        p.setDateOfBirth(dob != null ? dob.toLocalDate() : null);
        p.setPhone(rs.getString(COL_PHONE));
        p.setEmail(rs.getString(COL_EMAIL));
        p.setAddress(rs.getString(COL_ADDRESS));
        return p;
    }
}
