
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.DepartmentDao;
import com.amalitech.hospitalmanagementsystem.model.Department;
import com.amalitech.hospitalmanagementsystem.service.DepartmentService;

import java.util.List;
import java.util.Optional;

public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentDao dao;

    public DepartmentServiceImpl(DepartmentDao dao) {
        this.dao = dao;
    }

    @Override
    public Long create(Department department) {
        validate(department, true);
        return dao.create(department);
    }

    @Override
    public boolean update(Department department) {
        if (department.getId() == null) throw new IllegalArgumentException("ID required for update");
        validate(department, false);
        return dao.update(department);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.deleteById(id);
    }

    @Override
    public Optional<Department> getById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.findById(id);
    }

    @Override
    public List<Department> getAll() {
        return dao.findAll();
    }

    @Override
    public List<Department> searchByName(String nameLike) {
        return dao.searchByName(nameLike);
    }

    private void validate(Department d, boolean isCreate) {
        if (d == null) throw new IllegalArgumentException("Department cannot be null");
        if (d.getName() == null || d.getName().isBlank())
            throw new IllegalArgumentException("Name is required");
        // Description optional
    }
}
