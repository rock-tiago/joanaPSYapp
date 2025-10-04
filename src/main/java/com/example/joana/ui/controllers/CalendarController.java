package com.example.joana.ui.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DetailedWeekView;
import com.calendarfx.view.DayView;
import com.example.joana.dao.AppointmentDAO;
import com.example.joana.model.Appointment;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarController {

    @FXML private StackPane calendarContent;
    private CalendarView calendarView;
    private Calendar appointmentsCalendar;
    private AppointmentDAO appointmentDAO;
    @FXML private ToggleGroup viewToggleGroup;

    @FXML
    public void initialize() {
        calendarView = new CalendarView();
        appointmentsCalendar = new Calendar("Appointments");
        appointmentsCalendar.setStyle(Calendar.Style.STYLE1);

        CalendarSource calendarSource = new CalendarSource("My Calendars");
        calendarSource.getCalendars().add(appointmentsCalendar);
        calendarView.getCalendarSources().add(calendarSource);

        // Add to UI
        calendarContent.getChildren().setAll(calendarView);

        // Show week page by default
        calendarView.showWeekPage();

//        // Listen to date changes
//        calendarView.dateProperty().addListener((obs, oldDate, newDate) -> {
//            System.out.println("Date changed to: " + newDate);
//        });
//
//        // Listen to page changes (Day/Week/Month)
//        calendarView.selectedPageProperty().addListener((obs, oldPage, newPage) -> {
//            System.out.println("Now showing: " + newPage.getClass().getSimpleName());
//        });

    }

    private void loadAppointmentsIntoCalendar() {
        if (appointmentDAO == null) {
            System.out.println("appointmentDAO not set yet");
            return;
        }

        List<Appointment> all;

        try {
            all = appointmentDAO.getAllAppointments();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (all == null) return;

        for (Appointment a : all) {
            String title = (a.getPatient() != null) ? a.getPatient().getName() : "Appointment";
            Entry<Appointment> entry = new Entry<>(title);
            entry.setUserObject(a); // store the appointment object inside the Entry

            // set interval (there are convenience methods)
            // the API provides setInterval(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime)
            LocalDate date = a.getDate(); // adjust to your model types
            LocalTime time = a.getTime();
            if (date == null || time == null) continue;

            // default duration: 1 hour (or use a.getDuration() if you have it)
            entry.setInterval(date, time, date, time.plusHours(1));

            // add to calendar
            appointmentsCalendar.addEntry(entry);

            // optionally: listen for interval changes so you can persist DB updates
            entry.intervalProperty().addListener((obs, oldInterval, newInterval) -> {
                // convert newInterval to your Appointment model and persist
                // e.g., a.setDate(newInterval.getStartDate()); a.setTime(newInterval.getStartTime());
                // appointmentDAO.update(a);
            });
        }
    }

    private void openAppointmentEditor(Appointment appointment, Entry<?> entry) {
        // open your appointment form/dialog passing the appointment.
        // When the form saves, update the entry title / userObject / interval and persist to DB.
    }

    public void setDAO(AppointmentDAO dao) throws SQLException {
        this.appointmentDAO = dao;
        loadAppointmentsIntoCalendar();
        //loadDayView(); // default
    }

    @FXML
    private void switchToDay() { calendarView.showDayPage(); }

    @FXML
    private void switchToWeek() {
        calendarView.showWeekPage();
    }

    @FXML
    private void switchToMonth() {
        calendarView.showMonthPage();
    }

    @FXML
    private void goToday() {
        calendarView.setDate(LocalDate.now());
    }

    @FXML
    private void onNewAppointment() {
        // TODO: Open the "New Appointment" modal
    }

    private void loadDayView() throws SQLException {
        VBox dayView = new VBox(5);
        dayView.setPadding(new Insets(10));

        // Fetch appointments for today
        List<Appointment> appointments = appointmentDAO.getAppointmentsForDate(LocalDate.now());

        for (Appointment app : appointments) {
            Label label = new Label(app.getTime() + " - " + app.getPatient().getName());
            label.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-background-radius: 5;");
            label.setOnMouseClicked(e -> openAppointmentDetails(app));
            dayView.getChildren().add(label);
        }

        calendarContent.getChildren().setAll(dayView);
    }

    private void loadWeekView() {
        // TODO: display week grid
    }

    private void loadMonthView() {
        // TODO: display month grid
    }

    private void openAppointmentDetails(Appointment app) {
        // TODO: Open a detail popup (dialog)
    }
}

