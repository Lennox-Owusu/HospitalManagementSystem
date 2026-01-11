
package com.amalitech.hospitalmanagementsystem.model;

public class Department {
    private Long id;           // maps to department_id
    private String name;
    private String description;

    public Department() {}
    public Department(Long id, String name) {
        this.id = id; this.name = name;
    }
    public Department(Long id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override public String toString() {
        // So ComboBox shows the name automatically
        return name != null ? name : String.valueOf(id);
    }
}
