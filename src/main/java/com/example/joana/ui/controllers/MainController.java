package com.example.joana.ui.controllers;

import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Appointment;
import com.example.joana.model.Patient;
import com.example.joana.ui.list.AppointmentItem;
import com.example.joana.ui.list.CalendarItem;
import com.example.joana.ui.list.DayHeader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class MainController {

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> nameCol;
    @FXML private TableColumn<Patient, String> phoneCol;
    @FXML private TableColumn<Patient, String> emailCol;
    @FXML private ListView<CalendarItem> appointmentList;
    @FXML private Button addPatient;
    @FXML private Button scheduleAppointment;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    @FXML private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        appointmentList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CalendarItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (item instanceof DayHeader header) {
                    LocalDate date = header.getDate();
                    setText(date.format(DateTimeFormatter.ofPattern("d MMM, EEEE")));
                    setStyle("-fx-font-weight: bold; -fx-background-color: #e0e0e0; -fx-padding: 5;");
                }
                else if (item instanceof AppointmentItem apptItem) {
                    Appointment appt = apptItem.getAppointment();

                    String timeStr = "??:??";
                    if (appt.getTime() != null) {
                        timeStr = appt.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    }

                    String patientName = "Unknown";
                    try {
                        patientName = resolvePatientName(appt.getPatientId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    setText("   " + timeStr + " - " + patientName);
                    setStyle("-fx-padding: 3;");
                }
            }
        });
//        try {
//            refreshPatients();
//            refreshAppointments();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private String resolvePatientName(int patientId) throws SQLException {
        Patient patient = patientDAO.getPatientById(patientId);
        return patient != null ? patient.getName() : "Unknown";
    }

    public void setDAOs(PatientDAO patientDAO, AppointmentDAO appDAO) throws SQLException {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appDAO;
        refreshAllData();
    }

    private void refreshPatients() throws SQLException {
        patientTable.getItems().setAll(patientDAO.getAllPatients());
    }

    private void refreshAppointments() throws SQLException {
        appointmentList.getItems().clear();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            appointmentList.getItems().add(new DayHeader(date));

            var appointments = appointmentDAO.getAppointmentsForDay(date);
            appointments.sort(Comparator.comparing(Appointment::getTime));

            for (var appt : appointments) {
                appointmentList.getItems().add(new AppointmentItem(appt));
            }
        }
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

            stage.setOnHidden(e -> {
                // Refresh main table when patient dialog closes
                try {
                    refreshPatients();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            stage.showAndWait();
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
    private void onScheduleAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/joana/ScheduleNewAppointment.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            ScheduleAppointmentController controller = loader.getController();
            controller.setDAOs(patientDAO, appointmentDAO);

            // Listen for when the dialog closes to refresh data
            stage.setOnHidden(e -> refreshAllData());
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshAllData() {
        try {
            refreshPatients();
            refreshAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }
}
