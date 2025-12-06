package com.library;

import com.library.controller.UserLoginController;
import com.library.dao.DatabaseUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the User Portal JavaFX application.
 * This class initializes the database, performs hardware checks, and starts the login scene.
 */
public class UserPortalMain extends Application {

    /**
     * Starts the JavaFX application.
     * Initializes the database, checks hardware connectivity, and displays the login scene.
     *
     * @param primaryStage the primary stage for the application
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the database with tables and sample data
        DatabaseUtil.initializeDatabase();

        // Proceed to the login scene
        Scene scene = new UserLoginController(primaryStage).getScene();
        primaryStage.setTitle("User Portal");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(850);
        primaryStage.setResizable(true);
        primaryStage.show();
    }



    /**
     * Main method to launch the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
