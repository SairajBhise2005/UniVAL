package com.unival.facultyscheduling.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class EnvReader {
    private static final String ENV_FILE_PATH = ".env";
    private static Map<String, String> envVariables = null;

    public static String get(String key) {
        if (envVariables == null) {
            loadEnv();
        }
        return envVariables.get(key);
    }

    private static void loadEnv() {
        envVariables = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comments
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Split on first '=' character
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envVariables.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading .env file: " + e.getMessage());
            // Fallback to hardcoded values if file not found
            envVariables.put("SUPABASE_URL", "https://dhlympsnzcmutwrilmzx.supabase.co");
            envVariables.put("SUPABASE_ANON_KEY", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRobHltcHNuemNtdXR3cmlsbXp4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU0NjcxNjEsImV4cCI6MjA2MTA0MzE2MX0.1Pbb1DyqSlaSn9gayH-A8R2yAd469jAyL4qz-As5QAs");
        }
    }
}
