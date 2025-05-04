package com.unival.facultyscheduling.service;

import com.unival.facultyscheduling.config.AppConfig;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SupabaseClient {
    private static final Logger LOGGER = Logger.getLogger(SupabaseClient.class.getName());
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final SecureRandom secureRandom = new SecureRandom();

    private static String hashPassword(String password, String salt) {
        try {
            LOGGER.info("Hashing password with salt: " + salt);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = password + salt;
            LOGGER.info("Input string to hash: " + input);
            byte[] hash = digest.digest(input.getBytes());
            String result = Base64.getEncoder().encodeToString(hash);
            LOGGER.info("Generated hash: " + result);
            return result;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error hashing password", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String fetchData(String endpoint) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            String fullUrl = supabaseUrl + endpoint;
            LOGGER.info("Fetching data from: " + fullUrl);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Response status: " + response.statusCode());
            LOGGER.info("Response body: " + response.body());
            
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    public static String registerUser(String name, String email, String password, String role, String department, int year) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }

            LOGGER.info("=== Starting Registration Process ===");
            
            // Generate salt and hash password
            String salt = generateSalt();
            LOGGER.info("Generated salt: " + salt);
            
            String hashedPassword = hashPassword(password, salt);
            LOGGER.info("Generated hash: " + hashedPassword);
            
            String passwordToStore = hashedPassword + salt;
            LOGGER.info("Final password to store: " + passwordToStore);

            // First, get all departments and find the matching one
            LOGGER.info("Looking up department: " + department);
            
            // Construct the URL for department lookup
            String encodedDepartment = URLEncoder.encode(department, "UTF-8");
            String departmentEndpoint = String.format("/rest/v1/departments?name=eq.%s", encodedDepartment);
            LOGGER.info("Department endpoint: " + departmentEndpoint);
            
            HttpRequest deptRequest = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + departmentEndpoint))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> deptResponse = client.send(deptRequest, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Department lookup response code: " + deptResponse.statusCode());
            LOGGER.info("Department lookup response: " + deptResponse.body());

            if (deptResponse.body().equals("[]")) {
                // If exact match fails, try getting all departments to log what's available
                String allDepts = fetchData("/rest/v1/departments");
                LOGGER.info("Available departments: " + allDepts);
                throw new IOException("Department not found: " + department);
            }

            // Extract department ID from response
            Pattern deptPattern = Pattern.compile("\"department_id\":\"([^\"]+)\"");
            Matcher deptMatcher = deptPattern.matcher(deptResponse.body());
            
            String departmentId;
            if (deptMatcher.find()) {
                departmentId = deptMatcher.group(1);
                LOGGER.info("Found department ID: " + departmentId);
            } else {
                LOGGER.warning("Could not extract department ID from response: " + deptResponse.body());
                throw new IOException("Could not extract department ID from response");
            }

            // Create user record
            String userJson = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"department_id\":\"%s\",\"year\":%d}",
                name, email, passwordToStore, role, departmentId, year
            );

            HttpRequest userRequest = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/users"))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(userJson))
                    .build();

            HttpResponse<String> userResponse = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("User creation response code: " + userResponse.statusCode());
            LOGGER.info("User creation response: " + userResponse.body());

            if (userResponse.statusCode() != 201) {
                throw new IOException("Failed to create user: " + userResponse.body());
            }

            return userResponse.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    public static String authenticateUser(String email, String password) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                LOGGER.severe("Supabase credentials not configured");
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }

            LOGGER.info("=== Starting Authentication Process ===");
            LOGGER.info("Attempting to authenticate user: " + email);
            
            // Get user record
            String encodedEmail = URLEncoder.encode(email, "UTF-8");
            String endpoint = "/rest/v1/users?email=eq." + encodedEmail;
            LOGGER.info("Fetching user with endpoint: " + endpoint);
            
            HttpRequest userRequest = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + endpoint))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("User lookup response code: " + response.statusCode());
            LOGGER.info("User lookup response: " + response.body());

            if (response.statusCode() != 200 || response.body().equals("[]")) {
                LOGGER.warning("User not found or invalid response");
                return null;
            }

            // Extract user information
            Pattern userPattern = Pattern.compile("\"name\":\"([^\"]+)\",.*\"role\":\"([^\"]+)\",.*\"department_id\":\"([^\"]+)\"");
            Matcher userMatcher = userPattern.matcher(response.body());

            if (!userMatcher.find()) {
                LOGGER.warning("Could not extract user information from response");
                return null;
            }

            String storedName = userMatcher.group(1);
            String storedRole = userMatcher.group(2);
            String departmentId = userMatcher.group(3);

            // Extract stored password and salt
            Pattern passwordPattern = Pattern.compile("\"password\":\"([^\"]+)\"");
            Matcher passwordMatcher = passwordPattern.matcher(response.body());

            if (!passwordMatcher.find()) {
                LOGGER.warning("Could not extract password from response");
                return null;
            }

            String storedPasswordAndSalt = passwordMatcher.group(1);
            
            // Split stored password into hash and salt
            String storedHash = storedPasswordAndSalt.substring(0, 44); // Base64 SHA-256 is 44 characters
            String storedSalt = storedPasswordAndSalt.substring(44);

            // Hash the provided password with the stored salt
            String hashedPassword = hashPassword(password, storedSalt);

            // Compare the hashes
            if (!hashedPassword.equals(storedHash)) {
                LOGGER.warning("Password mismatch");
                return null;
            }

            // Get department name
            String deptResponse = fetchData("/rest/v1/departments?department_id=eq." + departmentId);
            Pattern deptNamePattern = Pattern.compile("\"name\":\"([^\"]+)\"");
            Matcher deptNameMatcher = deptNamePattern.matcher(deptResponse);

            String departmentName = deptNameMatcher.find() ? deptNameMatcher.group(1) : "Unknown Department";

            // Return user information as comma-separated string
            String userInfo = String.format("%s,%s,%s", storedName, storedRole, departmentName);
            LOGGER.info("Authentication successful. User info: " + userInfo);
            return userInfo;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    private static String extractUserId(String jsonResponse) {
        Pattern pattern = Pattern.compile("\"user\":\\{\"id\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonResponse);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Register a faculty member with additional faculty-specific information
     */
    public static String registerFaculty(String name, String email, String password, String departmentId, 
                                        String specialization, String officeLocation, String officeHours,
                                        String qualification, Integer experienceYears, String researchInterests) throws IOException {
        // First register the user
        String userResponse = registerUser(name, email, password, "faculty", departmentId, 0);
        
        try {
            // Extract user_id from the response using regex
            String userId = extractUserId(userResponse);
            if (userId != null) {
                // Update the faculty record with additional information
                return updateFacultyInfo(userId, specialization, officeLocation, officeHours, 
                                       qualification, experienceYears, researchInterests);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing user response: " + e.getMessage());
        }
        
        return userResponse;
    }
    
    /**
     * Register a student with additional student-specific information
     */
    public static String registerStudent(String name, String email, String password, String departmentId, 
                                        int year, String enrollmentNumber, String major, String minor,
                                        Double gpa, String expectedGraduationDate, String advisorId) throws IOException {
        // First register the user
        String userResponse = registerUser(name, email, password, "student", departmentId, year);
        
        try {
            // Extract user_id from the response using regex
            String userId = extractUserId(userResponse);
            if (userId != null) {
                // Update the student record with additional information
                return updateStudentInfo(userId, enrollmentNumber, major, minor, gpa, 
                                       expectedGraduationDate, advisorId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing user response: " + e.getMessage());
        }
        
        return userResponse;
    }
    
    /**
     * Update faculty information
     */
    private static String updateFacultyInfo(String facultyId, String specialization, String officeLocation, 
                                          String officeHours, String qualification, Integer experienceYears, 
                                          String researchInterests) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"specialization\":\"").append(specialization).append("\",");
            jsonBuilder.append("\"office_location\":\"").append(officeLocation).append("\",");
            jsonBuilder.append("\"office_hours\":\"").append(officeHours).append("\",");
            jsonBuilder.append("\"qualification\":\"").append(qualification).append("\",");
            jsonBuilder.append("\"experience_years\":").append(experienceYears).append(",");
            jsonBuilder.append("\"research_interests\":\"").append(researchInterests).append("\"");
            jsonBuilder.append("}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/faculty?faculty_id=eq." + facultyId))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }
    
    /**
     * Update student information
     */
    private static String updateStudentInfo(String studentId, String enrollmentNumber, String major, String minor,
                                          Double gpa, String expectedGraduationDate, String advisorId) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"enrollment_number\":\"").append(enrollmentNumber).append("\",");
            jsonBuilder.append("\"major\":\"").append(major).append("\",");
            jsonBuilder.append("\"minor\":\"").append(minor).append("\",");
            jsonBuilder.append("\"gpa\":").append(gpa).append(",");
            jsonBuilder.append("\"expected_graduation_date\":\"").append(expectedGraduationDate).append("\",");
            jsonBuilder.append("\"advisor_id\":\"").append(advisorId).append("\"");
            jsonBuilder.append("}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/students?student_id=eq." + studentId))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    // Department methods
    public static String getAllDepartments() throws IOException {
        String endpoint = "/rest/v1/departments";
        return fetchData(endpoint);
    }

    public static String getDepartmentById(String departmentId) throws IOException {
        String endpoint = "/rest/v1/departments?department_id=eq." + departmentId;
        return fetchData(endpoint);
    }

    // Course methods
    public static String getAllCourses() throws IOException {
        String endpoint = "/rest/v1/courses";
        return fetchData(endpoint);
    }

    public static String getCourseById(String courseId) throws IOException {
        String endpoint = "/rest/v1/courses?course_id=eq." + courseId;
        return fetchData(endpoint);
    }

    public static String getCoursesByDepartment(String departmentId) throws IOException {
        String endpoint = "/rest/v1/courses?department_id=eq." + departmentId;
        return fetchData(endpoint);
    }

    // Room methods
    public static String getAllRooms() throws IOException {
        String endpoint = "/rest/v1/rooms";
        return fetchData(endpoint);
    }

    public static String getRoomById(String roomId) throws IOException {
        String endpoint = "/rest/v1/rooms?room_id=eq." + roomId;
        return fetchData(endpoint);
    }

    public static String getAvailableRooms() throws IOException {
        String endpoint = "/rest/v1/rooms?is_available=eq.true";
        return fetchData(endpoint);
    }

    // Time slot methods
    public static String getAllTimeSlots() throws IOException {
        String endpoint = "/rest/v1/time_slots";
        return fetchData(endpoint);
    }

    public static String getTimeSlotsByDay(String dayOfWeek) throws IOException {
        String endpoint = "/rest/v1/time_slots?day_of_week=eq." + dayOfWeek;
        return fetchData(endpoint);
    }

    // Cohort methods
    public static String getAllCohorts() throws IOException {
        String endpoint = "/rest/v1/cohorts";
        return fetchData(endpoint);
    }

    public static String getCohortById(String cohortId) throws IOException {
        String endpoint = "/rest/v1/cohorts?cohort_id=eq." + cohortId;
        return fetchData(endpoint);
    }

    public static String getCohortsByDepartment(String departmentId) throws IOException {
        String endpoint = "/rest/v1/cohorts?department_id=eq." + departmentId;
        return fetchData(endpoint);
    }

    // Schedule methods
    public static String getAllSchedules() throws IOException {
        String endpoint = "/rest/v1/schedules";
        return fetchData(endpoint);
    }

    public static String getScheduleById(String scheduleId) throws IOException {
        String endpoint = "/rest/v1/schedules?schedule_id=eq." + scheduleId;
        return fetchData(endpoint);
    }

    public static String getSchedulesByFaculty(String facultyId) throws IOException {
        String endpoint = "/rest/v1/schedules?faculty_id=eq." + facultyId;
        return fetchData(endpoint);
    }

    public static String getSchedulesByCohort(String cohortId) throws IOException {
        String endpoint = "/rest/v1/schedules?cohort_id=eq." + cohortId;
        return fetchData(endpoint);
    }

    public static String createSchedule(String courseId, String facultyId, String cohortId, 
                                      String roomId, String slotId, String semester, 
                                      String academicYear) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"course_id\":\"").append(courseId).append("\",");
            jsonBuilder.append("\"faculty_id\":\"").append(facultyId).append("\",");
            jsonBuilder.append("\"cohort_id\":\"").append(cohortId).append("\",");
            jsonBuilder.append("\"room_id\":\"").append(roomId).append("\",");
            jsonBuilder.append("\"slot_id\":\"").append(slotId).append("\",");
            jsonBuilder.append("\"semester\":\"").append(semester).append("\",");
            jsonBuilder.append("\"academic_year\":\"").append(academicYear).append("\",");
            jsonBuilder.append("\"is_active\":true");
            jsonBuilder.append("}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/schedules"))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    // Evaluation methods
    public static String createEvaluation(String title, String description, String subject, String type,
                                        String date, String startTime, String endTime, String courseId,
                                        String facultyId, String roomId, String createdBy) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"title\":\"").append(title).append("\",");
            jsonBuilder.append("\"description\":\"").append(description).append("\",");
            jsonBuilder.append("\"subject\":\"").append(subject).append("\",");
            jsonBuilder.append("\"type\":\"").append(type).append("\",");
            jsonBuilder.append("\"date\":\"").append(date).append("\",");
            jsonBuilder.append("\"start_time\":\"").append(startTime).append("\",");
            jsonBuilder.append("\"end_time\":\"").append(endTime).append("\",");
            jsonBuilder.append("\"course_id\":\"").append(courseId).append("\",");
            jsonBuilder.append("\"faculty_id\":\"").append(facultyId).append("\",");
            jsonBuilder.append("\"room_id\":\"").append(roomId).append("\",");
            jsonBuilder.append("\"created_by\":\"").append(createdBy).append("\",");
            jsonBuilder.append("\"is_published\":false");
            jsonBuilder.append("}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/evaluations"))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    public static String getEvaluationsByFaculty(String facultyId) throws IOException {
        String endpoint = "/rest/v1/evaluations?faculty_id=eq." + facultyId;
        return fetchData(endpoint);
    }

    public static String getEvaluationsByCourse(String courseId) throws IOException {
        String endpoint = "/rest/v1/evaluations?course_id=eq." + courseId;
        return fetchData(endpoint);
    }

    // Comment methods
    public static String addComment(String evaluationId, String userId, String text, String parentCommentId) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"evaluation_id\":\"").append(evaluationId).append("\",");
            jsonBuilder.append("\"user_id\":\"").append(userId).append("\",");
            jsonBuilder.append("\"text\":\"").append(text).append("\"");
            
            if (parentCommentId != null) {
                jsonBuilder.append(",\"parent_comment_id\":\"").append(parentCommentId).append("\"");
            }
            
            jsonBuilder.append("}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/comments"))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    public static String getCommentsByEvaluation(String evaluationId) throws IOException {
        String endpoint = "/rest/v1/comments?evaluation_id=eq." + evaluationId;
        return fetchData(endpoint);
    }

    // Reaction methods
    public static String addReaction(String evaluationId, String userId, String reactionType) throws IOException {
        try {
            String supabaseUrl = AppConfig.getSupabaseUrl();
            String supabaseKey = AppConfig.getSupabaseKey();
            
            if (supabaseUrl == null || supabaseKey == null) {
                throw new IOException("Supabase credentials not configured. Please check your configuration.");
            }
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"evaluation_id\":\"").append(evaluationId).append("\",");
            jsonBuilder.append("\"user_id\":\"").append(userId).append("\",");
            jsonBuilder.append("\"reaction_type\":\"").append(reactionType).append("\"");
            jsonBuilder.append("}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/reactions"))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    public static String getReactionsByEvaluation(String evaluationId) throws IOException {
        String endpoint = "/rest/v1/reactions?evaluation_id=eq." + evaluationId;
        return fetchData(endpoint);
    }

    /**
     * Get all faculty members
     */
    public static String getAllFaculty() throws IOException {
        String endpoint = "/rest/v1/faculty";
        return fetchData(endpoint);
    }
}
