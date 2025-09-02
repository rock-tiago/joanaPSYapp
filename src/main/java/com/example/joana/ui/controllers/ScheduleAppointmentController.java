package com.example.joana.ui.controllers;


import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Appointment;
import com.example.joana.model.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
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
            patientComboBox.getItems().setAll(patientDAO.getAllPatients());
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
    public void onSaveAppointment(ActionEvent event) {
        Patient patient = patientComboBox.getValue();
        if (patient == null) {
            MainController.showAlert("Select a patient first!");
            return;
        }

        try {
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.parse(timeField.getText());
            String notes = notesArea.getText();

            Appointment appt = new Appointment(0, patient.getId(), date, time, notes);
            appointmentDAO.addAppointment(appt);

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            MainController.showAlert("Error saving appointment: " + e.getMessage());
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

