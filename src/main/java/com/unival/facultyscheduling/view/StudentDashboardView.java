package com.unival.facultyscheduling.view;

/**
 * StudentDashboardView provides the graphical user interface for the student dashboard in the UniVAL system.
 * <p>
 * This class is responsible for rendering and managing all student-facing dashboard features using JavaFX components.
 * It includes navigation between sections relevant to students, displays personalized student information, and provides
 * access to features such as schedules, course lists, and notifications. The dashboard is designed with a modern look and
 * responsive layout for an optimal student experience.
 * <p>
 * Core Features:
 * <ul>
 *     <li>Sidebar navigation for switching between student dashboard sections.</li>
 *     <li>Header displaying the current student and navigation controls.</li>
 *     <li>Dynamic content area for showing schedules, courses, and notifications.</li>
 *     <li>Integration with backend services for retrieving and updating student data.</li>
 *     <li>Window state management for preserving UI state across sessions.</li>
 * </ul>
 * <p>
 * Methods:
 * <ul>
 *     <li>initializeUI: Sets up the dashboard layout, containers, and default content for students.</li>
 *     <li>updateNavigationStyles: Updates sidebar button styles based on the active section.</li>
 *     <li>handleNavigation: Handles navigation to different dashboard sections.</li>
 *     <li>showDashboardContent: Displays the dashboard overview for students.</li>
 *     <li>showScheduleContent: Displays the student's schedule/calendar.</li>
 *     <li>showCoursesContent: Displays the course registration/content view.</li>
 *     <li>showProfileContent: Displays the student's profile information.</li>
 *     <li>createInfoText: Helper method to create styled text elements.</li>
 *     <li>show: Displays the dashboard window.</li>
 * </ul>
 * <p>
 * Note: This class is tightly coupled with the JavaFX platform and expects proper initialization of the JavaFX runtime.
 */
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

    /**
     * Constructs a new StudentDashboardView for the given student.
     *
     * @param stage        The primary stage for the dashboard.
     * @param studentName  The name of the student.
     * @param userRole     The role of the user (should be "STUDENT").
     * @param department   The department of the student.
     */
    public StudentDashboardView(Stage stage, String studentName, String userRole, String department) {
        this.stage = stage;
        this.studentName = studentName;
        this.userRole = userRole;
        this.department = department;
        WindowStateManager.initializeWindowState(stage);
        initializeUI();
    }

    /**
     * Initializes the UI components and layout of the student dashboard.
     * Sets up the sidebar navigation, content area, and default view.
     */
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

    /**
     * Updates the styles of the navigation buttons to reflect the currently selected section.
     *
     * @param buttons The list of navigation button nodes.
     */
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

    /**
     * Handles navigation to the specified dashboard section.
     *
     * @param section The section to navigate to (e.g., "Dashboard", "Schedule", "Courses", "Profile").
     */
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

    /**
     * Displays the dashboard overview content for the student, including enrolled courses.
     */
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

    /**
     * Displays the student's schedule/calendar view.
     */
    private void showScheduleContent() {
        content.getChildren().clear();
        FacultyCalendarView calendarView = new FacultyCalendarView(stage, userName, "STUDENT", userDepartment);
        content.getChildren().add(calendarView.getRoot());
    }

    /**
     * Displays the course registration or course content view for the student.
     */
    private void showCoursesContent() {
        content.getChildren().clear();
        Text contentTitle = new Text("Course Registration");
        contentTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Add course selection view
        CourseSelectionView courseView = new CourseSelectionView(stage, studentName, userRole, department);
        content.getChildren().addAll(contentTitle);
        courseView.show();
    }

    /**
     * Displays the student's profile information.
     */
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

    /**
     * Helper method to create styled text nodes for displaying information.
     *
     * @param text The text content to display.
     * @return A styled Text node.
     */
    private Text createInfoText(String text) {
        Text infoText = new Text(text);
        infoText.setStyle("-fx-font-size: 14px; -fx-fill: #666666;");
        return infoText;
    }

    /**
     * Displays the student dashboard window and applies window state settings.
     */
    public void show() {
        WindowStateManager.applyWindowState(stage);
        stage.show();
    }
}