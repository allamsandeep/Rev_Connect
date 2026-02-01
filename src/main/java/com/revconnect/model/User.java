package com.revconnect.model;

import java.util.Date;

public class User {

    private int userId;
    private String email;
    private String username;
    private String password;
    private UserType userType;

    private Date createdAt;
    private String securityQuestion;
    private String securityAnswer;

    public User() {}

    public User(String email, String username, String password,
                UserType userType, String securityQuestion, String securityAnswer) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.createdAt = new Date();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
