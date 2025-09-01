package com.example.joana.model;

public class Patient {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String notes;

    // Constructors, getters, setters
    public Patient() {}

    public Patient(String name, String phone, String email, String notes) {
        this(-1, name, phone, email, notes);
    }

    public Patient(int id, String name, String phone, String email, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
