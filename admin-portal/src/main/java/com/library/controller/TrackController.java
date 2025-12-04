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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
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
        for (Log log : new LogDAO().getAllLogs()) {
            logView.getItems().add(log.getTimestamp() + ": " + log.getAction());
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));

        layout.getChildren().addAll(logView, backBtn);
        return UIUtil.createScene(null, layout);
    }
}