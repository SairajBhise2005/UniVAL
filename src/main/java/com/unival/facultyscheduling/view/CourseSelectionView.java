package com.unival.facultyscheduling.view;

/**
 * CourseSelectionView provides the user interface for selecting and viewing courses in the UniVAL system.
 * <p>
 * This class is responsible for rendering a course selection screen using JavaFX components. It presents a list of available courses
 * to the user, allowing them to select a course and view its associated schedule or details. The view is styled with a modern look
 * and is intended to be used by both students and faculty, adapting its content and behavior based on the user's role and department.
 * <p>
 * Core Features:
 * <ul>
 *     <li>Header section with title and subtitle for user guidance.</li>
 *     <li>Dynamic main content area for displaying available courses.</li>
 *     <li>Integration with backend or data models to fetch and display course information (implementation-dependent).</li>
 *     <li>Responsive layout and styling for improved user experience.</li>
 * </ul>
 * <p>
 * Methods:
 * <ul>
 *     <li>initialize: Sets up the layout, header, and main content area for course selection.</li>
 *     <li>Other methods (not shown in this snippet) would handle course selection events and data updates.</li>
 * </ul>
 * <p>
 * Note: This class is tightly coupled with the JavaFX platform and expects proper initialization of the JavaFX runtime.
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CourseSelectionView {
    private Stage stage;
    private Scene scene;
    private String userName;
    private String userRole;
    private String userDepartment;

    /**
     * Constructs a new CourseSelectionView for the given user.
     *
     * @param stage           The primary stage for the course selection view.
     * @param userName        The name of the user.
     * @param userRole        The role of the user (e.g., "STUDENT" or "FACULTY").
     * @param userDepartment  The department of the user.
     */
    public CourseSelectionView(Stage stage, String userName, String userRole, String userDepartment) {
        this.stage = stage;
        this.userName = userName;
        this.userRole = userRole;
        this.userDepartment = userDepartment;
        initialize();
    }

    /**
     * Initializes the UI components and layout for the course selection view.
     * Sets up the header and main content area for displaying courses.
     */
    private void initialize() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));
        
        Label title = new Label("Select a Course");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitle = new Label("Choose a course to view its schedule");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(title, subtitle);
        root.setTop(header);

        // Main content
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20));

        // Create a grid for course cards
        GridPane courseGrid = new GridPane();
        courseGrid.setHgap(30);
        courseGrid.setVgap(30);
        courseGrid.setPadding(new Insets(20));
        courseGrid.setAlignment(Pos.CENTER);

        // TODO: Replace with actual courses from database
        String[] courses = {"Object-Oriented Programming", "Database Management Systems"};
        String[] courseCodes = {"CS101", "CS102"};
        String[][] cohorts = {
            {"Cohort A", "Cohort B", "Cohort C"},
            {"Cohort X", "Cohort Y"}
        };

        for (int i = 0; i < courses.length; i++) {
            VBox courseCard = createCourseCard(courses[i], courseCodes[i], cohorts[i]);
            courseGrid.add(courseCard, i % 2, i / 2);
        }

        mainContent.getChildren().add(courseGrid);
        root.setCenter(mainContent);

        // Footer with back button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));
        
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        backButton.setOnAction(e -> {
            if (userRole.equalsIgnoreCase("faculty")) {
                FacultyDashboardView dashboard = new FacultyDashboardView(stage, userName, userRole, userDepartment);
                dashboard.show();
            } else {
                StudentDashboardView dashboard = new StudentDashboardView(stage, userName, userRole, userDepartment);
                dashboard.show();
            }
        });
        
        footer.getChildren().add(backButton);
        root.setBottom(footer);

        scene = new Scene(root, 900, 700);
        stage.setScene(scene);
    }

    private VBox createCourseCard(String courseName, String courseCode, String[] cohorts) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(350);
        card.setPrefHeight(250);

        Label nameLabel = new Label(courseName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label codeLabel = new Label("Course Code: " + courseCode);
        codeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label cohortsLabel = new Label("Available Cohorts:");
        cohortsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox cohortsList = new VBox(8);
        cohortsList.setPadding(new Insets(10, 0, 0, 0));
        for (String cohort : cohorts) {
            Button cohortButton = new Button(cohort);
            cohortButton.setPrefWidth(200);
            cohortButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
            cohortButton.setOnAction(e -> {
                FacultyCalendarView calendarView = new FacultyCalendarView(stage, userName, userRole, userDepartment);
                calendarView.show();
            });
            cohortsList.getChildren().add(cohortButton);
        }

        card.getChildren().addAll(nameLabel, codeLabel, cohortsLabel, cohortsList);
        return card;
    }

    public void show() {
        stage.show();
    }
}
