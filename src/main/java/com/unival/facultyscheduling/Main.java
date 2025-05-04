package com.unival.facultyscheduling;

import com.unival.facultyscheduling.view.LoginView;
import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize window state management
        WindowStateManager.initializeWindowState(primaryStage);
        
        // Initialize the login view
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
