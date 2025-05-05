package com.unival.facultyscheduling.model;

/**
 * User represents an individual user within the UniVAL system.
 * <p>
 * This class encapsulates the core attributes and identity of a user, including their unique identifier,
 * email address, display name, assigned roles, and associated department. It is used throughout the application
 * to manage authentication, authorization, and user-specific data and permissions.
 * <p>
 * Core Features:
 * <ul>
 *     <li>Stores user identity and contact information.</li>
 *     <li>Supports multiple roles for flexible access control (e.g., student, faculty, admin).</li>
 *     <li>Associates users with a department for organizational grouping.</li>
 *     <li>Provides getters and setters for all fields, allowing for encapsulated access and modification.</li>
 * </ul>
 * <p>
 * Typical Usage:
 * <pre>
 *     User user = new User(id, email, name, roles, department);
 *     String userEmail = user.getEmail();
 *     List<String> userRoles = user.getRoles();
 * </pre>
 */
import java.util.List;

public class User {
    private String id;
    private String email;
    private String name;
    private List<String> roles;
    private String department;

    /**
     * Constructs a new User with the specified details.
     *
     * @param id         The unique identifier for the user.
     * @param email      The user's email address.
     * @param name       The user's display name.
     * @param roles      The list of roles assigned to the user (e.g., "student", "faculty").
     * @param department The user's associated department.
     */
    public User(String id, String email, String name, List<String> roles, String department) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.department = department;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return The user's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id The user's ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user's email address.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's display name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's display name.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of roles assigned to the user.
     *
     * @return The user's roles.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles assigned to the user.
     *
     * @param roles The user's roles.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Gets the user's associated department.
     *
     * @return The user's department.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the user's associated department.
     *
     * @param department The user's department.
     */
    public void setDepartment(String department) {
        this.department = department;
    }
}
