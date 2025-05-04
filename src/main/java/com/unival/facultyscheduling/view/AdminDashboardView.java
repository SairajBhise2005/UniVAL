package com.unival.facultyscheduling.view;

import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Circle;
import com.unival.facultyscheduling.model.User;
import com.unival.facultyscheduling.model.Course;
import com.unival.facultyscheduling.service.AdminService;
import com.unival.facultyscheduling.service.SupabaseClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

public class AdminDashboardView {
    private Stage stage;
    private Scene scene;
    private String userName;
    private String selectedSection;
    private VBox contentArea;
    private AdminService adminService; // Delay initialization
    private List<User> userList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();

    public AdminDashboardView(Stage stage, String userName) {
        this.stage = stage;
        this.userName = userName;
        WindowStateManager.initializeWindowState(stage);
        initialize();
    }

    private void initialize() {
        // Main container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");
        mainContainer.setPadding(new Insets(0));

        // Left sidebar
        VBox sidebar = createSidebar();
        mainContainer.setLeft(sidebar);

        // Top header
        HBox header = createHeader();
        mainContainer.setTop(header);

        // Content area
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: white;");
        mainContainer.setCenter(contentArea);

        // Show dashboard content by default
        showDashboardContent();

        // Create scene
        scene = new Scene(mainContainer, 1200, 800);
        stage.setTitle("UniVAL - Admin Dashboard");
        stage.setScene(scene);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        sidebar.setPadding(new Insets(20));

        // Title
        Text title = new Text("UniVAL Admin");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #297373;");

        // Navigation buttons
        VBox navButtons = new VBox(10);
        navButtons.setPadding(new Insets(20, 0, 0, 0));
        
        List<String> sections = Arrays.asList("Dashboard", "Users", "Courses", "Reports", "Settings");
        for (String section : sections) {
            Button navButton = createNavButton(section);
            navButtons.getChildren().add(navButton);
        }

        sidebar.getChildren().addAll(title, navButtons);
        return sidebar;
    }

