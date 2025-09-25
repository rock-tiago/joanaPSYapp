package com.example.joana.ui.controllers;

import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Appointment;
import com.example.joana.model.Patient;
import com.example.joana.ui.list.AppointmentItem;
import com.example.joana.ui.list.CalendarItem;
import com.example.joana.ui.list.DayHeader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML public ScrollPane appointmentsContainer;
    @FXML public HBox bottomButtons;
    @FXML public TextArea notesFlow;
    @FXML public VBox sidebar;
    @FXML private VBox calendarSection;
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> nameCol;
    @FXML private TableColumn<Patient, String> phoneCol;
    @FXML private TableColumn<Patient, String> emailCol;
    @FXML private HBox calendarContainer;
    @FXML private VBox container;
    @FXML private Button addPatient;
    @FXML private Button scheduleAppointment;
    @FXML private Button deletePatient;
    @FXML private Button updatePatient;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    @FXML private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        if (calendarContainer != null) {
            calendarContainer.getChildren().clear();
            calendarContainer.setSpacing(10);
            calendarContainer.setStyle("-fx-padding: 10;");
        }
        VBox.setVgrow(patientTable, Priority.ALWAYS);
        VBox.setVgrow(appointmentsContainer, Priority.ALWAYS);
        patientTable.setMinHeight(0);
        patientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        patientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String notes = newValue.getNotes();
                if (notes != null) {
                    notesFlow.setText(notes);
                } else {
                    notesFlow.setText("No notes for this patient.");
                }
            }
        });
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
        calendarContainer.getChildren().clear();
        LocalDate today = LocalDate.now();
        List<VBox> dayBoxes = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            LocalDate date = today.plusDays(i);

            VBox dayBox = new VBox(10);
            dayBox.setMinWidth(160); // optional: give each column a consistent width
            dayBox.setStyle("""
            -fx-background-color: #f9f9fb;
            -fx-border-color: #e3e3e8;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 8;
        """);

            Label header = new Label(date.format(DateTimeFormatter.ofPattern("EEE, d MMM")));
            header.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #333;");

            dayBox.getChildren().add(header);

            var appts = appointmentDAO.getAppointmentsForDay(date);
            appts.sort(Comparator.comparing(Appointment::getTime));

            if (appts.isEmpty()) {
                Label noAppts = new Label("No appointments");
                noAppts.setStyle("-fx-text-fill: #999;");
                dayBox.getChildren().add(noAppts);
            } else {
                for (var appt : appts) {
                    String timeStr = (appt.getTime() != null)
                            ? appt.getTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                            : "--:--";
                    String patientName;
                    try {
                        patientName = resolvePatientName(appt.getPatientId());
                    } catch (SQLException e) {
                        patientName = "Unknown";
                    }
                    Label pill = new Label(timeStr + "  –  " + patientName);
                    pill.setWrapText(true);
                    pill.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #e3e3e8;
                    -fx-background-radius: 6;
                    -fx-border-radius: 6;
                    -fx-padding: 6;
                """);
                    dayBox.getChildren().add(pill);
                }
            }
            dayBoxes.add(dayBox);
            calendarContainer.getChildren().add(dayBox);
        }

        Platform.runLater(() -> {
            calendarContainer.applyCss();
            calendarContainer.layout();

            // Measure the actual height of each day column after layout
            double maxDayHeight = dayBoxes.stream()
                    .mapToDouble(box -> box.getLayoutBounds().getHeight())
                    .max()
                    .orElse(200); // fallback if no appointments

            double targetHeight = maxDayHeight + 20; // add a little padding under

            // Fix all day columns to that height
            for (VBox box : dayBoxes) {
                box.setMinHeight(targetHeight);
                box.setPrefHeight(targetHeight);
            }

            calendarContainer.setMinHeight(targetHeight);
            calendarContainer.setPrefHeight(targetHeight);
            calendarContainer.setMaxHeight(targetHeight);

            // This is KEY: set the outer container to fixed height
            calendarSection.setMinHeight(targetHeight + 20); // ScrollPane padding
            calendarSection.setPrefHeight(targetHeight + 20);
            calendarSection.setMaxHeight(targetHeight + 20);

            appointmentsContainer.setFitToHeight(false);
        });

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
    private void onUpdatePatient() {
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
            controller.setPatient(selected);
            controller.setDAOs(patientDAO);

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
