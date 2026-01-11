
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Long create(Department department);
    boolean update(Department department);
    boolean delete(Long id);

    Optional<Department> getById(Long id);
    List<Department> getAll();
    List<Department> searchByName(String nameLike);
}
