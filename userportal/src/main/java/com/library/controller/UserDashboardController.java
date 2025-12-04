
package com.library.controller;

import com.library.model.UserSession;
import com.library.util.UIUtil;
import com.library.util.UILayoutConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.stage.Stage;

/**
 * Controller for the user dashboard.
 * Displays user information and provides navigation to issue, return, and logout functionalities.
 */
public class UserDashboardController {
    private Stage stage;

    /**
     * Constructor for UserDashboardController.
     * @param stage the primary stage of the application
     */
    public UserDashboardController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Creates and returns the dashboard scene.
     * Shows user details in read-only fields and buttons for navigation.
     * Redirects to login if no user is logged in.
     *
     * @return the Scene object for the dashboard view, or null if redirected
     */
    public Scene getScene() {
        if (UserSession.getLoggedInUser(stage) == null) {
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
            return null;
        }

        StackPane mainLayout = new StackPane();
        mainLayout.setStyle("-fx-background-image: url('https://images.unsplash.com/photo-1504384308090-c894fdcc538d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80'); -fx-background-size: cover; -fx-background-position: center center;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(25));
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(380);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15;");

        DropShadow shadow = new DropShadow();
        shadow.setBlurType(BlurType.GAUSSIAN);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));
        shadow.setRadius(12);
        contentBox.setEffect(shadow);

        // Heading label with gradient background and padding
        Label heading = new Label("User Portal");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-background-radius: 10; -fx-text-fill: white; -fx-padding: 12 20 12 20;");
        heading.setBackground(new Background(new BackgroundFill(
                javafx.scene.paint.LinearGradient.valueOf("linear-gradient(to right, #4caf50, #2196f3)"),
                new CornerRadii(10), Insets.EMPTY)));

        // User info fields styled with padding, font and drop shadow
        TextField nameField = createStyledTextField("Name", UserSession.getLoggedInUser(stage).getName());
        TextField emailField = createStyledTextField("Email", UserSession.getLoggedInUser(stage).getEmail());
        TextField mobileField = createStyledTextField("Mobile", UserSession.getLoggedInUser(stage).getMobile());
        TextField idField = createStyledTextField("User ID", UserSession.getLoggedInUser(stage).getStudentId());
        TextField courseField = null;
        if (UserSession.getLoggedInUser(stage).getCourse() != null && !UserSession.getLoggedInUser(stage).getCourse().trim().isEmpty()) {
            courseField = createStyledTextField("Course", UserSession.getLoggedInUser(stage).getCourse());
        }

        // Buttons styled with icons, gradients, fixed widths, and hover effects
        VBox buttonsBox = new VBox(15);
        buttonsBox.setAlignment(Pos.CENTER); // This was correct, but let's ensure it stays.

        Button issueBtn = createStyledButton("📗 Issue", "#1f7aec", "#0f62fe");
        issueBtn.setPrefWidth(200);
        issueBtn.setPrefHeight(50);
        issueBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserIssueController(stage).getScene()));

        Button returnBtn = createStyledButton("📘 Return", "#f59e0b", "#f97316");
        returnBtn.setPrefWidth(200);
        returnBtn.setPrefHeight(50);
        returnBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserReturnController(stage).getScene()));

        Button logoutBtn = createStyledButton("🚪 Logout", "#ef4444", "#dc2626");
        logoutBtn.setPrefWidth(200);
        logoutBtn.setPrefHeight(50);
        logoutBtn.setOnAction(e -> {
            UserSession.logout(stage);
            UIUtil.switchScene(stage, new UserLoginController(stage).getScene());
        });

        Button goBackBtn = createStyledButton("⏪ Go Back", "#6b7280", "#374151");
        goBackBtn.setPrefWidth(200);
        goBackBtn.setPrefHeight(50);
        goBackBtn.setOnAction(e -> UIUtil.switchScene(stage, new UserLoginController(stage).getScene()));

        buttonsBox.getChildren().addAll(issueBtn, returnBtn, goBackBtn, logoutBtn);

        if (courseField != null) {
            contentBox.getChildren().addAll(heading, nameField, emailField, mobileField, idField, courseField, buttonsBox);
        } else {
            contentBox.getChildren().addAll(heading, nameField, emailField, mobileField, idField, buttonsBox);
        }
        mainLayout.getChildren().add(contentBox);

        return new Scene(mainLayout, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }

    private TextField createStyledTextField(String label, String value) {
        TextField tf = new TextField(value);
        tf.setEditable(false);
        tf.setPromptText(label);
        tf.setPrefWidth(350);
        tf.setPadding(new Insets(8, 12, 8, 12));
        tf.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-font-size: 14; -fx-text-fill: #1e293b;");
        tf.setFocusTraversable(false);
        tf.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.04), 4, 0, 0, 1));
        return tf;
    }

    private Button createStyledButton(String text, String startColor, String endColor) {
        Button btn = new Button(text);
        String baseStyle = String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 16 10 16; -fx-cursor: hand; -fx-pref-width: 100;", startColor, endColor);
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 16 10 16; -fx-cursor: hand; -fx-pref-width: 100;", endColor, startColor)));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }
}
