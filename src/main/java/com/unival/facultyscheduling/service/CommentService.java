package com.unival.facultyscheduling.service;

import com.unival.facultyscheduling.model.Comment;
import com.unival.facultyscheduling.model.Reaction;
import okhttp3.*;
import org.json.*;
import java.io.IOException;
import java.util.*;

public class CommentService {
    private static final String SUPABASE_URL = "<YOUR_SUPABASE_URL>";
    private static final String SUPABASE_KEY = "<YOUR_SUPABASE_KEY>";
    private static final OkHttpClient client = new OkHttpClient();

    public List<Comment> getCommentsForEvaluation(String evaluationId) throws IOException {
        // TODO: Implement GET /comments?evaluation_id=eq.{evaluationId}&order=created_at
        return new ArrayList<>();
    }

    public void addComment(Comment comment) throws IOException {
        // TODO: Implement POST /comments
    }

    public List<Reaction> getReactionsForEvaluation(String evaluationId) throws IOException {
        // TODO: Implement GET /reactions?evaluation_id=eq.{evaluationId}&comment_id=is.null
        return new ArrayList<>();
    }

    public List<Reaction> getReactionsForComment(String commentId) throws IOException {
        // TODO: Implement GET /reactions?comment_id=eq.{commentId}
        return new ArrayList<>();
    }

    public void addReaction(Reaction reaction) throws IOException {
        // TODO: Implement POST /reactions
    }
} 