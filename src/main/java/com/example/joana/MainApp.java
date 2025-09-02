package com.example.joana;

import com.example.joana.dao.AppointmentDAO;
import com.example.joana.dao.PatientDAO;
import com.example.joana.data.DatabaseManager;
import com.example.joana.model.Patient;
import com.example.joana.ui.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // SQLite connection
        Connection conn = DriverManager.getConnection("jdbc:sqlite:psychologist.db");
        PatientDAO patientDAO = new PatientDAO(conn);
        AppointmentDAO appDAO = new AppointmentDAO(conn);
        patientDAO.createTable();
        appDAO.createTable();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/joana/MainView.fxml"));
        Parent root = loader.load();
        MainController ctrl = loader.getController();
        ctrl.setDAOs(patientDAO, appDAO);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Joana's Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("=== DATABASE INITIALIZATION ===");
        DatabaseManager.initialize();
        launch(args);
    }
}

