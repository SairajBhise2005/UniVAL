package com.unival.facultyscheduling.view;

/**
 * LoginView provides the graphical user interface for user authentication in the UniVAL system.
 * <p>
 * This class is responsible for rendering the login screen using JavaFX components. It includes a styled login card,
 * input fields for email and password, a 'Remember me' option, and navigation to registration or password recovery.
 * The view handles user authentication, including offline admin login and integration with Supabase for user data.
 * It redirects authenticated users to their respective dashboards based on their role (admin, faculty, student).
 * <p>
 * Core Features:
 * <ul>
 *     <li>Modern, responsive login card UI with branding and helpful prompts.</li>
 *     <li>Email and password input fields with validation and prompt text.</li>
 *     <li>'Remember me' functionality and password recovery link.</li>
 *     <li>Integration with SupabaseClient for user authentication.</li>
 *     <li>Offline admin authentication for fallback scenarios.</li>
 *     <li>Role-based redirection to Admin, Faculty, or Student dashboards.</li>
 * </ul>
 * <p>
 * Methods:
 * <ul>
 *     <li>initialize: Sets up the login UI, form fields, and event handlers.</li>
 *     <li>createInputField: Helper to create styled input fields for email and password.</li>
 *     <li>handleLogin: Handles authentication logic and dashboard redirection.</li>
 *     <li>show: Displays the login window.</li>
 * </ul>
 * <p>
 * Note: This class is tightly coupled with the JavaFX platform and expects proper initialization of the JavaFX runtime.
 */
import com.unival.facultyscheduling.service.SupabaseClient;
import com.unival.facultyscheduling.util.WindowStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import java.io.IOException;

public class LoginView {
    private Stage stage;
    private Scene scene;
    private Text actiontarget;

    /**
     * Constructs a new LoginView for the given application stage.
     *
     * @param stage The primary JavaFX stage for the login window.
     */
    public LoginView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    /**
     * Initializes the UI components and layout for the login view.
     * Sets up the login card, form fields, event handlers, and scene.
     */
    private void initialize() {
        // Initialize window state management
        WindowStateManager.initializeWindowState(stage);
        
        // Main container with gradient background
        VBox mainContainer = new VBox(20);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #85FFC7, #297373);");
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        // Set stage properties
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Login card container
        VBox loginCard = new VBox(20);
        loginCard.setMaxWidth(400);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(30));
        loginCard.setStyle("-fx-background-color: white; -fx-background-radius: 10;" +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Title section
        Text title = new Text("Welcome Back");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #297373;");

        Text subtitle = new Text("Sign in to your account");
        subtitle.setStyle("-fx-font-size: 16px; -fx-fill: #666666; -fx-padding: 0 0 20 0;");

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setMaxWidth(320);

        // Email field with icon
        VBox emailBox = createInputField("Email", "email-address");
        TextField emailField = (TextField) emailBox.getChildren().get(1);
        emailField.setPromptText("Enter your email");

        // Password field with icon
        VBox passwordBox = createInputField("Password", "password");
        PasswordField passwordField = (PasswordField) passwordBox.getChildren().get(1);
        passwordField.setPromptText("Enter your password");

        // Remember me and Forgot password
        HBox optionsBox = new HBox(10);
        optionsBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-text-fill: #7f8c8d;");
        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setStyle("-fx-text-fill: #3498db;");
        HBox.setHgrow(rememberMe, Priority.ALWAYS);
        optionsBox.getChildren().addAll(rememberMe, forgotPassword);

        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #297373; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-padding: 12px; -fx-background-radius: 5;" +
                           "-fx-cursor: hand;");

