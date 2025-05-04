package com.unival.facultyscheduling.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;

public abstract class DashboardView {
    protected Stage stage;
    protected Scene scene;
    protected String userName;
    protected String userRole;
    protected String userDepartment;

    public DashboardView(Stage stage, String userName, String userRole, String userDepartment) {
        this.stage = stage;
        this.userName = userName;
        this.userRole = userRole;
        this.userDepartment = userDepartment;
        
        // Ensure initialization happens on the JavaFX Application Thread
        if (Platform.isFxApplicationThread()) {
            initialize();
        } else {
            Platform.runLater(this::initialize);
        }
    }

    protected void initialize() {
        try {
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f8f9fa;");

            // Header
            HBox header = new HBox(20);
            header.setPadding(new Insets(15));
            header.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            
            VBox userInfo = new VBox(5);
            Label welcomeLabel = new Label("Welcome, " + userName);
            welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label roleLabel = new Label("Role: " + userRole);
            roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            Label departmentLabel = new Label("Department: " + userDepartment);
            departmentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            userInfo.getChildren().addAll(welcomeLabel, roleLabel, departmentLabel);
            
            HBox.setHgrow(userInfo, Priority.ALWAYS);
            
            Button logoutButton = new Button("Logout");
            logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
            logoutButton.setOnAction(e -> Platform.runLater(() -> {
                LoginView loginView = new LoginView(stage);
                loginView.show();
            }));
            
            header.getChildren().addAll(userInfo, logoutButton);
            root.setTop(header);

            // Main content
            VBox mainContent = new VBox(20);
            mainContent.setPadding(new Insets(20));
            initializeMainContent(mainContent);
            root.setCenter(mainContent);

            scene = new Scene(root, 1000, 700);
            
            // Set window title based on role
            stage.setTitle("UniVAL - " + userRole.substring(0, 1).toUpperCase() + userRole.substring(1) + " Dashboard");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing dashboard: " + e.getMessage());
        }
    }

    protected abstract void initializeMainContent(VBox mainContent);

    public void show() {
        if (Platform.isFxApplicationThread()) {
            showImpl();
        } else {
            Platform.runLater(this::showImpl);
        }
    }

    private void showImpl() {
        try {
            if (scene != null) {
                stage.setScene(scene);
                stage.show();
                stage.setMaximized(true);  // Make the window maximized by default
            } else {
                System.err.println("Error: Scene is null in " + getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error showing dashboard: " + e.getMessage());
        }
    }
} 