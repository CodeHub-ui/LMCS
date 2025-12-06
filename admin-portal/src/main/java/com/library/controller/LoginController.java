package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.model.Admin;
import com.library.model.Session;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Controller for handling admin login functionality with advanced UI.
 */
public class LoginController {
    private Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Main container with background image
        StackPane mainLayout = new StackPane();
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);

        // Content card
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(40));
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 15;");
        contentBox.setMaxWidth(450);
        contentBox.setMaxHeight(550);
        contentBox.setAlignment(Pos.CENTER);

        DropShadow shadow = new DropShadow();
        shadow.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));
        shadow.setRadius(16);
        contentBox.setEffect(shadow);

        // Gradient heading
        Rectangle headingBg = new Rectangle(400, 60);
        headingBg.setArcWidth(15);
        headingBg.setArcHeight(15);
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true,
            javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        );
        headingBg.setFill(gradient);

        Label heading = new Label("ADMIN LOGIN");
        heading.setFont(Font.font("System", FontWeight.BOLD, 24));
        heading.setTextFill(Color.WHITE);

        StackPane headingPane = new StackPane(headingBg, heading);

        Label subHeading = new Label("Enter your credentials to access the admin portal");
        subHeading.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");

        // MaterialFX text fields with icons
        MFXTextField adminIdField = new MFXTextField();
        adminIdField.setPromptText("Admin ID");
        adminIdField.setPrefWidth(350);
        adminIdField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        adminIdField.setLeadingIcon(new javafx.scene.control.Label("👤"));
        adminIdField.getLeadingIcon().setStyle("-fx-font-size: 16px;");

        MFXPasswordField passwordField = new MFXPasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(350);
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        passwordField.setLeadingIcon(new javafx.scene.control.Label("🔒"));
        passwordField.getLeadingIcon().setStyle("-fx-font-size: 16px;");

        // Buttons
        MFXButton loginBtn = new MFXButton("Login");
        loginBtn.setPrefWidth(350);
        loginBtn.setPrefHeight(45);
        loginBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;"));

        MFXButton forgotPasswordBtn = new MFXButton("Forgot Password?");
        forgotPasswordBtn.setPrefWidth(350);
        forgotPasswordBtn.setPrefHeight(45);
        forgotPasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #3b82f6; -fx-border-radius: 8;");
        forgotPasswordBtn.setOnMouseEntered(e -> forgotPasswordBtn.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #3b82f6; -fx-border-radius: 8;"));
        forgotPasswordBtn.setOnMouseExited(e -> forgotPasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #3b82f6; -fx-border-radius: 8;"));

        MFXButton registerBtn = new MFXButton("Register New Admin");
        registerBtn.setPrefWidth(350);
        registerBtn.setPrefHeight(45);
        registerBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;");
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;"));

        // Login action
        Runnable performLogin = () -> {
            String adminId = adminIdField.getText().trim();
            String password = passwordField.getText();

            if (adminId.isEmpty() || password.isEmpty()) {
                UIUtil.showAlert("Error", "Please enter both Admin ID and Password.", javafx.scene.control.Alert.AlertType.ERROR);
                return;
            }

            AdminDAO dao = new AdminDAO();
            Admin admin = dao.login(adminId, password);
            if (admin != null) {
                Session.setLoggedInUser(admin);
                UIUtil.switchScene(stage, new DashboardController(stage).getScene());
            } else {
                UIUtil.showAlert("Error", "Invalid Admin ID or Password.", javafx.scene.control.Alert.AlertType.ERROR);
            }
        };

        loginBtn.setOnAction(e -> performLogin.run());

        // Enter key support
        adminIdField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performLogin.run();
            }
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performLogin.run();
            }
        });

        forgotPasswordBtn.setOnAction(e -> {
            ForgotPasswordController forgotController = new ForgotPasswordController(stage, this);
            UIUtil.switchScene(stage, forgotController.getScene());
        });

        registerBtn.setOnAction(e -> {
            RegistrationController registrationController = new RegistrationController(stage);
            UIUtil.switchScene(stage, registrationController.getScene());
        });

        contentBox.getChildren().addAll(headingPane, subHeading, adminIdField, passwordField, loginBtn, forgotPasswordBtn, registerBtn);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        // Apply animations
        FadeIn fadeIn = new FadeIn(contentBox);
        fadeIn.setSpeed(0.5);
        fadeIn.play();

        return new Scene(mainLayout, 1050, 650);
    }
}
