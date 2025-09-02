package com.example.joana.dao;

// AppointmentDAO.java
import com.example.joana.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
                "patient_id INTEGER," +
                "date TEXT," +
                "time TEXT," +
                "notes TEXT," +
                "FOREIGN KEY(patient_id) REFERENCES patients(id))";
        conn.createStatement().execute(sql);
    }

    public void addAppointment(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, date, time, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getPatientId());
            stmt.setString(2, a.getDate().toString());
            stmt.setString(3, a.getTime().toString());
            stmt.setString(4, a.getNotes());
            stmt.executeUpdate();
        }
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate localDate = rs.getString("date") != null ? LocalDate.parse(rs.getString("date")) : null;
                LocalTime localTime = rs.getString("time") != null ? LocalTime.parse(rs.getString("time")) : null;
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patientId"),
                        localDate,
                        localTime,
                        rs.getString("notes")
                ));
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsForDay(LocalDate date) throws SQLException {
        String sql = "SELECT id, patient_id, date, time, notes FROM appointments WHERE date = ? ORDER BY time ASC";
        List<Appointment> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date.toString()); // store date as 'YYYY-MM-DD' in DB
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int patientId = rs.getInt("patient_id");
                    // adapt if your DB stores date/time in other formats/columns
                    String dateStr = rs.getString("date");     // expected YYYY-MM-DD
                    String timeStr = rs.getString("time");     // expected HH:mm or HH:mm:ss
                    LocalDate localDate = dateStr != null ? LocalDate.parse(dateStr) : null;
                    LocalTime localTime = timeStr != null ? LocalTime.parse(timeStr) : null;

                    String notes = null;
                    try { notes = rs.getString("notes"); } catch (SQLException ignored) {}

                    // Adjust this constructor call to match your Appointment model
                    Appointment appt = new Appointment(id, patientId, localDate, localTime, notes);
                    list.add(appt);
                }
            }
        }

        return list;
    }
}
