package com.revconnect.service;

import com.revconnect.dao.UserDAO;
import com.revconnect.model.User;

import java.util.List;

public class UserService {

    private UserDAO dao = new UserDAO();

    public boolean registerUser(User user) {
        return dao.insertUser(user);
    }

    public User login(String u, String p) {
        return dao.login(u, p);
    }

    public boolean resetPassword(String u, String a, String p) {
        return dao.resetPassword(u, a, p);
    }

    public List<User> searchUsers(String k) {
        return dao.searchUsers(k);
    }
    public String getSecurityQuestion(String username) {
        return dao.getSecurityQuestion(username);
    }
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        return dao.changePassword(userId, currentPassword, newPassword);
    }
}
