package com.unival.facultyscheduling.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.calendarfx.view.CalendarView;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import javafx.scene.control.Tooltip;
import com.unival.facultyscheduling.model.Comment;
import com.unival.facultyscheduling.model.Reaction;
import com.unival.facultyscheduling.service.CommentService;

public class FacultyCalendarView {
    private Stage stage;
    private Scene scene;
    private String userName;
    private String userRole;
    private String userDepartment;
    private BorderPane root;
    private CalendarView calendarView;
    private Calendar evaluationCalendar;
    private VBox commentSection;
    private CommentService commentService = new CommentService();

    public FacultyCalendarView(Stage stage, String userName, String userRole, String userDepartment) {
        this.stage = stage;
        this.userName = userName;
        this.userRole = userRole;
        this.userDepartment = userDepartment;
        initializeCalendarFX();
        setupMockData();
    }

    private void initializeCalendarFX() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 16, 0, 0, 0);");

        // Initialize CalendarFX
        calendarView = new CalendarView();
        
        // Configure calendar settings
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowSourceTrayButton(false);
        calendarView.setShowToolBar(true);
        calendarView.setShowPageToolBarControls(true);
        
        // Set up evaluation calendar
        evaluationCalendar = new Calendar("Faculty Evaluations");
        evaluationCalendar.setStyle(Calendar.Style.STYLE2); // default
        
        // Set up calendar source
        CalendarSource source = new CalendarSource("Evaluation Calendar");
        source.getCalendars().add(evaluationCalendar);
        calendarView.getCalendarSources().add(source);
        
        // Add controls based on user role
        setupControls();
        
        VBox centerBox = new VBox(20);
        centerBox.getChildren().add(calendarView);
        commentSection = createCommentSection("EVAL_ID_PLACEHOLDER", userName); // Replace with real evaluationId and userId
        centerBox.getChildren().add(commentSection);
        root.setCenter(centerBox);
    }
    
    private void setupControls() {
        VBox controls = new VBox(15);
        controls.setPadding(new Insets(15));
        controls.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        // Add role-specific controls
        if ("FACULTY".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole)) {
            Button addButton = new Button("Add Evaluation");
            addButton.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-weight: bold;");
            addButton.setOnAction(e -> showAddEvaluationDialog());
            controls.getChildren().add(addButton);
        }
        
        // Add navigation buttons
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            if (userRole.equalsIgnoreCase("faculty")) {
                FacultyDashboardView dashboard = new FacultyDashboardView(stage, userName, userRole, userDepartment);
                dashboard.show();
            } else {
                StudentDashboardView dashboard = new StudentDashboardView(stage, userName, userRole, userDepartment);
                dashboard.show();
            }
        });
        
        controls.getChildren().addAll(backButton);
        root.setRight(controls);
    }
    
    private TitledPane createLegend() {
        // Legend removed
        return null;
    }
    
    private void showAddEvaluationDialog() {
        Dialog<Entry<String>> dialog = new Dialog<>();
        dialog.setTitle("Add Evaluation");
        dialog.setHeaderText("Enter evaluation details");
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        
        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Enter evaluation title");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        // Disable weekends
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f8d7da;");
                }
            }
        });
        
        // Create time selection with 5-minute intervals
        ComboBox<String> startTime = new ComboBox<>();
        ComboBox<String> endTime = new ComboBox<>();
        for (int hour = 8; hour <= 17; hour++) {
            for (int min = 0; min < 60; min += 5) {
                String time = String.format("%02d:%02d", hour, min);
                startTime.getItems().add(time);
                endTime.getItems().add(time);
            }
        }
        startTime.setValue("09:00");
        endTime.setValue("10:00");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter evaluation description");
        descriptionArea.setPrefRowCount(3);
        
        // Add validation
        startTime.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && endTime.getValue() != null) {
                if (LocalTime.parse(newVal).isAfter(LocalTime.parse(endTime.getValue()))) {
                    endTime.setValue(newVal);
                }
            }
        });
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Start Time:"), 0, 2);
        grid.add(startTime, 1, 2);
        grid.add(new Label("End Time:"), 0, 3);
        grid.add(endTime, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Style the buttons
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #297373; -fx-text-fill: white;");
        
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #FF8552; -fx-text-fill: white;");
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                LocalDate date = datePicker.getValue();
                // Prevent scheduling on weekends
                if (date != null && (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Date");
                    alert.setHeaderText("Weekends Not Allowed");
                    alert.setContentText("You cannot schedule evaluations on Saturdays or Sundays.");
                    alert.showAndWait();
                    return null;
                }
                if (date != null && startTime.getValue() != null && endTime.getValue() != null) {
                    // Check if there are already 2 evaluations on this day
                    int count = countEvaluationsOnDate(date);
                    if (count >= 2) {
                        showMaxEvaluationsAlert();
                        return null;
                    }
                    Entry<String> entry = new Entry<>(titleField.getText());
                    entry.setInterval(
                        LocalDateTime.of(date, LocalTime.parse(startTime.getValue())),
                        LocalDateTime.of(date, LocalTime.parse(endTime.getValue()))
                    );
                    entry.setLocation(userDepartment);
                    entry.setUserObject(descriptionArea.getText());
                    
                    // Check for conflicts
                    if (hasConflict(entry)) {
                        showConflictAlert();
                        return null;
                    }
                    
                    return entry;
                }
            }
            return null;
        });
        
        Optional<Entry<String>> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            evaluationCalendar.addEntry(entry);
            updateCalendarStyles();
        });
    }
    
    private int countEvaluationsOnDate(LocalDate date) {
        int count = 0;
        for (Object obj : evaluationCalendar.findEntries("")) {
            @SuppressWarnings("unchecked")
            Entry<String> entry = (Entry<String>) obj;
            if (entry.getStartDate().equals(date)) {
                count++;
            }
        }
        return count;
    }
    
    private void showMaxEvaluationsAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Maximum Evaluations Reached");
        alert.setHeaderText("Cannot Add More Evaluations");
        alert.setContentText("You cannot add more than two evaluations on the same day.");
        alert.showAndWait();
    }
    
    private boolean hasConflict(Entry<String> newEntry) {
        for (Object obj : evaluationCalendar.findEntries("")) {
            @SuppressWarnings("unchecked")
            Entry<String> existingEntry = (Entry<String>) obj;
            if (newEntry.getStartAsLocalDateTime().isBefore(existingEntry.getEndAsLocalDateTime()) &&
                newEntry.getEndAsLocalDateTime().isAfter(existingEntry.getStartAsLocalDateTime())) {
                return true;
            }
        }
        return false;
    }
    
    private void showConflictAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Scheduling Conflict");
        alert.setHeaderText("Time Slot Conflict");
        alert.setContentText("The selected time slot conflicts with an existing evaluation. Please choose a different time.");
        alert.showAndWait();
    }
    
    private void updateCalendarStyles() {
        // Count evaluations per day
        Map<LocalDate, Integer> evalCount = new HashMap<>();
        for (Object obj : evaluationCalendar.findEntries("")) {
            @SuppressWarnings("unchecked")
            Entry<String> entry = (Entry<String>) obj;
            LocalDate date = entry.getStartDate();
            evalCount.put(date, evalCount.getOrDefault(date, 0) + 1);
        }
        // Update entry titles to reflect the number of evaluations on that day
        for (Object obj : evaluationCalendar.findEntries("")) {
            @SuppressWarnings("unchecked")
            Entry<String> entry = (Entry<String>) obj;
            LocalDate date = entry.getStartDate();
            int count = evalCount.getOrDefault(date, 0);
            String baseTitle = entry.getTitle().replaceAll("\\s*\\(\\d/2\\)$", "");
            entry.setTitle(baseTitle + " (" + count + "/2)");
        }
    }
    
    private void setupMockData() {
        // Add some mock evaluations
        LocalDateTime now = LocalDateTime.now();
        
        Entry<String> entry1 = new Entry<>("Quiz 1");
        entry1.setInterval(
            now.withHour(10).withMinute(0),
            now.withHour(11).withMinute(0)
        );
        entry1.setLocation(userDepartment);
        evaluationCalendar.addEntry(entry1);
        
        Entry<String> entry2 = new Entry<>("Mid Sem");
        entry2.setInterval(
            now.plusDays(2).withHour(14).withMinute(0),
            now.plusDays(2).withHour(16).withMinute(0)
        );
        entry2.setLocation(userDepartment);
        evaluationCalendar.addEntry(entry2);
        
        updateCalendarStyles();
    }

    public BorderPane getRoot() {
        return root;
    }

    public void show() {
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createCommentSection(String evaluationId, String userId) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: #fff; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 0);");
        Label title = new Label("Comments & Reactions");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #297373;");
        section.getChildren().add(title);

        // Reaction bar for evaluation
        HBox evalReactions = createReactionBar(null, evaluationId, userId);
        section.getChildren().add(evalReactions);

        // Comments area
        VBox commentsBox = new VBox(8);
        commentsBox.setPadding(new Insets(10, 0, 10, 0));
        section.getChildren().add(commentsBox);

        // Fetch and display comments asynchronously
        new Thread(() -> {
            try {
                List<Comment> comments = commentService.getCommentsForEvaluation(evaluationId);
                javafx.application.Platform.runLater(() -> {
                    commentsBox.getChildren().clear();
                    if (comments.isEmpty()) {
                        Label placeholder = new Label("No comments yet. Be the first to comment!");
                        commentsBox.getChildren().add(placeholder);
                    } else {
                        for (Comment comment : comments) {
                            commentsBox.getChildren().add(renderComment(comment, userId, evaluationId, 0));
                        }
                    }
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    commentsBox.getChildren().clear();
                    Label error = new Label("Failed to load comments.");
                    commentsBox.getChildren().add(error);
                });
            }
        }).start();

        // Add new comment box
        HBox addBox = new HBox(8);
        TextField commentField = new TextField();
        commentField.setPromptText("Add a comment...");
        Button addBtn = new Button("Post");
        addBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> {
            String text = commentField.getText().trim();
            if (!text.isEmpty()) {
                Comment newComment = new Comment();
                newComment.evaluationId = evaluationId;
                newComment.userId = userId;
                newComment.text = text;
                newComment.parentCommentId = null; // Top-level comment
                new Thread(() -> {
                    try {
                        commentService.addComment(newComment);
                        // Refresh comments after posting
                        List<Comment> comments = commentService.getCommentsForEvaluation(evaluationId);
                        javafx.application.Platform.runLater(() -> {
                            commentsBox.getChildren().clear();
                            commentField.clear();
                            if (comments.isEmpty()) {
                                Label placeholder = new Label("No comments yet. Be the first to comment!");
                                commentsBox.getChildren().add(placeholder);
                            } else {
                                for (Comment comment : comments) {
                                    commentsBox.getChildren().add(renderComment(comment, userId, evaluationId, 0));
                                }
                            }
                        });
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> {
                            Label error = new Label("Failed to post comment.");
                            commentsBox.getChildren().add(error);
                        });
                    }
                }).start();
            }
        });
        addBox.getChildren().addAll(commentField, addBtn);
        section.getChildren().add(addBox);
        return section;
    }

    // Recursive method to render a comment and its replies
    private VBox renderComment(Comment comment, String userId, String evaluationId, int depth) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(0, 0, 0, depth * 24));
        box.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 6;");
        Label author = new Label(comment.userId + " • " + (comment.createdAt != null ? comment.createdAt.toString() : ""));
        author.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        Label text = new Label(comment.text);
        text.setWrapText(true);
        HBox actions = new HBox(8);
        Button replyBtn = new Button("Reply");
        replyBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #297373; -fx-font-size: 11px;");
        replyBtn.setOnAction(e -> {
            // Show reply input and post reply
            TextField replyField = new TextField();
            replyField.setPromptText("Reply...");
            Button postReplyBtn = new Button("Post");
            postReplyBtn.setStyle("-fx-background-color: #297373; -fx-text-fill: white; -fx-font-size: 11px;");
            HBox replyBox = new HBox(6, replyField, postReplyBtn);
            box.getChildren().add(replyBox);
            postReplyBtn.setOnAction(ev -> {
                String replyText = replyField.getText().trim();
                if (!replyText.isEmpty()) {
                    Comment reply = new Comment();
                    reply.evaluationId = evaluationId;
                    reply.userId = userId;
                    reply.text = replyText;
                    reply.parentCommentId = comment.commentId;
                    new Thread(() -> {
                        try {
                            commentService.addComment(reply);
                            // Refresh comments after posting
                            List<Comment> comments = commentService.getCommentsForEvaluation(evaluationId);
                            javafx.application.Platform.runLater(() -> {
                                // Find the root VBox (commentSection) and refresh
                                VBox parentSection = (VBox) box.getParent().getParent();
                                parentSection.getChildren().clear();
                                parentSection.getChildren().add(createCommentSection(evaluationId, userId));
                            });
                        } catch (Exception ex) {
                            javafx.application.Platform.runLater(() -> {
                                Label error = new Label("Failed to post reply.");
                                box.getChildren().add(error);
                            });
                        }
                    }).start();
                }
            });
        });
        actions.getChildren().add(replyBtn);
        // Reaction bar for comment
        HBox commentReactions = createReactionBar(comment.commentId, evaluationId, userId);
        actions.getChildren().add(commentReactions);
        box.getChildren().addAll(author, text, actions);
        // Render replies recursively
        if (comment.replies != null) {
            for (Comment reply : comment.replies) {
                box.getChildren().add(renderComment(reply, userId, evaluationId, depth + 1));
            }
        }
        return box;
    }

    private HBox createReactionBar(String commentId, String evaluationId, String userId) {
        HBox bar = new HBox(6);
        bar.setAlignment(Pos.CENTER_LEFT);
        String[] emojis = {"✅", "😟", "🔁", "👍", "👎", "❤️"};
        Map<String, Integer> reactionCounts = new HashMap<>();
        // Fetch reaction counts asynchronously
        new Thread(() -> {
            try {
                List<Reaction> reactions;
                if (commentId == null) {
                    reactions = commentService.getReactionsForEvaluation(evaluationId);
                } else {
                    reactions = commentService.getReactionsForComment(commentId);
                }
                for (String emoji : emojis) {
                    int count = (int) reactions.stream().filter(r -> emoji.equals(r.reactionType)).count();
                    reactionCounts.put(emoji, count);
                }
                javafx.application.Platform.runLater(() -> {
                    bar.getChildren().clear();
                    for (String emoji : emojis) {
                        int count = reactionCounts.getOrDefault(emoji, 0);
                        Button btn = new Button(emoji + " " + count);
                        btn.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 5; -fx-background-radius: 5;");
                        btn.setOnAction(e -> {
                            // Post reaction and refresh counts
                            Reaction reaction = new Reaction();
                            reaction.evaluationId = evaluationId;
                            reaction.userId = userId;
                            reaction.commentId = commentId;
                            reaction.reactionType = emoji;
                            new Thread(() -> {
                                try {
                                    commentService.addReaction(reaction);
                                    // Refresh reaction counts
                                    List<Reaction> updatedReactions;
                                    if (commentId == null) {
                                        updatedReactions = commentService.getReactionsForEvaluation(evaluationId);
                                    } else {
                                        updatedReactions = commentService.getReactionsForComment(commentId);
                                    }
                                    Map<String, Integer> updatedCounts = new HashMap<>();
                                    for (String em : emojis) {
                                        int c = (int) updatedReactions.stream().filter(r -> em.equals(r.reactionType)).count();
                                        updatedCounts.put(em, c);
                                    }
                                    javafx.application.Platform.runLater(() -> {
                                        bar.getChildren().clear();
                                        for (String em : emojis) {
                                            int c = updatedCounts.getOrDefault(em, 0);
                                            Button b = new Button(em + " " + c);
                                            b.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 5; -fx-background-radius: 5;");
                                            b.setOnAction(btn.getOnAction()); // reuse handler
                                            bar.getChildren().add(b);
                                        }
                                    });
                                } catch (Exception ex) {
                                    // Optionally show error
                                }
                            }).start();
                        });
                        bar.getChildren().add(btn);
                    }
                });
            } catch (Exception ex) {
                // Optionally show error
            }
        }).start();
        return bar;
    }
}
