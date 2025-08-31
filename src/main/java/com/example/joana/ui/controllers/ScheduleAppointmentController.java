package com.example.joana.ui.controllers;


import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Appointment;
import com.example.joana.model.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class ScheduleAppointmentController {

    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextArea notesArea;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    public void setDAOs(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
        loadPatients();
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            patientComboBox.getItems().setAll(patients);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddNewPatient() {
        System.out.println("Open dialog to add a new patient here");
        // After adding, reload patients
        loadPatients();
    }

    @FXML
    public void onSaveAppointment() {
        Patient patient = patientComboBox.getValue();
        if (patient == null) {
            System.out.println("Select or add a patient first!");
            return;
        }

        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String time = timeField.getText();
        String notes = notesArea.getText();

        Appointment appt = new Appointment(0, patient.getId(), date, time, notes);

        try {
            appointmentDAO.addAppointment(appt);
            System.out.println("Appointment saved for: " + patient.getName());
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) patientComboBox.getScene().getWindow();
        stage.close();
    }
}

