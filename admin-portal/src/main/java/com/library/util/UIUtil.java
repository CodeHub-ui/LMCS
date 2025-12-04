// Placeholder for UIUtil.java
package com.library.util;

import com.library.controller.DashboardController;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.NotificationPos;
// import io.github.palexdev.materialfx.notifications.MFXNotificationCenter;
// import io.github.palexdev.materialfx.notifications.MFXSimpleNotification;
// import io.github.palexdev.materialfx.notifications.base.INotification;
// import io.github.palexdev.materialfx.controls.MFXStageDialog;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Map;

public class UIUtil {
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static MFXGenericDialog createDialog(String title, String content) {
        MFXGenericDialog dialog = new MFXGenericDialog();
        dialog.setHeaderText(title);
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        dialog.setContent(contentLabel);
        return dialog;
    }

    public static void showConfirmation(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                onConfirm.run();
            }
        });
    }

    public static void showSuccess(String title, String message) {
        showNotification(title, message, "mfx-success");
    }

    public static void showError(String title, String message) {
        showNotification(title, message, "mfx-error");
    }

    public static void showWarning(String title, String message) {
        showNotification(title, message, "mfx-warning");
    }

    private static void showNotification(String title, String message, String styleClass) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void switchScene(Stage stage, Scene scene) {
        // Do not apply external CSS: use programmatic Java styling only
        if (scene == null) return;
        stage.setScene(scene);
    }

    public static Button createStyledButton(String text, String startColor, String endColor) {
        Button btn = new Button(text);
        String baseStyle = String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;", startColor, endColor);
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;", endColor, startColor)));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    public static void setButtonStyle(Button button, String startColor, String endColor) {
        String baseStyle = String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;", startColor, endColor);
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: linear-gradient(%s, %s); -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;", endColor, startColor)));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }

    public static Scene createScene(Parent topBar, Parent content) {
        BorderPane root = new BorderPane();
        root.setStyle(UILayoutConstants.FULL_BACKGROUND_STYLE);

        BorderPane contentPane = new BorderPane();
        contentPane.setStyle(UILayoutConstants.CONTENT_PANE_STYLE);

        if (topBar != null) {
            contentPane.setTop(topBar);
        }
        contentPane.setCenter(content);
        BorderPane.setMargin(content, UILayoutConstants.PADDING);

        root.setCenter(contentPane);
        return new Scene(root, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }

    public static Scene createSceneWithSidebar(Stage stage, String currentView, Parent content) {
        BorderPane root = new BorderPane();
        root.setStyle(UILayoutConstants.getBackgroundStyle());

        // Re-create sidebar from DashboardController logic
        DashboardController tempDashboard = new DashboardController(stage);
        VBox sidebar = tempDashboard.createSidebar(currentView);

        root.setLeft(sidebar);
        root.setCenter(content);
        return new Scene(root, UILayoutConstants.SCENE_WIDTH, UILayoutConstants.SCENE_HEIGHT);
    }
}
