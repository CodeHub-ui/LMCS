package com.library.util;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Utility class for UI-related operations in the library management system.
 * Provides methods for displaying alerts, confirmations, and switching scenes.
 */
public class UIUtil {

    /**
     * Displays an alert dialog with the specified title, message, and type.
     *
     * @param title the title of the alert dialog
     * @param message the message to display in the alert
     * @param type the type of alert (e.g., INFORMATION, ERROR, WARNING)
     */
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with the specified message.
     *
     * @param message the message to display in the confirmation dialog
     * @return true if the user confirms (OK), false otherwise
     */
    public static boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Switches the scene on the given stage to the specified scene.
     *
     * @param stage the stage on which to set the new scene
     * @param scene the scene to set on the stage
     */
    public static void switchScene(Stage stage, Scene scene) {
        stage.setScene(scene);
    }

    /**
     * Creates a styled button with given text and gradient background colors.
     *
     * @param text the button text
     * @param color1 the first color in the gradient
     * @param color2 the second color in the gradient
     * @return a styled Button object
     */
    public static Button createStyledButton(String text, String color1, String color2) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: linear-gradient(" + color1 + ", " + color2 + "); -fx-text-fill: white;");
        return button;
    }
}
