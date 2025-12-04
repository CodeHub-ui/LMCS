package com.library;

import com.library.controller.LoginController;
import com.library.dao.DatabaseUtil;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;  // Static reference to the Application instance

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;  // Set the instance when the app starts
        DatabaseUtil.initializeDatabase();

        // Revert to the original logic that creates the LoginController programmatically.
        // This controller builds its own scene.
        Scene scene = new LoginController(primaryStage).getScene();
        primaryStage.setTitle("Library Management Admin Portal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Static getter for HostServices
    public static HostServices getAppHostServices() {
        return instance.getHostServices();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
