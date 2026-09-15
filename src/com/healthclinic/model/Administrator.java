package com.healthclinic.model;

/**
 * Represents a Clinic Administrator.
 */
public class Administrator extends Person {
    private static final long serialVersionUID = 1L;

    private String username;
    private String role; // e.g. "SUPER_ADMIN", "CLINIC_STAFF"

    public Administrator() {
        super();
    }

    public Administrator(String id, String name, String phone, String email,
                         String username, String role) {
        super(id, name, phone, email);
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator: " + username + " (" + role + ")";
    }
}
