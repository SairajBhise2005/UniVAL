package com.unival.facultyscheduling.util;

import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.application.Platform;

public class WindowStateManager {
    private static boolean isMaximized = false;
    private static double width = 800;
    private static double height = 600;
    private static double x = 0;
    private static double y = 0;

    public static void saveWindowState(Window window) {
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            isMaximized = stage.isMaximized();
            if (!isMaximized) {
                width = stage.getWidth();
                height = stage.getHeight();
                x = stage.getX();
                y = stage.getY();
            }
        }
    }

    public static void applyWindowState(Window window) {
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            Platform.runLater(() -> {
                stage.setMaximized(isMaximized);
                if (!isMaximized) {
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.setX(x);
                    stage.setY(y);
                }
            });
        }
    }

    public static void initializeWindowState(Stage stage) {
        // Set initial minimum dimensions
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Save initial state
        if (stage.isMaximized()) {
            isMaximized = true;
        } else {
            width = stage.getWidth();
            height = stage.getHeight();
            x = stage.getX();
            y = stage.getY();
        }

        // Add listener for window state changes
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            isMaximized = newVal;
            if (!newVal) {
                width = stage.getWidth();
                height = stage.getHeight();
                x = stage.getX();
                y = stage.getY();
            }
        });

        // Add listener for window position and size changes
        stage.xProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.isMaximized()) {
                x = newVal.doubleValue();
            }
        });

        stage.yProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.isMaximized()) {
                y = newVal.doubleValue();
            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.isMaximized()) {
                width = newVal.doubleValue();
            }
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.isMaximized()) {
                height = newVal.doubleValue();
            }
        });
    }

    public static boolean isMaximized() {
        return isMaximized;
    }

    public static boolean isMinimized() {
        return !isMaximized;
    }
} 