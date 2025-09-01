package com.example.joana.ui.controllers;

import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Patient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MainController {

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> nameCol;
    @FXML private TableColumn<Patient, String> phoneCol;
    @FXML private TableColumn<Patient, String> emailCol;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    @FXML private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    public void setPatientDAO(PatientDAO dao) throws SQLException {
        this.patientDAO = dao;
        refreshPatients();
    }

    private void refreshPatients() throws SQLException {
        patientTable.getItems().setAll(patientDAO.getAllPatients());
    }

    private void loadPatients() {
        try {
            patientTable.getItems().addAll(patientDAO.getAllPatients());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddPatient() {
        System.out.println("Open Add Patient Dialog here");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/joana/AddPatient.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Patient");

            PatientController controller = loader.getController();
            controller.setDAOs(patientDAO);

            stage.showAndWait();

            Patient newPatient = controller.getAddedPatient();
            if (newPatient != null) {
                patientTable.getItems().add(newPatient);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpdatePatient() { //TODO fix this - creates new instead of updating || does not add to DB
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a patient to update").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/joana/AddPatient.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Update Patient");

            PatientController controller = loader.getController();
            controller.setDAOs(patientDAO);
            controller.setPatient(selected); // preload fields

            stage.showAndWait();

            patientTable.refresh(); // update table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a patient to delete").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete patient " + selected.getName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                patientDAO.deletePatient(selected.getId());
                patientTable.getItems().remove(selected);
            }
        });
    }


    @FXML
    public void onScheduleAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/joana/ScheduleNewAppointment.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Schedule Appointment");

            // Inject DAOs into controller
            ScheduleAppointmentController controller = loader.getController();
            controller.setDAOs(patientDAO, appointmentDAO);

            stage.showAndWait();

            // Reload patients if needed
            loadPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
