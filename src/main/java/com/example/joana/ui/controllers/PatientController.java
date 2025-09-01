package com.example.joana.ui.controllers;

import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(name);
            selected.setPhone(phone);
            selected.setEmail(email);
            selected.setNotes(extraNotes);
            patientDAO.updatePatient(selected);
        } else {
            Patient newPatient = new Patient(name, phone, email, extraNotes);
            patientDAO.addPatient(newPatient);
            patientList.add(newPatient);
        }

        clearForm();
        // TODO: use patientDAO to save new or updated patient

        System.out.println("Saving patient: " + name + " / " + email);
    }

    public void onCancel() {
        nameField.getScene().getWindow().hide();
    }

    public void onDeletePatient(ActionEvent actionEvent) {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            patientDAO.deletePatient(selected.getId()); // TODO: implement in DAO
            patientList.remove(selected);
            clearForm();
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
