package com.example.joana.model;

import com.example.joana.dao.PatientDAO;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private int patientId;
    private LocalDate date;
    private LocalTime time;
    private String notes;

    public Appointment() {}

    public Appointment(int id, int patientId, LocalDate date, LocalTime time, String notes) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
