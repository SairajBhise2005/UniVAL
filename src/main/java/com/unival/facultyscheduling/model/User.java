package com.unival.facultyscheduling.model;

import java.util.List;

public class User {
    private String id;
    private String email;
    private String name;
    private List<String> roles;
    private String department;

    // Constructor
    public User(String id, String email, String name, List<String> roles, String department) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.department = department;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
