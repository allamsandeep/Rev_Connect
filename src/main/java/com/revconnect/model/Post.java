package com.revconnect.model;

import java.sql.Timestamp;
import java.util.Date;


public class Post {

    private int postId;
    private int userId;
    private String username;
    // Required for JDBC
    public Post() {}

    // Constructor for creating post
    public Post(int userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    private String content;
    private Timestamp createdAt;
    // 🔥 NEW BUSINESS / ADVANCED FIELDS
    private String postType;   // NORMAL / PROMOTIONAL / SHARED
    // 🔥 NEW BUSINESS / ADVANCED FIELDS
    private boolean pinned;     // true / false
    private Integer originalPostId;   // null = original post
    private String ctaText;
    private String ctaLink;

    // ===== Getters & Setters =====

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 🔥 NEW FIELDS

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public Integer getOriginalPostId() {
        return originalPostId;
    }

    public void setOriginalPostId(Integer originalPostId) {
        this.originalPostId = originalPostId;
    }
    public String getCtaText() {
        return ctaText;
    }

    public void setCtaText(String ctaText) {
        this.ctaText = ctaText;
    }

    public String getCtaLink() {
        return ctaLink;
    }

    public void setCtaLink(String ctaLink) {
        this.ctaLink = ctaLink;
    }

    @Override
    public String toString() {
        return "Post ID: " + postId +
                "\nUser: " + username +
                "\nContent: " + content +
                "\nCreated At: " + createdAt;
    }
}