    private Button createNavButton(String section) {
        Button button = new Button(section);
        button.setPrefWidth(210);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 14px; -fx-padding: 12px 20px; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            if (!section.equals(selectedSection)) {
                button.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #297373; -fx-font-size: 14px; -fx-padding: 12px 20px; -fx-cursor: hand;");
            }
        });

        button.setOnMouseExited(e -> {
            if (!section.equals(selectedSection)) {
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 14px; -fx-padding: 12px 20px; -fx-cursor: hand;");
            }
        });

        button.setOnAction(e -> {
            selectedSection = section;
            updateNavigationStyles(button.getParent().getChildrenUnmodifiable());
            handleNavigation(section);
        });

        return button;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);

        // User info
        HBox userInfo = new HBox(15);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        
        Circle avatar = new Circle(25, Color.web("#297373"));
        Text welcomeText = new Text("Welcome, " + userName);
        welcomeText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #297373;");
        userInfo.getChildren().addAll(avatar, welcomeText);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 8; -fx-cursor: hand;"));
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        header.getChildren().addAll(userInfo, spacer, logoutBtn);
        return header;
    }

    private void showDashboardContent() {
        contentArea.getChildren().clear();

        // Dashboard title
        Text title = new Text("Dashboard Overview");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");
        contentArea.getChildren().add(title);

        // Statistics cards
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new Insets(20, 0, 20, 0));

        // Add statistics cards
        addStatCard(statsGrid, "Total Users", "1,234", 0, 0);
        addStatCard(statsGrid, "Active Courses", "45", 0, 1);
        addStatCard(statsGrid, "Pending Evaluations", "12", 1, 0);
        addStatCard(statsGrid, "System Status", "Online", 1, 1);

        contentArea.getChildren().add(statsGrid);

        // Quick actions section
        VBox quickActions = new VBox(15);
        quickActions.setPadding(new Insets(20));
        quickActions.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text actionsTitle = new Text("Quick Actions");
        actionsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");
        quickActions.getChildren().add(actionsTitle);

        // Add quick action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button addUserBtn = createActionButton("Add New User");
        Button createCourseBtn = createActionButton("Create Course");
        Button generateReportBtn = createActionButton("Generate Report");
        
        actionButtons.getChildren().addAll(addUserBtn, createCourseBtn, generateReportBtn);
        quickActions.getChildren().add(actionButtons);
        contentArea.getChildren().add(quickActions);

        // Recent activity section
        VBox recentActivity = new VBox(15);
        recentActivity.setPadding(new Insets(20));
        recentActivity.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text activityTitle = new Text("Recent Activity");
        activityTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");
        recentActivity.getChildren().add(activityTitle);

        // Add sample activity items
        addActivityItem(recentActivity, "New user registration", "2 hours ago");
        addActivityItem(recentActivity, "Course schedule updated", "4 hours ago");
        addActivityItem(recentActivity, "System maintenance completed", "1 day ago");

        contentArea.getChildren().add(recentActivity);
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5; -fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5; -fx-cursor: hand;"));
        return button;
    }

    private void addStatCard(GridPane grid, String title, String value, int row, int col) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(250);

        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 14px; -fx-fill: #6c757d;");

        Text valueText = new Text(value);
        valueText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");

        card.getChildren().addAll(titleText, valueText);
        grid.add(card, col, row);
    }

    private void addActivityItem(VBox container, String description, String time) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10));

        Circle dot = new Circle(5, Color.web("#297373"));
        Text descText = new Text(description);
        descText.setStyle("-fx-font-size: 14px; -fx-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text timeText = new Text(time);
        timeText.setStyle("-fx-font-size: 12px; -fx-fill: #6c757d;");

        item.getChildren().addAll(dot, descText, spacer, timeText);
        container.getChildren().add(item);
    }

    private void handleNavigation(String section) {
        contentArea.getChildren().clear();
        switch (section) {
            case "Dashboard":
                showDashboardContent();
                break;
            case "Users":
                showUsersContent();
                break;
            case "Courses":
                showCoursesContent();
                break;
            case "Reports":
                showReportsContent();
                break;
            case "Settings":
                showSettingsContent();
                break;
        }
    }

    private boolean isOfflineAdmin() {
        return "Admin".equalsIgnoreCase(userName);
    }

    private void showUsersContent() {
        contentArea.getChildren().clear();
        Text title = new Text("User Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");
        contentArea.getChildren().add(title);

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(20, 0, 20, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5; -fx-padding: 8px;");

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("All Roles", "Admin", "Faculty", "Student");
        roleFilter.setValue("All Roles");
        roleFilter.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        Button searchButton = createActionButton("Search");
        searchBox.getChildren().addAll(searchField, roleFilter, searchButton);
        contentArea.getChildren().add(searchBox);

        TableView<User> usersTable = new TableView<>();
        usersTable.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoles().isEmpty() ? "" : cellData.getValue().getRoles().get(0)));
        TableColumn<User, String> departmentCol = new TableColumn<>("Department");
        departmentCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartment()));
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");

        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = createActionButton("Edit Role");
            private final Button deleteBtn = createActionButton("Delete");
            {
                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserRoleDialog(user, usersTable);
                });
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    usersTable.getItems().remove(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });

        usersTable.getColumns().addAll(nameCol, emailCol, roleCol, departmentCol, actionsCol);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        userList.clear();
        if (isOfflineAdmin()) {
            userList.add(new User("1", "admin@example.com", "Admin User", List.of("Admin"), "Administration"));
            userList.add(new User("2", "faculty@example.com", "Faculty User", List.of("Faculty"), "Computer Science"));
            userList.add(new User("3", "student@example.com", "Student User", List.of("Student"), "Mathematics"));
        } else {
            if (adminService == null) adminService = new AdminService();
            JSONArray usersJson = adminService.getAllUsers();
            for (int i = 0; i < usersJson.length(); i++) {
                JSONObject obj = usersJson.getJSONObject(i);
                String id = obj.optString("id");
                String email = obj.optString("email");
                String name = obj.optString("name");
                List<String> roles = new ArrayList<>();
                String role = obj.optString("role");
                if (!role.isEmpty()) roles.add(role);
                String department = obj.optString("department_id");
                userList.add(new User(id, email, name, roles, department));
            }
        }
        usersTable.getItems().setAll(userList);
        contentArea.getChildren().add(usersTable);
    }

    private void showEditUserRoleDialog(User user, TableView<User> usersTable) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit User Role");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "Faculty", "Student");
        roleCombo.setValue(user.getRoles().isEmpty() ? "" : user.getRoles().get(0));
        dialog.getDialogPane().setContent(roleCombo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? roleCombo.getValue() : null);
        dialog.showAndWait().ifPresent(newRole -> {
            if (adminService.updateUserRole(user.getId(), newRole)) {
                user.getRoles().clear();
                user.getRoles().add(newRole);
                usersTable.refresh();
            }
        });
    }

    private void showCoursesContent() {
        contentArea.getChildren().clear();
        Text title = new Text("Course Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");
        contentArea.getChildren().add(title);

        VBox courseForm = new VBox(15);
        courseForm.setPadding(new Insets(20));
        courseForm.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text formTitle = new Text("Add New Course");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");

        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course Name");
        courseNameField.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5; -fx-padding: 8px;");

        TextField courseCodeField = new TextField();
        courseCodeField.setPromptText("Course Code");
        courseCodeField.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5; -fx-padding: 8px;");

        ComboBox<String> departmentCombo = new ComboBox<>();
        departmentCombo.getItems().addAll("Computer Science", "Mathematics", "Physics", "Chemistry");
        departmentCombo.setPromptText("Select Department");
        departmentCombo.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        Button createButton = createActionButton("Create Course");
        createButton.setOnAction(e -> {
            String name = courseNameField.getText();
            String code = courseCodeField.getText();
            String dept = departmentCombo.getValue();
            if (!name.isEmpty() && !code.isEmpty() && dept != null) {
                courseList.add(new Course(String.valueOf(courseList.size() + 1), code, name, dept));
                showCoursesContent();
            }
        });
        courseForm.getChildren().addAll(formTitle, courseNameField, courseCodeField, departmentCombo, createButton);
        contentArea.getChildren().add(courseForm);

        VBox coursesList = new VBox(10);
        coursesList.setPadding(new Insets(20, 0, 0, 0));
        Text listTitle = new Text("Existing Courses");
        listTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");
        coursesList.getChildren().add(listTitle);

        courseList.clear();
        if (isOfflineAdmin()) {
            courseList.add(new Course("1", "CS101", "Introduction to Programming", "Computer Science"));
            courseList.add(new Course("2", "MATH201", "Calculus II", "Mathematics"));
            courseList.add(new Course("3", "PHY101", "Physics Fundamentals", "Physics"));
        } else {
            try {
                JSONArray coursesJson = new JSONArray(SupabaseClient.getAllCourses());
                for (int i = 0; i < coursesJson.length(); i++) {
                    JSONObject obj = coursesJson.getJSONObject(i);
                    Course course = new Course(obj.optString("id"), obj.optString("code"), obj.optString("name"), obj.optString("department"));
                    courseList.add(course);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (Course course : courseList) {
            addCourseItemWithActions(coursesList, course);
        }
        contentArea.getChildren().add(coursesList);
    }

    private void addCourseItemWithActions(VBox container, Course course) {
        HBox courseItem = new HBox(15);
        courseItem.setPadding(new Insets(10));
        courseItem.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        VBox courseInfo = new VBox(5);
        Text courseCode = new Text(course.getCode());
        courseCode.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #297373;");
        Text courseName = new Text(course.getName());
        courseName.setStyle("-fx-font-size: 14px; -fx-fill: #2c3e50;");
        Text deptText = new Text(course.getDepartment());
        deptText.setStyle("-fx-font-size: 12px; -fx-fill: #6c757d;");
        courseInfo.getChildren().addAll(courseCode, courseName, deptText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editButton = createActionButton("Edit");
        editButton.setOnAction(e -> showEditCourseDialog(course));
        Button deleteButton = createActionButton("Delete");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 5;");
        deleteButton.setOnAction(e -> {
            try {
                String supabaseUrl = com.unival.facultyscheduling.config.AppConfig.getSupabaseUrl();
                String supabaseKey = com.unival.facultyscheduling.config.AppConfig.getSupabaseKey();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(supabaseUrl + "/rest/v1/courses?id=eq." + course.getId()))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .DELETE()
                    .build();
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204) {
                    courseList.remove(course);
                    showCoursesContent();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        courseItem.getChildren().addAll(courseInfo, spacer, editButton, deleteButton);
        container.getChildren().add(courseItem);
    }

    private void showEditCourseDialog(Course course) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        TextField nameField = new TextField(course.getName());
        TextField codeField = new TextField(course.getCode());
        TextField deptField = new TextField(course.getDepartment());
        VBox vbox = new VBox(10, new Label("Name:"), nameField, new Label("Code:"), codeField, new Label("Department:"), deptField);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new Course(course.getId(), codeField.getText(), nameField.getText(), deptField.getText()) : null);
        dialog.showAndWait().ifPresent(updatedCourse -> {
            try {
                JSONObject json = new JSONObject();
                json.put("name", updatedCourse.getName());
                json.put("code", updatedCourse.getCode());
                json.put("department", updatedCourse.getDepartment());
                String supabaseUrl = com.unival.facultyscheduling.config.AppConfig.getSupabaseUrl();
                String supabaseKey = com.unival.facultyscheduling.config.AppConfig.getSupabaseKey();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(supabaseUrl + "/rest/v1/courses?id=eq." + updatedCourse.getId()))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .method("PATCH", java.net.http.HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    showCoursesContent();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void showReportsContent() {
        contentArea.getChildren().clear();
        Text title = new Text("Reports");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");
        contentArea.getChildren().add(title);

        VBox reportOptions = new VBox(15);
        reportOptions.setPadding(new Insets(20));
        reportOptions.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text optionsTitle = new Text("Generate Report");
        optionsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");

        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll("User Activity Report", "Course Enrollment Report", "Evaluation Schedule Report", "System Usage Report");
        reportType.setPromptText("Select Report Type");
        reportType.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        DatePicker startDate = new DatePicker();
        startDate.setPromptText("Start Date");
        startDate.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        DatePicker endDate = new DatePicker();
        endDate.setPromptText("End Date");
        endDate.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        Button generateButton = createActionButton("Generate Report");
        reportOptions.getChildren().addAll(optionsTitle, reportType, startDate, endDate, generateButton);
        contentArea.getChildren().add(reportOptions);

        VBox recentReports = new VBox(15);
        recentReports.setPadding(new Insets(20, 0, 0, 0));
        Text recentTitle = new Text("Recent Reports");
        recentTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");
        recentReports.getChildren().add(recentTitle);

        if (isOfflineAdmin()) {
            addReportItem(recentReports, "User Activity Report", "Sample Data", "View");
            addReportItem(recentReports, "Course Enrollment Report", "Sample Data", "View");
            addReportItem(recentReports, "System Usage Report", "Sample Data", "View");
        } else {
            if (adminService == null) adminService = new AdminService();
            try {
                String reportTypeValue = reportType.getValue();
                if (reportTypeValue == null) reportTypeValue = "User Activity Report";
                JSONArray reportData = new JSONArray();
                if (reportTypeValue.equals("User Activity Report")) {
                    reportData = adminService.getAllUsers();
                } else if (reportTypeValue.equals("Course Enrollment Report")) {
                    reportData = new JSONArray(SupabaseClient.getAllCourses());
                }
                for (int i = 0; i < reportData.length(); i++) {
                    JSONObject obj = reportData.getJSONObject(i);
                    addReportItem(recentReports, reportTypeValue, obj.optString("name", obj.optString("email", "")), "View");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        contentArea.getChildren().add(recentReports);
    }

    private void showSettingsContent() {
        contentArea.getChildren().clear();
        Text title = new Text("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #297373;");
        contentArea.getChildren().add(title);

        VBox settingsContainer = new VBox(20);
        settingsContainer.setPadding(new Insets(20));

        VBox systemSettings = createSettingsSection("System Settings");
        VBox securitySettings = createSettingsSection("Security Settings");
        VBox backupSettings = createSettingsSection("Backup Settings");

        CheckBox emailNotif = new CheckBox();
        ComboBox<String> tzCombo = new ComboBox<>(javafx.collections.FXCollections.observableArrayList("UTC", "EST", "PST"));
        TextField sessionTimeout = new TextField("30");
        addSettingItem(systemSettings, "Enable Email Notifications", emailNotif);
        addSettingItem(systemSettings, "Default Time Zone", tzCombo);
        addSettingItem(systemSettings, "Session Timeout (minutes)", sessionTimeout);

        CheckBox twoFA = new CheckBox();
        ComboBox<String> pwPolicy = new ComboBox<>(javafx.collections.FXCollections.observableArrayList("Basic", "Medium", "Strong"));
        ComboBox<String> sessionMgmt = new ComboBox<>(javafx.collections.FXCollections.observableArrayList("Single Session", "Multiple Sessions"));
        addSettingItem(securitySettings, "Require Two-Factor Authentication", twoFA);
        addSettingItem(securitySettings, "Password Policy", pwPolicy);
        addSettingItem(securitySettings, "Session Management", sessionMgmt);

        CheckBox autoBackup = new CheckBox();
        ComboBox<String> backupFreq = new ComboBox<>(javafx.collections.FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
        TextField backupLoc = new TextField("C:/backups");
        addSettingItem(backupSettings, "Automatic Backup", autoBackup);
        addSettingItem(backupSettings, "Backup Frequency", backupFreq);
        addSettingItem(backupSettings, "Backup Location", backupLoc);

        settingsContainer.getChildren().addAll(systemSettings, securitySettings, backupSettings);
        contentArea.getChildren().add(settingsContainer);

        Button saveButton = createActionButton("Save Settings");
        saveButton.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 30px; -fx-background-radius: 5;");
        saveButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Settings saved (offline mode).");
            alert.showAndWait();
        });
        contentArea.getChildren().add(saveButton);
    }

    private VBox createSettingsSection(String title) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text sectionTitle = new Text(title);
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #297373;");
        section.getChildren().add(sectionTitle);

        return section;
    }

    private void addSettingItem(VBox container, String label, Control control) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 0, 0, 0));

        Text labelText = new Text(label);
        labelText.setStyle("-fx-font-size: 14px; -fx-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        control.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        item.getChildren().addAll(labelText, spacer, control);
        container.getChildren().add(item);
    }

    private void addCourseItem(VBox container, String code, String name, String department) {
        HBox courseItem = new HBox(15);
        courseItem.setPadding(new Insets(10));
        courseItem.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        VBox courseInfo = new VBox(5);
        Text courseCode = new Text(code);
        courseCode.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #297373;");
        Text courseName = new Text(name);
        courseName.setStyle("-fx-font-size: 14px; -fx-fill: #2c3e50;");
        Text deptText = new Text(department);
        deptText.setStyle("-fx-font-size: 12px; -fx-fill: #6c757d;");
        courseInfo.getChildren().addAll(courseCode, courseName, deptText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editButton = createActionButton("Edit");
        Button deleteButton = createActionButton("Delete");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 5;");

        courseItem.getChildren().addAll(courseInfo, spacer, editButton, deleteButton);
        container.getChildren().add(courseItem);
    }

    private void addReportItem(VBox container, String name, String period, String action) {
        HBox reportItem = new HBox(15);
        reportItem.setPadding(new Insets(10));
        reportItem.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        VBox reportInfo = new VBox(5);
        Text reportName = new Text(name);
        reportName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #297373;");
        Text reportPeriod = new Text(period);
        reportPeriod.setStyle("-fx-font-size: 14px; -fx-fill: #6c757d;");
        reportInfo.getChildren().addAll(reportName, reportPeriod);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionButton = createActionButton(action);
        reportItem.getChildren().addAll(reportInfo, spacer, actionButton);
        container.getChildren().add(reportItem);
    }

    private void updateNavigationStyles(List<Node> buttons) {
        for (Node node : buttons) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText().equals(selectedSection)) {
                    button.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 20px; -fx-cursor: hand;");
                } else {
                    button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 14px; -fx-padding: 12px 20px; -fx-cursor: hand;");
                }
            }
        }
    }

    public void show() {
        stage.show();
    }
}