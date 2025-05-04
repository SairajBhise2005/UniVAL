package com.unival.facultyscheduling.view;

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

    public CourseSelectionView(Stage stage, String userName, String userRole, String userDepartment) {
        this.stage = stage;
        this.userName = userName;
        this.userRole = userRole;
        this.userDepartment = userDepartment;
        initialize();
    }

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
