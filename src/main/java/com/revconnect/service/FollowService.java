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

        // 🚫 Cannot follow yourself
        if (followerId == followedId) {
            System.out.println("❌ You cannot follow yourself");
            return false;
        }

        // 🚫 User does not exist
        if (!userDAO.userExists(followedId)) {
            System.out.println("❌ User does not exist");
            return false;
        }

        // 🚫 Already following
        if (followDAO.isFollowing(followerId, followedId)) {
            System.out.println("⚠ You are already following this user");
            return false;
        }

        // ✅ Follow
        boolean success = followDAO.followUser(followerId, followedId);

        if (success) {
            String followerName = userDAO.getUsernameById(followerId);
            String message = "👤 " + followerName + " started following you";
            notificationService.createNotification(followedId, message);

            System.out.println("✅ Now following user");
            return true;
        }

        System.out.println("❌ Failed to follow user");
        return false;
    }

    // ================= UNFOLLOW USER =================
    public boolean unfollowUser(int followerId, int followedId) {

        // 🚫 Cannot unfollow yourself
        if (followerId == followedId) {
            System.out.println("❌ You cannot unfollow yourself");
            return false;
        }

        // 🚫 Not following
        if (!followDAO.isFollowing(followerId, followedId)) {
            System.out.println("⚠ You are not following this user");
            return false;
        }

        boolean success = followDAO.unfollowUser(followerId, followedId);

        if (success) {
            System.out.println("🚫 Unfollowed successfully");
        } else {
            System.out.println("❌ Failed to unfollow user");
        }

        return success;
    }


    // ================= VIEW FOLLOWERS =================
    public List<User> viewFollowers(int userId) {
        return followDAO.getFollowers(userId);
    }

    // ================= VIEW FOLLOWING =================
    public List<User> viewFollowing(int userId) {
        return followDAO.getFollowing(userId);
    }

    public int getFollowerCount(int userId) {
        return followDAO.getFollowerCount(userId);
    }

    public int getFollowingCount(int userId) {
        return followDAO.getFollowingCount(userId);
    }


}
