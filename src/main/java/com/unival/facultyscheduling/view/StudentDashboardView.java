package com.unival.facultyscheduling.view;

import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.Modality;
import javafx.scene.Node;
import java.util.List;
import java.util.Arrays;

public class StudentDashboardView {
    private Stage stage;
    private Scene scene;
    private String userName;
    private String userRole;
    private String userDepartment;
    private VBox content;
    private String studentId;
    private String studentName;
    private String department;
    private BorderPane root;
    private String selectedSection;

    public StudentDashboardView(Stage stage, String studentName, String userRole, String department) {
        this.stage = stage;
        this.studentName = studentName;
        this.userRole = userRole;
        this.department = department;
        WindowStateManager.initializeWindowState(stage);
        initializeUI();
    }

    private void initializeUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #85FFC7, #297373);");
        root.setPadding(new Insets(20));

        // Create navigation panel with only student-specific options
        VBox navigationPanel = new VBox(10);
        navigationPanel.setPadding(new Insets(20));
        navigationPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10 0 0 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        navigationPanel.setPrefWidth(220);
        navigationPanel.setAlignment(Pos.TOP_CENTER);

        // Add logo or title
        Text title = new Text("UniVAL");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Add logout button to the top of the navigation panel
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        // Add navigation buttons with enhanced styling
        List<String> sections = Arrays.asList("Dashboard", "Schedule", "Courses", "Profile");
        VBox navButtons = new VBox(15);
        navButtons.setAlignment(Pos.TOP_CENTER);
        navButtons.setPadding(new Insets(30, 0, 0, 0));
        for (String section : sections) {
            Button navButton = new Button(section);
            navButton.setPrefWidth(180);
            navButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
            navButton.setOnMouseEntered(e -> {
                if (!selectedSection.equals(section)) {
                    navButton.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #297373; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
                }
            });
            navButton.setOnMouseExited(e -> {
                if (!selectedSection.equals(section)) {
                    navButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
                }
            });
            navButton.setOnAction(e -> {
                selectedSection = section;
                updateNavigationStyles(navButtons.getChildren());
                handleNavigation(section);
            });
            navButtons.getChildren().add(navButton);
        }

        // Highlight the default selected section
        selectedSection = "Dashboard";
        updateNavigationStyles(navButtons.getChildren());

        navigationPanel.getChildren().addAll(title, logoutBtn, navButtons);
        root.setLeft(navigationPanel);

        // Initialize content area
        content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 0 10 10 0;");
        VBox.setVgrow(content, Priority.ALWAYS);
        root.setCenter(content);

        // Show dashboard content by default
        showDashboardContent();

        // Create scene
        scene = new Scene(root);
        stage.setTitle("UniVAL - Student Dashboard");
        stage.setScene(scene);
    }

    private void updateNavigationStyles(List<Node> buttons) {
        for (Node button : buttons) {
            if (button instanceof Button) {
                Button navButton = (Button) button;
                if (navButton.getText().equals(selectedSection)) {
                    navButton.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
                } else {
                    navButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
                }
            }
        }
    }

    private void handleNavigation(String section) {
        switch (section) {
            case "Dashboard":
                showDashboardContent();
                break;
            case "Schedule":
                showScheduleContent();
                break;
            case "Courses":
                showCoursesContent();
                break;
            case "Profile":
                showProfileContent();
                break;
        }
    }

    private void showDashboardContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Dashboard Overview");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Create dashboard summary
        VBox summary = new VBox(15);
        summary.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");

        Text coursesTitle = new Text("Enrolled Courses");
        coursesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #297373;");
        
        VBox coursesList = new VBox(10);
        coursesList.getChildren().addAll(
            createInfoText("Data Structures and Algorithms"),
            createInfoText("Database Management Systems"),
            createInfoText("Web Development")
        );

        summary.getChildren().addAll(coursesTitle, coursesList);
        content.getChildren().addAll(contentTitle, summary);
    }

    private void showScheduleContent() {
        content.getChildren().clear();
        FacultyCalendarView calendarView = new FacultyCalendarView(stage, userName, "STUDENT", userDepartment);
        content.getChildren().add(calendarView.getRoot());
    }

    private void showCoursesContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Course Registration");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Add course selection view
        CourseSelectionView courseView = new CourseSelectionView(stage, studentName, userRole, department);
        content.getChildren().addAll(contentTitle);
        courseView.show();
    }

    private void showProfileContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Student Profile");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        VBox profileInfo = new VBox(15);
        profileInfo.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");

        // Add profile information
        profileInfo.getChildren().addAll(
            createInfoText("Name: " + studentName),
            createInfoText("Role: " + userRole),
            createInfoText("Department: " + department)
        );

        content.getChildren().addAll(contentTitle, profileInfo);
    }

    private Text createInfoText(String text) {
        Text infoText = new Text(text);
        infoText.setStyle("-fx-font-size: 14px; -fx-fill: #666666;");
        return infoText;
    }

    public void show() {
        WindowStateManager.applyWindowState(stage);
        stage.show();
    }
}