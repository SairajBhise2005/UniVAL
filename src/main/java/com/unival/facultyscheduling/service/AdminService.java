package com.unival.facultyscheduling.service;

import com.unival.facultyscheduling.util.SupabaseClient;
import org.json.JSONObject;
import org.json.JSONArray;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.IOException;

public class AdminService {
    private final SupabaseClient supabaseClient;
    private static final String ADMIN_ROLE = "admin";

    public AdminService() {
        this.supabaseClient = SupabaseClient.getInstance();
    }

    public boolean isUserAdmin(String userId) {
        try {
            Request request = new Request.Builder()
                .url(supabaseClient.getSupabaseUrl() + "/rest/v1/users?id=eq." + userId)
                .header("apikey", supabaseClient.getSupabaseKey())
                .header("Authorization", "Bearer " + supabaseClient.getSupabaseKey())
                .build();

            try (Response response = supabaseClient.getHttpClient().newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JSONArray data = new JSONArray(response.body().string());
                    if (data.length() > 0) {
                        JSONObject user = data.getJSONObject(0);
                        return ADMIN_ROLE.equals(user.getString("role"));
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JSONArray getAllUsers() {
        try {
            Request request = new Request.Builder()
                .url(supabaseClient.getSupabaseUrl() + "/rest/v1/users")
                .header("apikey", supabaseClient.getSupabaseKey())
                .header("Authorization", "Bearer " + supabaseClient.getSupabaseKey())
                .build();

            try (Response response = supabaseClient.getHttpClient().newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return new JSONArray(response.body().string());
                }
            }
            return new JSONArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public boolean updateUserRole(String userId, String newRole) {
        try {
            JSONObject updateData = new JSONObject();
            updateData.put("role", newRole);

            Request request = new Request.Builder()
                .url(supabaseClient.getSupabaseUrl() + "/rest/v1/users?id=eq." + userId)
                .header("apikey", supabaseClient.getSupabaseKey())
                .header("Authorization", "Bearer " + supabaseClient.getSupabaseKey())
                .header("Content-Type", "application/json")
                .patch(RequestBody.create(updateData.toString().getBytes(), MediaType.parse("application/json")))
                .build();

            try (Response response = supabaseClient.getHttpClient().newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JSONArray getFacultyList() {
        try {
            Request request = new Request.Builder()
                .url(supabaseClient.getSupabaseUrl() + "/rest/v1/faculty")
                .header("apikey", supabaseClient.getSupabaseKey())
                .header("Authorization", "Bearer " + supabaseClient.getSupabaseKey())
                .build();

            try (Response response = supabaseClient.getHttpClient().newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return new JSONArray(response.body().string());
                }
            }
            return new JSONArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public JSONArray getSchedules() {
        try {
            Request request = new Request.Builder()
                .url(supabaseClient.getSupabaseUrl() + "/rest/v1/schedule")
                .header("apikey", supabaseClient.getSupabaseKey())
                .header("Authorization", "Bearer " + supabaseClient.getSupabaseKey())
                .build();

            try (Response response = supabaseClient.getHttpClient().newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return new JSONArray(response.body().string());
                }
            }
            return new JSONArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public boolean deleteUser(String userId) {
        try {
            Request request = new Request.Builder()
                .url(supabaseClient.getSupabaseUrl() + "/rest/v1/users?id=eq." + userId)
                .header("apikey", supabaseClient.getSupabaseKey())
                .header("Authorization", "Bearer " + supabaseClient.getSupabaseKey())
                .delete()
                .build();

            try (Response response = supabaseClient.getHttpClient().newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
} 