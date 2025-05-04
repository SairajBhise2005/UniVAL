package com.unival.facultyscheduling.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration manager for the application.
 * Loads sensitive credentials from environment variables or a local properties file.
 */
public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    
    private static Properties properties;
    
    static {
        properties = new Properties();
        loadConfiguration();
    }
    
    /**
     * Loads configuration from environment variables or a local properties file.
     * Environment variables take precedence over the properties file.
     */
    private static void loadConfiguration() {
        // Try to load from environment variables first
        String supabaseUrl = System.getenv("SUPABASE_URL");
        String supabaseKey = System.getenv("SUPABASE_KEY");
        
        // If environment variables are not set, try to load from properties file
        if (supabaseUrl == null || supabaseKey == null) {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                properties.load(fis);
                LOGGER.info("Configuration loaded from properties file");
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not load configuration file: " + e.getMessage());
            }
        }
        
        // Set properties from environment variables or properties file
        if (supabaseUrl != null) {
            properties.setProperty("supabase.url", supabaseUrl);
        } else {
            supabaseUrl = properties.getProperty("supabase.url");
        }
        
        if (supabaseKey != null) {
            properties.setProperty("supabase.key", supabaseKey);
        } else {
            supabaseKey = properties.getProperty("supabase.key");
        }
        
        // Validate configuration
        if (supabaseUrl == null || supabaseKey == null) {
            LOGGER.severe("Supabase credentials not found. Please set SUPABASE_URL and SUPABASE_KEY environment variables or create a config.properties file.");
        }
    }
    
    /**
     * Gets the Supabase URL from configuration.
     * @return The Supabase URL
     */
    public static String getSupabaseUrl() {
        return properties.getProperty("supabase.url");
    }
    
    /**
     * Gets the Supabase API key from configuration.
     * @return The Supabase API key
     */
    public static String getSupabaseKey() {
        return properties.getProperty("supabase.key");
    }
} 