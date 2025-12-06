package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
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
 * Controller for handling forgot password functionality with advanced UI.
 */
public class ForgotPasswordController {
    private Stage stage;
    private LoginController loginController;

    public ForgotPasswordController(Stage stage, LoginController loginController) {
        this.stage = stage;
        this.loginController = loginController;
    }

    public Scene getScene() {
        // Main container with background image
        StackPane mainLayout = new StackPane();
        mainLayout.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);

        // Content card
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.92); -fx-background-radius: 15;");
        contentBox.setMaxWidth(450);
        contentBox.setMaxHeight(500);

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

        Label heading = new Label("FORGOT PASSWORD");
        heading.setFont(Font.font("System", FontWeight.BOLD, 24));
        heading.setTextFill(Color.WHITE);

        StackPane headingPane = new StackPane(headingBg, heading);

        Label subHeading = new Label("Enter your email address to receive a reset code");
        subHeading.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");

        // MaterialFX text field with icon
        MFXTextField emailField = new MFXTextField();
        emailField.setPromptText("Email Address");
        emailField.setPrefWidth(350);
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        emailField.setLeadingIcon(new javafx.scene.control.Label("📧"));
        emailField.getLeadingIcon().setStyle("-fx-font-size: 16px;");

        // Buttons
        MFXButton sendCodeBtn = new MFXButton("Send Reset Code");
        sendCodeBtn.setPrefWidth(350);
        sendCodeBtn.setPrefHeight(45);
        sendCodeBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        sendCodeBtn.setOnMouseEntered(e -> sendCodeBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;"));
        sendCodeBtn.setOnMouseExited(e -> sendCodeBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;"));

        MFXButton backBtn = new MFXButton("Back to Login");
        backBtn.setPrefWidth(350);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;"));
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, loginController.getScene()));

        sendCodeBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                UIUtil.showAlert("Error", "Please enter your email address.", javafx.scene.control.Alert.AlertType.ERROR);
                return;
            }

            AdminDAO dao = new AdminDAO();
            if (dao.sendPasswordResetEmail(email)) {
                UIUtil.showAlert("Success", "Password reset code sent to your email.", javafx.scene.control.Alert.AlertType.INFORMATION);
                // Navigate to reset password scene
                ResetPasswordController resetController = new ResetPasswordController(stage, loginController, email);
                UIUtil.switchScene(stage, resetController.getScene());
            } else {
                UIUtil.showAlert("Error", "Email not found or failed to send.", javafx.scene.control.Alert.AlertType.ERROR);
            }
        });

        contentBox.getChildren().addAll(headingPane, subHeading, emailField, sendCodeBtn, backBtn);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        // Apply animations
        FadeIn fadeIn = new FadeIn(contentBox);
        fadeIn.setSpeed(0.5);
        fadeIn.play();

        return new Scene(mainLayout, 1050, 650);
    }
}
