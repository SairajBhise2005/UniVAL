package com.unival.facultyscheduling.service;
import com.unival.facultyscheduling.utils.EnvReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * SupabaseClient provides methods to interact with a Supabase backend for user authentication and registration.
 * <p>
 * This class encapsulates the logic for making HTTP requests to a Supabase REST API, including fetching user data,
 * authenticating users by email, and registering new users with relevant details. It utilizes project configuration
 * for the Supabase URL and API key, which should be securely managed and not exposed in version control.
 * <p>
 * Usage of this class assumes that the environment variables SUPABASE_URL and SUPABASE_ANON_KEY are set and accessible
 * through the EnvReader utility.
 * <p>
 * Methods:
 * <ul>
 *     <li>fetchData: Performs a GET request to a specified Supabase endpoint and returns the response as a String.</li>
 *     <li>authenticateUser: Queries the Supabase users table for a user with a specific email.</li>
 *     <li>registerUser: Registers a new user in the Supabase users table with provided details.</li>
 * </ul>
 * <p>
 * Note: Additional methods for POST, PUT, DELETE can be implemented similarly for full CRUD support.
 */
public class SupabaseClient {
    // Replace 'your-supabase-url' below with your actual Supabase project URL.
    // It should look something like 'https://xxxxxxxxxxxx.supabase.co'
    private static final String SUPABASE_URL = EnvReader.get("SUPABASE_URL");
    
    // Replace 'your-supabase-key' below with your actual Supabase API key (anon key).
    // You can find this in your Supabase dashboard under Settings > API.
    // Keep this key secure and do not commit it to version control.
    private static final String SUPABASE_KEY = EnvReader.get("SUPABASE_ANON_KEY");

    /**
     * Performs a GET request to the specified endpoint of the Supabase REST API.
     *
     * @param endpoint The REST endpoint to query (e.g., "/rest/v1/users").
     * @return The response body as a String.
     * @throws Exception If a network or IO error occurs during the request.
     */
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

    /**
     * Authenticates a user by querying the Supabase users table for the provided email address.
     *
     * @param email The email address of the user to authenticate.
     * @return The user data as a JSON String if found, otherwise an empty result.
     * @throws Exception If a network or IO error occurs during the request.
     */
    public static String authenticateUser(String email) throws Exception {
        String endpoint = "/rest/v1/users?email=eq." + email;
        return fetchData(endpoint);
    }

    /**
     * Registers a new user in the Supabase users table with the specified details.
     *
     * @param name       The full name of the user.
     * @param email      The email address of the user.
     * @param role       The role assigned to the user (e.g., student, faculty).
     * @param department The department the user belongs to.
     * @param year       The academic year or relevant year for the user.
     * @return The response from Supabase as a JSON String, including the created user or error details.
     * @throws Exception If a network or IO error occurs during the request.
     */
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
