package com.example.joana.dao;

// AppointmentDAO.java
import com.example.joana.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private Connection conn;

    public AppointmentDAO(Connection conn) {
        this.conn = conn;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patientId INTEGER," +
                "date TEXT," +
                "time TEXT," +
                "notes TEXT," +
                "FOREIGN KEY(patientId) REFERENCES patients(id))";
        conn.createStatement().execute(sql);
    }

    public void addAppointment(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments (patientId, date, time, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getPatientId());
            stmt.setString(2, a.getDate());
            stmt.setString(3, a.getTime());
            stmt.setString(4, a.getNotes());
            stmt.executeUpdate();
        }
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patientId=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patientId"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("notes")
                ));
            }
        }
        return appointments;
    }
}