        // Hover effect for login button
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; " +
                                                               "-fx-font-size: 14px; -fx-padding: 12px; -fx-background-radius: 5;" +
                                                               "-fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #297373; -fx-text-fill: white; " +
                                                              "-fx-font-size: 14px; -fx-padding: 12px; -fx-background-radius: 5;" +
                                                              "-fx-cursor: hand;"));

        // Register link
        HBox registerBox = new HBox(5);
        registerBox.setAlignment(Pos.CENTER);
        Text registerText = new Text("Don't have an account?");
        registerText.setStyle("-fx-fill: #666666;");
        Hyperlink registerLink = new Hyperlink("Register");
        registerLink.setStyle("-fx-text-fill: #FF8552; -fx-underline: false;");
        registerBox.getChildren().addAll(registerText, registerLink);

        // Error message
        actiontarget = new Text();
        actiontarget.setStyle("-fx-fill: #FF8552;");

        // Add all components to form container
        formContainer.getChildren().addAll(
            emailBox, passwordBox, optionsBox, loginButton, registerBox, actiontarget
        );

        // Add components to login card
        loginCard.getChildren().addAll(title, subtitle, formContainer);

        // Add login card to main container
        mainContainer.getChildren().add(loginCard);

        // Handle login
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));

        // Handle register link
        registerLink.setOnAction(e -> {
            RegistrationView registrationView = new RegistrationView(stage, () -> {
                LoginView loginView = new LoginView(stage);
                loginView.show();
            });
            registrationView.show();
        });

        scene = new Scene(mainContainer, 800, 600);
    }

    /**
     * Creates a styled input field for the login form.
     *
     * @param label The label for the input field.
     * @param type  The type of input ("email-address" or "password").
     * @return A VBox containing the label and input field.
     */
    private VBox createInputField(String label, String type) {
        VBox container = new VBox(5);
        Label fieldLabel = new Label(label);
        fieldLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        Control inputField;
        if (type.equals("password")) {
            inputField = new PasswordField();
        } else {
            inputField = new TextField();
        }

        inputField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; " +
                          "-fx-border-radius: 5; -fx-background-radius: 5; " +
                          "-fx-padding: 12px;");

        container.getChildren().addAll(fieldLabel, inputField);
        return container;
    }

    /**
     * Handles user authentication and dashboard redirection.
     * Validates input, performs offline admin login, or authenticates via SupabaseClient.
     * Redirects users to their respective dashboards based on role.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     */
    private void handleLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            actiontarget.setText("Please fill in all fields");
            return;
        }

        // Fully offline admin authentication
        if ("admin".equals(email) && "admin123$".equals(password)) {
            System.out.println("Admin credentials accepted, showing dashboard...");
            javafx.application.Platform.runLater(() -> {
                try {
                    AdminDashboardView dashboard = new AdminDashboardView(stage, "Admin");
                    dashboard.show();
                    System.out.println("Admin dashboard should now be visible.");
                } catch (Exception e) {
                    e.printStackTrace();
                    actiontarget.setText("Error loading admin dashboard: " + e.getMessage());
                }
            });
            return;
        }

        try {
            String response = SupabaseClient.authenticateUser(email, password);
            if (response != null && !response.isEmpty()) {
                // Parse user info from response (name,role,department)
                String[] userInfo = response.split(",");
                if (userInfo.length != 3) {
                    actiontarget.setText("Error: Invalid user data received");
                    return;
                }

                String userName = userInfo[0];
                String userRole = userInfo[1];
                String userDepartment = userInfo[2];

                // Ensure UI updates happen on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    try {
                        // Redirect to appropriate dashboard
                        if ("faculty".equalsIgnoreCase(userRole)) {
                            FacultyDashboardView dashboard = new FacultyDashboardView(stage, userName, userRole, userDepartment);
                            dashboard.show();
                        } else if ("student".equalsIgnoreCase(userRole)) {
                            StudentDashboardView dashboard = new StudentDashboardView(stage, userName, userRole, userDepartment);
                            dashboard.show();
                        } else {
                            actiontarget.setText("Error: Unknown user role - " + userRole);
                        }
                    } catch (Exception e) {
                        actiontarget.setText("Error loading dashboard: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                actiontarget.setText("Invalid email or password");
            }
        } catch (IOException ex) {
            actiontarget.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Displays the login window and applies window state settings.
     */
    public void show() {
        stage.setTitle("UniVAL - Login");
        stage.setScene(scene);
        WindowStateManager.applyWindowState(stage);
        stage.show();
    }
}
