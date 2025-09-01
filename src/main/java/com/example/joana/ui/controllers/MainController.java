package com.example.joana.ui.controllers;

import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Patient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MainController {

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> nameCol;
    @FXML private TableColumn<Patient, String> phoneCol;
    @FXML private TableColumn<Patient, String> emailCol;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    public void setPatientDAO(PatientDAO dao) {
        this.patientDAO = dao;
        loadPatients();
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
        } catch (Exception e){
            e.printStackTrace();
        }
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
