package com.example.joana.model;

public class Appointment {
    private int id;
    private int patientId;
    private String date;
    private String time;
    private String notes;

    public Appointment() {}

    public Appointment(int id, int patientId, String date, String time, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.time = time;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
