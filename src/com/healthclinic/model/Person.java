package com.healthclinic.model;

import java.io.Serializable;

/**
 * Abstract base class representing a Person in the Health Clinic System.
 * Demonstrates Abstraction and Inheritance (OOP & SOLID - OCP, LSP).
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String phone;
    protected String email;

    public Person() {
    }

    public Person(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Polymorphic method to get human-readable details of the person.
     */
    public abstract String getRoleDescription();

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}
