package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.model.Admin;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import com.library.util.PasswordUtil;
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
 * Controller for handling password reset functionality with advanced UI.
 */
public class ResetPasswordController {
    private Stage stage;
    private LoginController loginController;
    private String email;

    public ResetPasswordController(Stage stage, LoginController loginController, String email) {
        this.stage = stage;
        this.loginController = loginController;
        this.email = email;
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
        contentBox.setMaxHeight(600);

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

        Label heading = new Label("RESET PASSWORD");
        heading.setFont(Font.font("System", FontWeight.BOLD, 24));
        heading.setTextFill(Color.WHITE);

        StackPane headingPane = new StackPane(headingBg, heading);

        Label subHeading = new Label("Enter the reset code and your new password");
        subHeading.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");

        // MaterialFX text field for reset code
        MFXTextField codeField = new MFXTextField();
        codeField.setPromptText("Reset Code");
        codeField.setPrefWidth(350);
        codeField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        codeField.setLeadingIcon(new javafx.scene.control.Label("🔑"));
        codeField.getLeadingIcon().setStyle("-fx-font-size: 16px;");

        // MaterialFX password fields
        MFXPasswordField newPasswordField = new MFXPasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setPrefWidth(350);
        newPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        newPasswordField.setLeadingIcon(new javafx.scene.control.Label("🔒"));
        newPasswordField.getLeadingIcon().setStyle("-fx-font-size: 16px;");

        MFXPasswordField confirmPasswordField = new MFXPasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        confirmPasswordField.setPrefWidth(350);
        confirmPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        confirmPasswordField.setLeadingIcon(new javafx.scene.control.Label("🔒"));
        confirmPasswordField.getLeadingIcon().setStyle("-fx-font-size: 16px;");

        // Buttons
        MFXButton resetBtn = new MFXButton("Reset Password");
        resetBtn.setPrefWidth(350);
        resetBtn.setPrefHeight(45);
        resetBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;");
        resetBtn.setOnMouseEntered(e -> resetBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;"));
        resetBtn.setOnMouseExited(e -> resetBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8;"));

        MFXButton backBtn = new MFXButton("Back to Login");
        backBtn.setPrefWidth(350);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8;"));
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, loginController.getScene()));

        resetBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                UIUtil.showAlert("Error", "Please fill in all fields.", javafx.scene.control.Alert.AlertType.ERROR);
                return;
            }

            if (!PasswordUtil.isValidPassword(newPassword)) {
                UIUtil.showAlert("Error", "Password must be at least 8 characters with upper, lower, and digit.", javafx.scene.control.Alert.AlertType.ERROR);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                UIUtil.showAlert("Error", "Passwords do not match.", javafx.scene.control.Alert.AlertType.ERROR);
                return;
            }

            AdminDAO dao = new AdminDAO();
            Admin admin = dao.getAdminByEmail(email);
            if (admin != null && dao.updatePassword(admin.getId(), newPassword)) {
                UIUtil.showAlert("Success", "Password reset successfully!", javafx.scene.control.Alert.AlertType.INFORMATION);
                UIUtil.switchScene(stage, loginController.getScene());
            } else {
                UIUtil.showAlert("Error", "Failed to reset password.", javafx.scene.control.Alert.AlertType.ERROR);
            }
        });

        contentBox.getChildren().addAll(headingPane, subHeading, codeField, newPasswordField, confirmPasswordField, resetBtn, backBtn);
        mainLayout.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        // Apply animations
        FadeIn fadeIn = new FadeIn(contentBox);
        fadeIn.setSpeed(0.5);
        fadeIn.play();

        return new Scene(mainLayout, 1050, 650);
    }
}
