package com.library.model;

/**
 * Manages the current user session for the user portal.
 * This class provides static methods to handle the logged-in user state per stage.
 * It uses a map to maintain session state for each JavaFX stage.
 */
import com.library.model.User;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private static Map<Stage, User> loggedInUsers = new HashMap<>(); // Holds the currently logged-in users per stage

    /**
     * Retrieves the currently logged-in user for the given stage.
     * @param stage the JavaFX stage
     * @return the logged-in User object, or null if no user is logged in for this stage
     */
    public static User getLoggedInUser(Stage stage) { return loggedInUsers.get(stage); }

    /**
     * Sets the logged-in user for the session of the given stage.
     * @param stage the JavaFX stage
     * @param user the User object to set as logged in
     */
    public static void setLoggedInUser(Stage stage, User user) { loggedInUsers.put(stage, user); }

    /**
     * Logs in the user by setting the logged-in user for the given stage.
     * @param stage the JavaFX stage
     * @param user the User object to log in
     */
    public static void login(Stage stage, User user) { loggedInUsers.put(stage, user); }

    /**
     * Logs out the current user by clearing the session for the given stage.
     * @param stage the JavaFX stage
     */
    public static void logout(Stage stage) { loggedInUsers.remove(stage); }
}
