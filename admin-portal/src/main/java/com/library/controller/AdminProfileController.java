package com.library.controller;

import com.library.dao.AdminDAO;
import com.library.model.Admin;
import com.library.model.Session;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import com.library.util.EmailService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * AdminProfileController handles admin profile viewing and editing.
 * Allows admins to view and update their profile information.
 */
public class AdminProfileController {
    private Stage stage;

    public AdminProfileController(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        if (Session.getLoggedInAdmin() == null) {
            UIUtil.switchScene(stage, new LoginController(stage).getScene());
            return null;
        }

        VBox contentBox = new VBox(20);
        contentBox.setPadding(UILayoutConstants.PADDING);
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label("👤 Admin Profile");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(Session.getLoggedInAdmin().getName());
        nameField.setPrefWidth(300);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(Session.getLoggedInAdmin().getEmail());
        emailField.setPrefWidth(300);

        Label mobileLabel = new Label("Mobile:");
        TextField mobileField = new TextField(Session.getLoggedInAdmin().getMobile());
        mobileField.setPrefWidth(300);

        Label adminIdLabel = new Label("Admin ID:");
        TextField adminIdField = new TextField(Session.getLoggedInAdmin().getAdminId());
        adminIdField.setEditable(false);
        adminIdField.setPrefWidth(300);

        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(mobileLabel, 0, 2);
        formGrid.add(mobileField, 1, 2);
        formGrid.add(adminIdLabel, 0, 3);
        formGrid.add(adminIdField, 1, 3);

        Button saveBtn = UIUtil.createStyledButton("Save Changes", "#10b981", "#059669");
        saveBtn.setPrefWidth(150);
        saveBtn.setOnAction(e -> {
            // Implement save functionality
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newMobile = mobileField.getText().trim();

            // Basic validation
            if (newName.isEmpty() || newEmail.isEmpty() || newMobile.isEmpty()) {
                UIUtil.showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
                return;
            }

            if (!newEmail.contains("@") || !newEmail.contains(".")) {
                UIUtil.showAlert("Error", "Please enter a valid email address!", Alert.AlertType.ERROR);
                return;
            }

            if (newMobile.length() != 10 || !newMobile.matches("\\d+")) {
                UIUtil.showAlert("Error", "Please enter a valid 10-digit mobile number!", Alert.AlertType.ERROR);
                return;
            }

            // Check if email is already in use by another admin
            AdminDAO dao = new AdminDAO();
            if (dao.isEmailTakenByAnotherAdmin(newEmail, Session.getLoggedInAdmin().getId())) {
                UIUtil.showAlert("Error", "This email is already in use by another admin!", Alert.AlertType.ERROR);
                return;
            }

            // Update profile
            boolean success = dao.updateProfile(Session.getLoggedInAdmin().getId(), newName, newEmail, newMobile);
            if (success) {
                // Update the session admin object
                Session.getLoggedInAdmin().setName(newName);
                Session.getLoggedInAdmin().setEmail(newEmail);
                Session.getLoggedInAdmin().setMobile(newMobile);
                UIUtil.showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                UIUtil.showAlert("Error", "Failed to update profile. Please try again.", Alert.AlertType.ERROR);
            }
        });

        Button changePasswordBtn = UIUtil.createStyledButton("Change Password", "#f59e0b", "#f97316");
        changePasswordBtn.setPrefWidth(180);
        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());

        Button backBtn = UIUtil.createStyledButton("Back to Dashboard", "#6b7280", "#374151");
        backBtn.setPrefWidth(200);
        backBtn.setOnAction(e -> UIUtil.switchScene(stage, new DashboardController(stage).getScene()));

        HBox buttonBox = new HBox(20, saveBtn, changePasswordBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(heading, formGrid, buttonBox);

        return UIUtil.createScene(null, contentBox);
    }

    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current password and new password");

        // Create the password fields
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType changeButtonType = new ButtonType("Change Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        // Enable/Disable change button depending on whether fields are filled
        Button changeButton = (Button) dialog.getDialogPane().lookupButton(changeButtonType);
        changeButton.setDisable(true);

        // Validation logic
        currentPasswordField.textProperty().addListener((observable, oldValue, newValue) ->
            validatePasswordFields(currentPasswordField, newPasswordField, confirmPasswordField, changeButton));
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) ->
            validatePasswordFields(currentPasswordField, newPasswordField, confirmPasswordField, changeButton));
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) ->
            validatePasswordFields(currentPasswordField, newPasswordField, confirmPasswordField, changeButton));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return dialogButton;
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == changeButtonType) {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validate passwords
            if (!newPassword.equals(confirmPassword)) {
                UIUtil.showAlert("Error", "New passwords do not match!", Alert.AlertType.ERROR);
                return;
            }

            if (newPassword.length() < 6) {
                UIUtil.showAlert("Error", "New password must be at least 6 characters long!", Alert.AlertType.ERROR);
                return;
            }

            // Verify current password
            AdminDAO dao = new AdminDAO();
            Admin admin = dao.login(Session.getLoggedInAdmin().getAdminId(), currentPassword);
            if (admin == null) {
                UIUtil.showAlert("Error", "Current password is incorrect!", Alert.AlertType.ERROR);
                return;
            }

            // Change password
            boolean success = dao.updatePassword(Session.getLoggedInAdmin().getId(), newPassword);
            if (success) {
                // Send email confirmation
                EmailService emailService = new EmailService();
                boolean emailSent = emailService.sendPasswordChangeNotification(
                    Session.getLoggedInAdmin().getEmail(),
                    Session.getLoggedInAdmin().getName()
                );
                if (emailSent) {
                    UIUtil.showAlert("Success", "Password changed successfully! A confirmation email has been sent.", Alert.AlertType.INFORMATION);
                } else {
                    UIUtil.showAlert("Success", "Password changed successfully! (Email notification failed to send)", Alert.AlertType.INFORMATION);
                }
            } else {
                UIUtil.showAlert("Error", "Failed to change password. Please try again.", Alert.AlertType.ERROR);
            }
        }
    }

    private void validatePasswordFields(PasswordField current, PasswordField newPass, PasswordField confirm, Button changeBtn) {
        boolean allFilled = !current.getText().trim().isEmpty() &&
                           !newPass.getText().trim().isEmpty() &&
                           !confirm.getText().trim().isEmpty();
        boolean passwordsMatch = newPass.getText().equals(confirm.getText());
        changeBtn.setDisable(!(allFilled && passwordsMatch));
    }
}
