module com.example.joana {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires javafx.graphics;
    //requires com.example.joana;

    opens com.example.joana.ui.controllers   to javafx.fxml;
    opens com.example.joana.model to javafx.base;
    exports com.example.joana.ui.controllers;
    exports com.example.joana.model;

    exports com.example.joana;
}