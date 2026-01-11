
package com.amalitech.hospitalmanagementsystem.model;

public class Doctor {
    private Long id;                   // maps to doctor_id
    private String firstName;
    private String lastName;
    private String specialization;
    private Long departmentId;         // FK to departments.department_id
    private String phone;
    private String email;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String specialization,
                  Long departmentId, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.departmentId = departmentId;
        this.phone = phone;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
