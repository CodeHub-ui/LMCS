package com.library.controller;

import com.library.dao.LogDAO;
import com.library.model.Log;
import com.library.util.UILayoutConstants;
import com.library.util.UIUtil;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrackController {
    private Stage stage;

    public TrackController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Content container with semi-transparent background
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        ListView<String> logView = new ListView<>();
        logView.setStyle("-fx-background-color: #fafafa; -fx-border-color:#e5e7eb; -fx-padding:8; -fx-background-radius:6;");
        logView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Method to refresh logs
        Runnable refreshLogs = () -> {
            List<Log> allLogs = new LogDAO().getAllLogs();
            logView.getItems().clear();
            for (Log log : allLogs) {
                logView.getItems().add(log.getTimestamp() + ": " + log.getAction());
            }
        };

        // Initial load
        refreshLogs.run();

        TextField searchField = new TextField();
        searchField.setPromptText("Search by action or timestamp");

        Button searchBtn = UIUtil.createStyledButton("Search", "#3b82f6", "#1d4ed8");
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            List<Log> allLogs = new LogDAO().getAllLogs();
            logView.getItems().clear();
            for (Log log : allLogs) {
                if (log.getAction().toLowerCase().contains(query) || log.getTimestamp().toLowerCase().contains(query)) {
                    logView.getItems().add(log.getTimestamp() + ": " + log.getAction());
                }
            }
        });

        Button deleteBtn = UIUtil.createStyledButton("Delete Selected", "#ef4444", "#dc2626");
        deleteBtn.setOnAction(e -> {
            List<Integer> selectedIndices = logView.getSelectionModel().getSelectedIndices();
            List<Log> allLogs = new LogDAO().getAllLogs();
            List<Integer> idsToDelete = new ArrayList<>();
            for (int index : selectedIndices) {
                idsToDelete.add(allLogs.get(index).getId());
            }
            new LogDAO().deleteLogsByIds(idsToDelete);
            // Refresh
            refreshLogs.run();
        });

        Button advancedBtn = UIUtil.createStyledButton("Advanced Feature", "#10b981", "#059669");
        advancedBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Advanced Feature");
            alert.setHeaderText(null);
            alert.setContentText("Advanced analysis coming soon!");
            alert.showAndWait();
        });

        Button backBtn = UIUtil.createStyledButton("Back", "#6b7280", "#4b5563");
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));

        layout.getChildren().addAll(searchField, searchBtn, logView, deleteBtn, advancedBtn, backBtn);
        return UIUtil.createScene(null, layout);
    }
}