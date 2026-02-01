package com.revconnect.service;

import com.revconnect.dao.NotificationDAO;
import com.revconnect.dao.UserDAO;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    // ================= CREATE NOTIFICATION =================
    public void createNotification(int userId, String message) {
        notificationDAO.createNotification(userId, message);
    }

    // ================= UNREAD COUNT =================
    public int getUnreadCount(int userId) {
        return notificationDAO.getUnreadCount(userId);
    }

    // ================= UNREAD NOTIFICATIONS =================
    public List<String> getUnreadNotifications(int userId) {
        return notificationDAO.getUnreadNotifications(userId);
    }

    // ================= ALL NOTIFICATIONS =================
    public List<String> getAllNotifications(int userId) {
        return notificationDAO.getAllNotifications(userId);
    }

    // ================= MARK ALL READ =================
    public void markAllAsRead(int userId) {
        notificationDAO.markAllAsRead(userId);
    }

    // ================= HELPER =================
    public String getUsername(int userId) {
        return userDAO.getUsernameById(userId);
    }
}
