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
    @FXML private TextArea notesArea;


    private PatientDAO patientDAO;
    private ObservableList<Patient> patientList;

    private Patient currentPatient;
    private Patient resultPatient;

    public Patient getResultPatient() {
        return resultPatient;
    }

    public void setDAOs(PatientDAO patientDAO) throws SQLException {
        this.patientDAO = patientDAO;
        initializeTable();
        loadPatients();
    }

    public void initializeTable() throws SQLException {
        //List<Patient> patients = patientDAO.getAllPatients();
        //patientList = FXCollections.observableArrayList(patients);

        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        phoneColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        //patientTable.setItems(patientList);

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                phoneField.setText(newSel.getPhone());
                emailField.setText(newSel.getEmail());
                notesArea.setText(newSel.getNotes());
            }
        });
    }

    private void loadPatients() throws SQLException {
        List<Patient> patients = patientDAO.getAllPatients();
        patientList = FXCollections.observableArrayList(patients);
        patientTable.setItems(patientList);
    }


    public void setPatient(Patient patient) {
        this.currentPatient = patient;

        if (patient != null) {
            nameField.setText(patient.getName());
            phoneField.setText(patient.getPhone());
            emailField.setText(patient.getEmail());
            notesArea.setText(patient.getNotes());
        }
    }

    @FXML
    public void onSavePatient(ActionEvent actionEvent) {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                MainController.showAlert("Name is required");
                return;
            }

            if (currentPatient != null) {
                // Update existing patient
                currentPatient .setName(name);
                currentPatient .setPhone(phoneField.getText().trim());
                currentPatient .setEmail(emailField.getText().trim());
                currentPatient .setNotes(notesArea.getText().trim());

                patientDAO.updatePatient(currentPatient);
                resultPatient = currentPatient;

                patientTable.refresh();
            } else {
                // Create new patient
                Patient newPatient = new Patient(
                        name,
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        notesArea.getText().trim()
                );
                patientDAO.addPatient(newPatient);
                resultPatient = newPatient;

                patientList.add(newPatient);
                patientTable.getSelectionModel().select(newPatient);
            }

            closeWindow();

        } catch (SQLException e) {
            e.printStackTrace();
            MainController.showAlert("Error saving patient: " + e.getMessage());
        }
    }

    @FXML
    public void onCancel() {
        resultPatient = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onDeletePatient(ActionEvent actionEvent) {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete patient " + selected.getName() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    patientDAO.deletePatient(selected.getId());
                    patientList.remove(selected);
                    clearForm();
                }
            });
        }
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        notesArea.clear();
    }
}
