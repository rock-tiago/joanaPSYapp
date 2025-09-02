package com.example.joana.ui.list;

import java.time.LocalDate;

public class DayHeader implements CalendarItem {
    private final LocalDate date;

    public DayHeader(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
