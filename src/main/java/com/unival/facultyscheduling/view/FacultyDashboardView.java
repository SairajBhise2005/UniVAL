package com.unival.facultyscheduling.view;

/**
 * FacultyDashboardView provides the graphical user interface for the faculty dashboard in the UniVAL system.
 * <p>
 * This class is responsible for rendering and managing all faculty-facing dashboard features using JavaFX components.
 * It includes navigation between sections relevant to faculty members, displays personalized faculty information,
 * and provides access to features such as schedules, course lists, and notifications. The dashboard is designed
 * with a modern look and responsive layout for an optimal faculty experience.
 * <p>
 * Core Features:
 * <ul>
 *     <li>Sidebar navigation for switching between faculty dashboard sections.</li>
 *     <li>Header displaying the current faculty member and navigation controls.</li>
 *     <li>Dynamic content area for showing schedules, courses, and notifications.</li>
 *     <li>Integration with backend services for retrieving and updating faculty data.</li>
 *     <li>Window state management for preserving UI state across sessions.</li>
 * </ul>
 * <p>
 * Methods:
 * <ul>
 *     <li>initialize: Sets up the dashboard layout, containers, and default content for faculty.</li>
 *     <li>Other methods (not shown in this snippet) handle navigation, data display, and event handling.</li>
 * </ul>
 * <p>
 * Note: This class is tightly coupled with the JavaFX platform and expects proper initialization of the JavaFX runtime.
 */
import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;
import java.util.Arrays;

public class FacultyDashboardView {
    private Stage stage;
    private Scene scene;
    private String userName;
    private String userRole;
    private String userDepartment;
    private VBox content;
    private String selectedSection;

    /**
     * Constructs a new FacultyDashboardView for the given faculty member.
     *
     * @param stage           The primary stage for the dashboard.
     * @param userName        The name of the faculty member.
     * @param userRole        The role of the user (should be "FACULTY").
     * @param userDepartment  The department of the faculty member.
     */
    public FacultyDashboardView(Stage stage, String userName, String userRole, String userDepartment) {
        this.stage = stage;
        this.userName = userName;
        this.userRole = userRole;
        this.userDepartment = userDepartment;
        WindowStateManager.initializeWindowState(stage);
        initialize();
    }

    /**
     * Initializes the UI components and layout of the faculty dashboard.
     * Sets up the sidebar navigation, content area, and default view.
     */
    private void initialize() {
        // Main container with gradient background
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #85FFC7, #297373);");
        mainContainer.setPadding(new Insets(20));

        // Navigation panel
        VBox navigationPanel = new VBox(10);
        navigationPanel.setPadding(new Insets(20));
        navigationPanel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10 0 0 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        navigationPanel.setPrefWidth(220);
        navigationPanel.setAlignment(Pos.TOP_CENTER);

        // Add logo or title
        Text title = new Text("UniVAL");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #297373;");

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
                if (!section.equals("Dashboard")) {
                    navButton.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #297373; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
                }
            });
            navButton.setOnMouseExited(e -> {
                if (!section.equals("Dashboard")) {
                    navButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
                }
            });
            navButton.setOnAction(e -> {
                handleNavigation(section);
            });
            navButtons.getChildren().add(navButton);
        }

        // Highlight the default selected section
        Button dashboardButton = (Button) navButtons.getChildren().get(0);
        dashboardButton.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #297373; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");

        navigationPanel.getChildren().addAll(title, navButtons);
        mainContainer.setLeft(navigationPanel);

        // Content area
        content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 0 10 10 0;");
        VBox.setVgrow(content, Priority.ALWAYS);
        mainContainer.setCenter(content);

        // Header section
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox userInfo = new VBox(5);
        Text welcomeText = new Text("Welcome, " + userName);
        welcomeText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");
        Text roleText = new Text(userRole.substring(0, 1).toUpperCase() + userRole.substring(1));
        roleText.setStyle("-fx-font-size: 16px; -fx-fill: #666666;");
        Text deptText = new Text(userDepartment);
        deptText.setStyle("-fx-font-size: 14px; -fx-fill: #666666;");
        userInfo.getChildren().addAll(welcomeText, roleText, deptText);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        header.getChildren().addAll(userInfo, logoutBtn);
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        mainContainer.setTop(header);

        // Create scene
        scene = new Scene(mainContainer);
        stage.setTitle("UniVAL - Faculty Dashboard");
        stage.setScene(scene);

        // Show dashboard content by default
        showDashboardContent();
    }

    private void showDashboardContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Dashboard Overview");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Create dashboard summary
        VBox summary = new VBox(15);
        summary.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");

        Text coursesTitle = new Text("Teaching Courses");
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

    private void showCoursesContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Course Management");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Add course selection view
        CourseSelectionView courseView = new CourseSelectionView(stage, userName, userRole, userDepartment);
        content.getChildren().addAll(contentTitle);
        courseView.show();

        // Add Back to Dashboard button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        backBtn.setOnAction(e -> showDashboardContent());
        content.getChildren().add(backBtn);
    }

    private void showProfileContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Faculty Profile");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        VBox profileInfo = new VBox(15);
        profileInfo.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");

        // Add profile information
        profileInfo.getChildren().addAll(
            createInfoText("Name: " + userName),
            createInfoText("Role: " + userRole),
            createInfoText("Department: " + userDepartment)
        );

        content.getChildren().addAll(contentTitle, profileInfo);

        // Add Back to Dashboard button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;"));
        backBtn.setOnAction(e -> showDashboardContent());
        content.getChildren().add(backBtn);
    }

    private void showScheduleContent() {
        content.getChildren().clear();
        FacultyCalendarView calendarView = new FacultyCalendarView(stage, userName, "FACULTY", userDepartment);
        content.getChildren().add(calendarView.getRoot());
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
}