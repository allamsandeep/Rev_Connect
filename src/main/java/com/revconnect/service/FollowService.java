package com.revconnect.service;

import com.revconnect.dao.FollowDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.model.User;

import java.util.List;

public class FollowService {

    private final FollowDAO followDAO = new FollowDAO();
    private final NotificationService notificationService = new NotificationService();
    private final UserDAO userDAO = new UserDAO();

    // ================= FOLLOW USER =================
    public boolean followUser(int followerId, int followedId) {

        // 🚫 cannot follow yourself
        if (followerId == followedId) {
            return false;
        }

        // 🚫 already following
        if (followDAO.isFollowing(followerId, followedId)) {
            return false;
        }

        boolean success = followDAO.followUser(followerId, followedId);

        // 🔔 CREATE NOTIFICATION
        if (success) {
            String followerName = userDAO.getUsernameById(followerId);
            String message = "👤 " + followerName + " started following you";
            notificationService.createNotification(followedId, message);
        }

        return success;
    }

    // ================= UNFOLLOW USER =================
    public boolean unfollowUser(int followerId, int followedId) {
        return followDAO.unfollowUser(followerId, followedId);
    }

    // ================= VIEW FOLLOWERS =================
    public List<User> viewFollowers(int userId) {
        return followDAO.getFollowers(userId);
    }

    // ================= VIEW FOLLOWING =================
    public List<User> viewFollowing(int userId) {
        return followDAO.getFollowing(userId);
    }
}
