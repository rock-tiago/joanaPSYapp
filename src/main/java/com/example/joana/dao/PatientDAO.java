package com.example.joana.dao;

import com.example.joana.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private Connection conn;

    public PatientDAO(Connection conn) {
        this.conn = conn;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS patients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "phone TEXT," +
                "email TEXT," +
                "notes TEXT)";
        conn.createStatement().execute(sql);
    }

    public void addPatient(Patient p) throws SQLException {
        String sql = "INSERT INTO patients (name, phone, email, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getPhone());
            stmt.setString(3, p.getEmail());
            stmt.setString(4, p.getNotes());
            stmt.executeUpdate();
        }
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM patients");
        while (rs.next()) {
            patients.add(new Patient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("notes")
            ));
        }
        return patients;
    }
}
