package com.unival.facultyscheduling.model;

import java.time.LocalDateTime;
import java.util.List;

public class Comment {
    public String commentId;
    public String evaluationId;
    public String userId;
    public String parentCommentId;
    public String text;
    public boolean isEdited;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public List<Comment> replies;
    public List<Reaction> reactions;
} 