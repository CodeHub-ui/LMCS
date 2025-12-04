// Placeholder for RegistrationController.java
package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.model.Admin;
import com.library.service.RegistrationService;
import com.library.util.PasswordUtil;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import com.library.util.EmailService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistrationController {
    private Stage stage;

    public RegistrationController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
    VBox contentBox = new VBox(10);
    contentBox.setPadding(UILayoutConstants.PADDING);
    contentBox.setAlignment(Pos.CENTER);

    TextField adminIdField = new TextField();
    adminIdField.setPromptText("Admin ID");
    adminIdField.setStyle(UILayoutConstants.getTextInputStyle());

    TextField nameField = new TextField();
    nameField.setPromptText("Full Name");
    nameField.setStyle(UILayoutConstants.getTextInputStyle());

    TextField emailField = new TextField();
    emailField.setPromptText("Email");
    emailField.setStyle(UILayoutConstants.getTextInputStyle());

    TextField mobileField = new TextField();
    mobileField.setPromptText("Mobile Number");
    mobileField.setStyle(UILayoutConstants.getTextInputStyle());

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Password");
    passwordField.setStyle(UILayoutConstants.getTextInputStyle());

    TextField plainPassword = new TextField();
    plainPassword.setPromptText("Password");
    plainPassword.setStyle(UILayoutConstants.getTextInputStyle());

    PasswordField confirmPasswordField = new PasswordField();
    confirmPasswordField.setPromptText("Confirm Password");
    confirmPasswordField.setStyle(UILayoutConstants.getTextInputStyle());

    TextField plainConfirm = new TextField();
    plainConfirm.setPromptText("Confirm Password");
    plainConfirm.setStyle(UILayoutConstants.getTextInputStyle());

    CheckBox showPassword = new CheckBox("Show Password");
    // Bind plain text fields to masked fields so they stay in sync
    plainPassword.textProperty().bindBidirectional(passwordField.textProperty());
    plainPassword.managedProperty().bind(showPassword.selectedProperty());
    plainPassword.visibleProperty().bind(showPassword.selectedProperty());
    passwordField.managedProperty().bind(showPassword.selectedProperty().not());
    passwordField.visibleProperty().bind(showPassword.selectedProperty().not());

    plainConfirm.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    plainConfirm.managedProperty().bind(showPassword.selectedProperty());
    plainConfirm.visibleProperty().bind(showPassword.selectedProperty());
    confirmPasswordField.managedProperty().bind(showPassword.selectedProperty().not());
    confirmPasswordField.visibleProperty().bind(showPassword.selectedProperty().not());

    Button registerBtn = new Button("Register");
    registerBtn.setStyle("-fx-background-color: linear-gradient(#1f7aec,#0f62fe); -fx-text-fill:white; -fx-padding:8 12 8 12; -fx-background-radius:8;");
        registerBtn.setOnAction(e -> {
            String adminId = adminIdField.getText() == null ? "" : adminIdField.getText().trim();
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String email = emailField.getText() == null ? "" : emailField.getText().trim();
            String mobile = mobileField.getText() == null ? "" : mobileField.getText().trim();
            String password = passwordField.getText();
            String confirm = confirmPasswordField.getText();
            if (adminId.isEmpty() || name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
                UIUtil.showAlert("Error", "Please fill all fields.", Alert.AlertType.ERROR);
            } else if (!PasswordUtil.isValidPassword(password)) {
                UIUtil.showAlert("Error", "Password must be at least 8 characters with upper, lower, and digit.", Alert.AlertType.ERROR);
            } else if (!password.equals(confirm)) {
                UIUtil.showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
            } else {
                // Check for duplicate registration
                RegistrationService.RegistrationResult validationResult = RegistrationService.validateRegistration(email, mobile);
                if (!validationResult.isSuccess()) {
                    // Show popup with existing account details
                    String message = "Registration failed: " + validationResult.getErrorMessage() + "\n\n" +
                                   "Existing Account Details:\n" +
                                   validationResult.getExistingAccount().toString();
                    UIUtil.showAlert("Duplicate Account Found", message, Alert.AlertType.WARNING);
                    return;
                }

                Admin admin = new Admin();
                admin.setAdminId(adminId);
                admin.setName(name);
                admin.setEmail(email);
                admin.setMobile(mobile);
                admin.setPasswordHash(password);
                AdminDAO dao = new AdminDAO();
                if (dao.register(admin)) {
                    // Send confirmation email
                    EmailService emailService = new EmailService();
                    emailService.sendAdminRegistrationNotification(email, adminId);
                    UIUtil.showAlert("Success", "Registration successful! A confirmation email has been sent.", Alert.AlertType.INFORMATION);
                    UIUtil.switchScene(stage, new LoginController(stage).getScene());
                } else {
                    String err = AdminDAO.getLastErrorMessage();
                    String msg = "Registration failed." + (err != null ? "\nReason: " + err : "");
                    UIUtil.showAlert("Error", msg, Alert.AlertType.ERROR);
                }
            }
        });

    Button cancelBtn = new Button("Cancel");
    cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color:#d1d5db; -fx-padding:8 12 8 12;");
    cancelBtn.setOnAction(e -> UIUtil.switchScene(stage, new LoginController(stage).getScene()));

        contentBox.getChildren().addAll(adminIdField, nameField, emailField, mobileField, passwordField, plainPassword, confirmPasswordField, plainConfirm, showPassword, registerBtn, cancelBtn);

        return UIUtil.createScene(null, contentBox);
    }
}
