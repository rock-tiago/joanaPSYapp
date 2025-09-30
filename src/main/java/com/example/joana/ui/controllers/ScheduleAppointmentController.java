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

    @FXML public ComboBox timePicker;
    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextArea notesArea;
    @FXML private Button saveAppointment;
    @FXML private Button cancel;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    private void setupPatientComboBox() {
        // Display names instead of Patient.toString()
        patientComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.getName());
            }
        });

        // Also fix the "selected item" display
        patientComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.getName());
            }
        });
    }

    public void setDAOs(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
        setupPatientComboBox();
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

