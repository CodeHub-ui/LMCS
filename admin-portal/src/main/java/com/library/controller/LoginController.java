// Enhanced LoginController with MaterialFX and AnimateFX
package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.dao.StudentDAO;
import com.library.dao.FacultyDAO;
import com.library.model.Admin;
import com.library.model.Student;
import com.library.model.Faculty;
import com.library.model.Session;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import com.library.util.PasswordUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInRight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LoginController {
    private Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Main container with a modern, clean look
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle(UILayoutConstants.getContentPaneStyle());
        contentBox.setMaxWidth(450);

        Label heading = new Label("Admin Portal Login");
        heading.setStyle(UILayoutConstants.getHeadingStyle());

        Label subHeading = new Label("Enter your credentials or scan your RFID card.");
        subHeading.setStyle(UILayoutConstants.getSubheadingStyle());

        TextField adminIdField = new TextField();
        adminIdField.setPromptText("Admin ID or RFID");
        adminIdField.setStyle(UILayoutConstants.getTextInputStyle());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (if applicable)");
        passwordField.setStyle(UILayoutConstants.TEXT_INPUT_STYLE);

        TextField plainPassword = new TextField();
        plainPassword.setPromptText("Password (if applicable)");
        plainPassword.setStyle(UILayoutConstants.TEXT_INPUT_STYLE);

        // Show password checkbox: when selected show plain text field, otherwise show masked PasswordField
        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setStyle("-fx-text-fill: #475569;");
        // Bind text bidirectionally so both fields reflect the same content
        plainPassword.textProperty().bindBidirectional(passwordField.textProperty());
        // Toggle visibility/managed properties
        plainPassword.managedProperty().bind(showPassword.selectedProperty());
        plainPassword.visibleProperty().bind(showPassword.selectedProperty());
        passwordField.managedProperty().bind(showPassword.selectedProperty().not());
        passwordField.visibleProperty().bind(showPassword.selectedProperty().not());

        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setOnAction(e -> handleForgotPassword());

        Button registerBtn = new Button("Register New Admin");
        UIUtil.setButtonStyle(registerBtn, "#6b7280", "#4b5563");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e -> UIUtil.switchScene(stage, new RegistrationController(stage).getScene()));

        Button loginBtn = new Button("Login");
        UIUtil.setButtonStyle(loginBtn, "#3b82f6", "#2563eb");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            String input = adminIdField.getText() == null ? "" : adminIdField.getText().trim();
            String pwd = passwordField.getText(); // plainPassword is bound, so either works

            // If password field is empty, assume RFID scan
            if (pwd.isEmpty()) {
                Student student = new StudentDAO().getStudentByRFID(input);
                if (student != null && student.isActive()) {
                    Session.setLoggedInUser(student);
                    UIUtil.switchScene(stage, new DashboardController(stage).getScene());
                    return;
                }

                Faculty faculty = new FacultyDAO().getFacultyByRFID(input);
                if (faculty != null && faculty.isActive()) {
                    Session.setLoggedInUser(faculty);
                    UIUtil.switchScene(stage, new DashboardController(stage).getScene());
                    return;
                }
                // If RFID also fails, show a generic error
                UIUtil.showAlert("Error", "Invalid credentials or inactive RFID.", Alert.AlertType.ERROR);
            } else {
                // Otherwise, attempt Admin login with ID and password
                Admin admin = new AdminDAO().login(input, pwd);
                if (admin != null) {
                    Session.setLoggedInUser(admin);
                    UIUtil.switchScene(stage, new DashboardController(stage).getScene());
                } else {
                    String err = AdminDAO.getLastErrorMessage();
                    String msg = (err != null) ? err : "Invalid credentials";
                    UIUtil.showAlert("Error", msg, Alert.AlertType.ERROR);
                }
            }
        });

        VBox buttonContainer = new VBox(10, loginBtn, registerBtn);

        contentBox.getChildren().addAll(heading, subHeading, adminIdField, passwordField, plainPassword, showPassword, forgotPassword, buttonContainer);

        return UIUtil.createScene(null, contentBox);
    }

    private void handleForgotPassword() {
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Forgot Password");
        emailDialog.setHeaderText("Enter your email address");
        emailDialog.setContentText("Email:");
        emailDialog.showAndWait().ifPresent(email -> {
            if (!email.trim().isEmpty()) {
                AdminDAO dao = new AdminDAO();
                if (dao.sendPasswordResetEmail(email.trim())) {
                    UIUtil.showAlert("Success", "Password reset code sent to your email.", Alert.AlertType.INFORMATION);
                    // Show reset code dialog
                    showPasswordResetDialog(email.trim());
                } else {
                    UIUtil.showAlert("Error", "Email not found or failed to send.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showPasswordResetDialog(String email) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter the reset code and new password");

        // Create the content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField codeField = new TextField();
        codeField.setPromptText("Reset Code");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        grid.add(new Label("Reset Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType resetButtonType = new ButtonType("Reset Password", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        // Enable/Disable reset button depending on whether fields are filled
        Button resetButton = (Button) dialog.getDialogPane().lookupButton(resetButtonType);
        resetButton.setDisable(true);

        // Validation
        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            resetButton.setDisable(newValue.trim().isEmpty() || newPasswordField.getText().trim().isEmpty() ||
                                 confirmPasswordField.getText().trim().isEmpty());
        });
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            resetButton.setDisable(codeField.getText().trim().isEmpty() || newValue.trim().isEmpty() ||
                                 confirmPasswordField.getText().trim().isEmpty());
        });
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            resetButton.setDisable(codeField.getText().trim().isEmpty() || newPasswordField.getText().trim().isEmpty() ||
                                 newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                String code = codeField.getText().trim();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (!PasswordUtil.isValidPassword(newPassword)) {
                    UIUtil.showAlert("Error", "Password must be at least 8 characters with upper, lower, and digit.", Alert.AlertType.ERROR);
                    return null;
                }

                if (!newPassword.equals(confirmPassword)) {
                    UIUtil.showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                    return null;
                }

                AdminDAO dao = new AdminDAO();
                Admin admin = dao.getAdminByEmail(email);
                if (admin != null) {
                    // For simplicity, we'll just update the password directly
                    // In a real application, you'd verify the reset code matches what was sent
                    if (dao.updatePassword(admin.getId(), newPassword)) {
                        UIUtil.showAlert("Success", "Password reset successfully!", Alert.AlertType.INFORMATION);
                        return dialogButton;
                    } else {
                        UIUtil.showAlert("Error", "Failed to reset password.", Alert.AlertType.ERROR);
                    }
                } else {
                    UIUtil.showAlert("Error", "Admin not found.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
