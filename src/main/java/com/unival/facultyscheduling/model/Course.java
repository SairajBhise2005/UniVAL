package com.unival.facultyscheduling.model;

public class Course {
    private String id;
    private String code;
    private String name;
    private String department;

    public Course(String id, String code, String name, String department) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.department = department;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
} 