package com.example.joana.ui.list;

import com.example.joana.model.Appointment;

public class AppointmentItem implements CalendarItem {
    private final Appointment appointment;

    public AppointmentItem(Appointment appointment) {
        this.appointment = appointment;
    }
    public Appointment getAppointment() {
        return appointment;
    }
}
