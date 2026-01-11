
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentDao {
    Long create(Department department);
    boolean update(Department department);
    boolean deleteById(Long id);

    Optional<Department> findById(Long id);
    List<Department> findAll();
    List<Department> searchByName(String nameLike);
}
