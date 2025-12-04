package com.library.controller;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.model.UserSession;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Controller for handling user login functionality.
 * Provides a login scene with username and password fields, and authentication.
 */
public class UserLoginController {
    private Stage stage;

    /**
     * Constructor for UserLoginController.
     * @param stage the primary stage of the application
     */
    public UserLoginController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Creates and returns the login scene.
     * Includes username and password fields, login button, and navigation.
     *
     * @return the Scene object for the login view2G1K2Q
     */
    public Scene getScene() {
        StackPane mainLayout = new StackPane();
        mainLayout.setStyle("-fx-background-image: url('https://images.unsplash.com/photo-1504384308090-c894fdcc538d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80'); -fx-background-size: cover; -fx-background-position: center center;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(UILayoutConstants.PADDING);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 15;");
        contentBox.setMaxWidth(400);
        contentBox.setAlignment(Pos.CENTER);

        DropShadow shadow = new DropShadow();
        shadow.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));
        shadow.setRadius(16);
        contentBox.setEffect(shadow);

        Label heading = new Label("User Portal Login");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField rfidField = new TextField();
        rfidField.setPromptText("Scan RFID Card");
        rfidField.setPrefWidth(350);
        rfidField.setPadding(new javafx.geometry.Insets(12));
        rfidField.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14; -fx-text-fill: #1e293b;");
        rfidField.setFocusTraversable(true);

        Button loginBtn = UIUtil.createStyledButton("Login", "#1f7aec", "#0f62fe");
        loginBtn.setPrefWidth(350);
        loginBtn.setPrefHeight(50);
        loginBtn.setOnAction(e -> {
            String rfid = rfidField.getText();
            if (rfid.trim().isEmpty()) {
                UIUtil.showAlert("Error", "Please scan your RFID card.", Alert.AlertType.ERROR);
                return;
            }
            try {
                User user = new UserDAO().loginByRfid(rfid);
                if (user != null) {
                    UserSession.login(stage, user);
                    UIUtil.switchScene(stage, new UserDashboardController(stage).getScene());
                } else {
                    UIUtil.showAlert("Error", "Invalid RFID card.", Alert.AlertType.ERROR);
                }
            } catch (RuntimeException ex) {
                UIUtil.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                ex.printStackTrace();
            } catch (Exception ex) {
                UIUtil.showAlert("Error", "An error occurred during login.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        rfidField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginBtn.fire();
            }
        });

        contentBox.getChildren().addAll(heading, rfidField, loginBtn);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }
}
