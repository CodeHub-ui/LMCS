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
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(UILayoutConstants.PADDING);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 15;");
        contentBox.setMaxWidth(400);
        contentBox.setMaxHeight(600);
        contentBox.setAlignment(Pos.CENTER);

        DropShadow shadow = new DropShadow();
        shadow.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));
        shadow.setRadius(16);
        contentBox.setEffect(shadow);


        Label heading = new Label("USER LOGIN");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField rfidField = new TextField();
        rfidField.setPromptText("Tap your card");
        rfidField.setPrefWidth(350);
        rfidField.setPadding(new javafx.geometry.Insets(12));
        rfidField.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14; -fx-text-fill: #1e293b;");
        rfidField.setFocusTraversable(true);

        // Method to perform login
        Runnable performLogin = () -> {
            String rfid = rfidField.getText().trim();
            if (rfid.isEmpty()) {
                UIUtil.showAlert("Error", "Please Tap your RFID card.", Alert.AlertType.ERROR);
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
        };

        // Automatic login when RFID reaches expected length (e.g., 10 characters for sample data)
        rfidField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 10) { // Assuming RFID length is 10 characters like RFID001001N
                // Use Platform.runLater to avoid triggering another change event during current event processing
                javafx.application.Platform.runLater(() -> performLogin.run());
            }
        });

        Button loginBtn = UIUtil.createStyledButton("Login", "#1f7aec", "#0f62fe");
        loginBtn.setPrefWidth(350);
        loginBtn.setPrefHeight(50);
        loginBtn.setOnAction(e -> performLogin.run());

        rfidField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performLogin.run();
            }
        });

        contentBox.getChildren().addAll(heading, rfidField, loginBtn);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }
}
