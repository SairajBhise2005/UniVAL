package com.unival.facultyscheduling.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * EnvReader is a utility class for loading and retrieving environment variables from a .env file.
 * <p>
 * This class reads key-value pairs from a .env file located at the root of the project directory and stores them
 * in memory for fast retrieval. If the .env file is not found or cannot be read, EnvReader falls back to hardcoded
 * default values for critical variables such as SUPABASE_URL and SUPABASE_ANON_KEY.
 * <p>
 * Usage:
 * <ul>
 *     <li>Call EnvReader.get("KEY") to retrieve the value of an environment variable.</li>
 *     <li>The environment variables are loaded once on first access and cached for subsequent calls.</li>
 * </ul>
 * <p>
 * Methods:
 * <ul>
 *     <li>get: Returns the value of the specified environment variable key.</li>
 *     <li>loadEnv: Loads all environment variables from the .env file into memory.</li>
 * </ul>
 * <p>
 * Note: The .env file should contain lines in the format KEY=VALUE. Lines starting with '#' are treated as comments.
 */
public class EnvReader {
    private static final String ENV_FILE_PATH = ".env";
    private static Map<String, String> envVariables = null;

    /**
     * Retrieves the value of the specified environment variable key.
     * If the variables have not been loaded yet, it loads them from the .env file.
     *
     * @param key The name of the environment variable to retrieve.
     * @return The value of the environment variable, or null if not found.
     */
    public static String get(String key) {
        if (envVariables == null) {
            loadEnv();
        }
        return envVariables.get(key);
    }

    /**
     * Loads environment variables from the .env file into memory.
     * If the file cannot be read, sets fallback default values for SUPABASE_URL and SUPABASE_ANON_KEY.
     */
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
