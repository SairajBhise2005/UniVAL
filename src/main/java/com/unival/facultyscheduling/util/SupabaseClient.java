package com.unival.facultyscheduling.util;

import okhttp3.OkHttpClient;

public class SupabaseClient {
    private static SupabaseClient instance;
    private final OkHttpClient httpClient;
    private final String supabaseUrl;
    private final String supabaseKey;

    private SupabaseClient() {
        supabaseUrl = System.getenv("SUPABASE_URL");
        supabaseKey = System.getenv("SUPABASE_KEY");

        if (supabaseUrl == null || supabaseKey == null) {
            throw new RuntimeException("Supabase credentials not found in environment variables. Please set SUPABASE_URL and SUPABASE_KEY.");
        }

        this.httpClient = new OkHttpClient();
    }

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getSupabaseKey() {
        return supabaseKey;
    }
}