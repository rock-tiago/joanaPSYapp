package com.example.joana.ui.controllers;

import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.model.Appointment;
import com.example.joana.model.Patient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML private ScrollPane appointmentsContainer;
    @FXML private StackPane contentArea;
    @FXML private VBox calendarSection;
    @FXML private HBox calendarContainer;
    @FXML private VBox container;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    public void setDAOs(PatientDAO patientDAO, AppointmentDAO appDAO) throws SQLException {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appDAO;
        loadContent("/com/example/joana/MainMenu.fxml");
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

            Object controller = loader.getController();
            if (controller instanceof MainMenuController) {
                ((MainMenuController) controller).setDAOs(patientDAO, appointmentDAO);
            } else if (controller instanceof CalendarController) {
                ((CalendarController) controller).setDAO(appointmentDAO);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void onHomeClick() {
        loadContent("/com/example/joana/MainMenu.fxml");
    }

    @FXML public void onCalendarClick() {
        loadContent("/com/example/joana/CalendarView.fxml");
    }

    @FXML public void onPatientsClick() {
        loadContent("/com/example/joana/PatientsView.fxml");
    }

    @FXML public void onSettingsClick() {
        loadContent("/com/example/joana/SettingsView.fxml");
    }
}
