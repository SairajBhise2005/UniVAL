package com.unival.facultyscheduling;

/**
 * Main serves as the entry point for the UniVAL Faculty Scheduling application.
 * <p>
 * This class extends {@link javafx.application.Application} and is responsible for launching the JavaFX application lifecycle.
 * It initializes window state management and displays the login view to the user upon startup. The application is designed to
 * provide a modern, user-friendly interface for faculty, students, and administrators to manage schedules and access relevant features.
 * <p>
 * Core Features:
 * <ul>
 *     <li>Initializes the JavaFX runtime and primary application stage.</li>
 *     <li>Sets up persistent window state management for user convenience.</li>
 *     <li>Displays the login screen as the first interaction point.</li>
 * </ul>
 * <p>
 * Methods:
 * <ul>
 *     <li>start: Sets up the main window and displays the login view.</li>
 *     <li>main: Launches the JavaFX application.</li>
 * </ul>
 * <p>
 * Typical Usage:
 * <pre>
 *     java -jar facultyscheduling.jar
 * </pre>
 */
import com.unival.facultyscheduling.view.LoginView;
import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    /**
     * Starts the JavaFX application, initializes window state, and displays the login view.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize window state management
        WindowStateManager.initializeWindowState(primaryStage);
        
        // Initialize the login view
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }

    /**
     * Main entry point for launching the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
