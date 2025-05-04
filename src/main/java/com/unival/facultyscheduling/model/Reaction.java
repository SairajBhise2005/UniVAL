package com.unival.facultyscheduling.model;

import java.time.LocalDateTime;

public class Reaction {
    public String reactionId;
    public String evaluationId;
    public String userId;
    public String commentId; // nullable, for comment or evaluation
    public String reactionType;
    public LocalDateTime createdAt;
} 