package com.example.joana.ui.controllers;

import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class PatientController {

    // Table
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> nameColumn;
    @FXML private TableColumn<Patient, String> phoneColumn;
    @FXML private TableColumn<Patient, String> emailColumn;

    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea notes;


    private PatientDAO patientDAO;
    private ObservableList<Patient> patientList;

    private Patient addedPatient;

    public Patient getAddedPatient() {
        return addedPatient;
    }

    public void setDAOs(PatientDAO patientDAO) throws SQLException {
        this.patientDAO = patientDAO;

        List<Patient> patients = patientDAO.getAllPatients();
        patientList = FXCollections.observableArrayList(patients);

        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        phoneColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        patientTable.setItems(patientList);

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                phoneField.setText(newSel.getPhone());
                emailField.setText(newSel.getEmail());
                notes.setText(newSel.getNotes());
            }
        });
    }

    public void setPatient(Patient patient) {
        //TODO finish up updating already existing patient
        //update already existing patient
        addedPatient = patient;
        nameField.setText(patient.getName());
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail());
        notes.setText(patient.getNotes());
    }

    public void onSavePatient(ActionEvent actionEvent) throws SQLException {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String extraNotes = notes.getText();

        // TODO: validate input
        if (name.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Name is required").show();
            return;
        }

        addedPatient = new Patient(name, phone, email, extraNotes);
        patientDAO.addPatient(addedPatient);

        System.out.println("Saving patient: " + name + " / " + email);

        // close dialog
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();

        System.out.println("Saving patient: " + name + " / " + email);
    }

    public void onCancel() {
        nameField.getScene().getWindow().hide();
    }

    public void onDeletePatient(ActionEvent actionEvent) {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete patient " + selected.getName() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    patientDAO.deletePatient(selected.getId()); // DAO call
                    patientList.remove(selected); // update UI
                }
            });
        }
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        notes.clear();
        patientTable.getSelectionModel().clearSelection();
    }
}
