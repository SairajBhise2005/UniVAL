package com.unival.facultyscheduling.view;

import com.unival.facultyscheduling.service.SupabaseClient;
import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

public class RegistrationView {
    private Stage stage;
    private Scene scene;
    private Text actiontarget;
    private Runnable onBackToLogin;
    private String selectedRole;

    public RegistrationView(Stage stage, Runnable onBackToLogin) {
        this.stage = stage;
        this.onBackToLogin = onBackToLogin;
        WindowStateManager.initializeWindowState(stage);
        showRoleSelectionDialog();
    }

    private void showRoleSelectionDialog() {
        // Main container with gradient background
        VBox mainContainer = new VBox(20);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #85FFC7, #297373);");
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        // Role selection card
        VBox roleCard = new VBox(20);
        roleCard.setMaxWidth(400);
        roleCard.setAlignment(Pos.CENTER);
        roleCard.setPadding(new Insets(30));
        roleCard.setStyle("-fx-background-color: white; -fx-background-radius: 10;" +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text title = new Text("Choose Your Role");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #297373;");

        Text subtitle = new Text("Select your role to continue registration");
        subtitle.setStyle("-fx-font-size: 16px; -fx-fill: #666666; -fx-padding: 0 0 20 0;");

        Button facultyBtn = createButton("Faculty", "#297373");
        Button studentBtn = createButton("Student", "#297373");
        Button backBtn = createButton("Back to Login", "#FF8552");

        // Add hover effects
        addButtonHoverEffect(facultyBtn, "#297373", "#FF8552");
        addButtonHoverEffect(studentBtn, "#297373", "#FF8552");
        addButtonHoverEffect(backBtn, "#FF8552", "#297373");

        facultyBtn.setOnAction(e -> {
            selectedRole = "faculty";
            showRegistrationForm();
        });

        studentBtn.setOnAction(e -> {
            selectedRole = "student";
            showRegistrationForm();
        });

        backBtn.setOnAction(e -> onBackToLogin.run());

        roleCard.getChildren().addAll(title, subtitle, facultyBtn, studentBtn, backBtn);
        mainContainer.getChildren().add(roleCard);

        scene = new Scene(mainContainer, 800, 600);
        stage.setTitle("UniVAL - Role Selection");
        stage.setScene(scene);
    }

    private void showRegistrationForm() {
        // Main container with gradient background
        VBox mainContainer = new VBox(20);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #85FFC7, #297373);");
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        // Registration card
        VBox registrationCard = new VBox(20);
        registrationCard.setMaxWidth(500);
        registrationCard.setAlignment(Pos.CENTER);
        registrationCard.setPadding(new Insets(30));
        registrationCard.setStyle("-fx-background-color: white; -fx-background-radius: 10;" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Title section
        Text title = new Text(selectedRole.substring(0, 1).toUpperCase() + selectedRole.substring(1) + " Registration");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #297373;");

        Text subtitle = new Text("Create your account");
        subtitle.setStyle("-fx-font-size: 16px; -fx-fill: #666666; -fx-padding: 0 0 20 0;");

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setMaxWidth(400);

        // Form fields
        VBox nameBox = createInputField("Full Name", "text");
        TextField nameField = (TextField) nameBox.getChildren().get(1);

        VBox emailBox = createInputField("Email Address", "email");
        TextField emailField = (TextField) emailBox.getChildren().get(1);

        VBox passwordBox = createInputField("Password", "password");
        PasswordField passwordField = (PasswordField) passwordBox.getChildren().get(1);

        // Department selection
        VBox departmentBox = new VBox(5);
        Label departmentLabel = new Label("Department");
        departmentLabel.setStyle("-fx-text-fill: #297373; -fx-font-weight: bold;");
        ComboBox<String> departmentComboBox = new ComboBox<>();
        departmentComboBox.getItems().addAll(
            "School of Computational Data Sciences",
            "School of Liberal Arts and Design Studies",
            "School of Business Studies",
            "School of Legal Studies and Governance"
        );
        departmentComboBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #297373;" +
                                  "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8;");
        departmentComboBox.setMaxWidth(Double.MAX_VALUE);
        departmentBox.getChildren().addAll(departmentLabel, departmentComboBox);

        // Additional fields for students
        VBox yearBox = new VBox(5);
        ComboBox<String> yearComboBox = new ComboBox<>();
        if (selectedRole.equals("student")) {
            Label yearLabel = new Label("Year");
            yearLabel.setStyle("-fx-text-fill: #297373; -fx-font-weight: bold;");
            yearComboBox.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year");
            yearComboBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #297373;" +
                                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8;");
            yearComboBox.setMaxWidth(Double.MAX_VALUE);
            yearBox.getChildren().addAll(yearLabel, yearComboBox);
        }

        // Buttons
        Button registerButton = createButton("Register", "#297373");
        Button backButton = createButton("Back", "#FF8552");

        // Add hover effects
        addButtonHoverEffect(registerButton, "#297373", "#FF8552");
        addButtonHoverEffect(backButton, "#FF8552", "#297373");

        // Error message
        actiontarget = new Text();
        actiontarget.setStyle("-fx-fill: #FF8552;");

        // Add form fields
        formContainer.getChildren().addAll(nameBox, emailBox, passwordBox, departmentBox);
        if (selectedRole.equals("student")) {
            formContainer.getChildren().add(yearBox);
        }
        formContainer.getChildren().addAll(registerButton, backButton, actiontarget);

        // Handle registration
        registerButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                String department = departmentComboBox.getValue();
                int year = 0;

                if (selectedRole.equals("student")) {
                    String yearStr = yearComboBox.getValue();
                    if (yearStr != null) {
                        year = Integer.parseInt(yearStr.substring(0, 1));
                    }
                }

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || department == null ||
                    (selectedRole.equals("student") && yearComboBox.getValue() == null)) {
                    actiontarget.setText("Please fill in all required fields");
                    return;
                }

                String response = SupabaseClient.registerUser(name, email, password, selectedRole, department, year);

                if (response != null && !response.isEmpty()) {
                    showSuccessDialog();
                } else {
                    actiontarget.setText("Registration failed");
                }
            } catch (IOException ex) {
                actiontarget.setText("Error: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> showRoleSelectionDialog());

        // Add components to registration card
        registrationCard.getChildren().addAll(title, subtitle, formContainer);
        mainContainer.getChildren().add(registrationCard);

        scene = new Scene(mainContainer, 800, 700);
        stage.setTitle("UniVAL - Registration");
        stage.setScene(scene);
    }

    private VBox createInputField(String label, String type) {
        VBox container = new VBox(5);
        Label fieldLabel = new Label(label);
        fieldLabel.setStyle("-fx-text-fill: #297373; -fx-font-weight: bold;");

        Control inputField;
        if (type.equals("password")) {
            inputField = new PasswordField();
        } else {
            inputField = new TextField();
        }

        inputField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #297373; " +
                          "-fx-border-radius: 5; -fx-background-radius: 5; " +
                          "-fx-padding: 12px;");

        container.getChildren().addAll(fieldLabel, inputField);
        return container;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                       "-fx-font-size: 14px; -fx-padding: 12px; -fx-background-radius: 5;" +
                       "-fx-cursor: hand;");
        return button;
    }

    private void addButtonHoverEffect(Button button, String defaultColor, String hoverColor) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; " +
                                                     "-fx-font-size: 14px; -fx-padding: 12px; -fx-background-radius: 5;" +
                                                     "-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + defaultColor + "; -fx-text-fill: white; " +
                                                    "-fx-font-size: 14px; -fx-padding: 12px; -fx-background-radius: 5;" +
                                                    "-fx-cursor: hand;"));
    }

    private void showSuccessDialog() {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration Successful");
                    alert.setHeaderText(null);
        alert.setContentText("Registration successful! You will be redirected to the login page.");
        
        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        
        // Style the buttons
        dialogPane.lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #297373; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        alert.showAndWait();
        onBackToLogin.run();
    }

    public void show() {
        WindowStateManager.applyWindowState(stage);
        stage.show();
    }
} 