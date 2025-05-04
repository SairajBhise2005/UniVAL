package com.unival.facultyscheduling.service;
import com.unival.facultyscheduling.utils.EnvReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupabaseClient {
    // Replace 'your-supabase-url' below with your actual Supabase project URL.
    // It should look something like 'https://xxxxxxxxxxxx.supabase.co'
    private static final String SUPABASE_URL = EnvReader.get("SUPABASE_URL");
    
    // Replace 'your-supabase-key' below with your actual Supabase API key (anon key).
    // You can find this in your Supabase dashboard under Settings > API.
    // Keep this key secure and do not commit it to version control.
    private static final String SUPABASE_KEY = EnvReader.get("SUPABASE_ANON_KEY");

    public static String fetchData(String endpoint) throws Exception {
        URL url = new URL(SUPABASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
        conn.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();
        return content.toString();
    }

    public static String authenticateUser(String email) throws Exception {
        String endpoint = "/rest/v1/users?email=eq." + email;
        return fetchData(endpoint);
    }

    public static String registerUser(String name, String email, String role, String department, int year) throws Exception {
        URL url = new URL(SUPABASE_URL + "/rest/v1/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Prefer", "return=representation");
        conn.setDoOutput(true);
    
        String jsonInputString = "{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"role\":\"" + role + "\",\"department\":\"" + department + "\",\"year\":" + year + "}";
    
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } catch (Exception e) {
            // If there's an error, read the error stream for more details
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        } finally {
            conn.disconnect();
        }
        return content.toString();
    }
    // Additional methods for POST, PUT, DELETE would be implemented similarly
}
