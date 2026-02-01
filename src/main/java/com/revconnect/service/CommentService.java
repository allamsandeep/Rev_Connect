package com.revconnect.service;

import com.revconnect.dao.CommentDAO;
import com.revconnect.dao.NotificationDAO;
import com.revconnect.dao.PostDAO;
import com.revconnect.dao.UserDAO;

import java.util.List;

public class CommentService {

    private final CommentDAO commentDAO = new CommentDAO();
    private final PostDAO postDAO = new PostDAO();
    private final NotificationService notificationService = new NotificationService();
    private UserDAO userDAO = new UserDAO();

    private NotificationDAO notificationDAO = new NotificationDAO();

    // ================= ADD COMMENT =================
    public boolean addComment(int postId, int userId, String commentText) {

        // ✅ Validation
        if (commentText == null || commentText.trim().isEmpty()) {
            System.out.println("❌ Comment cannot be empty");
            return false;
        }

        if (commentText.length() > 500) {
            System.out.println("❌ Comment too long (max 500 characters)");
            return false;
        }

        // ✅ Save comment
        boolean success = commentDAO.addComment(postId, userId, commentText);

        // 🔔 Notify post owner
        if (success) {
            int postOwnerId = postDAO.getPostOwnerId(postId);

            // 🚫 Avoid self-notification
            if (postOwnerId != -1 && postOwnerId != userId) {
                String commenterName = userDAO.getUsernameById(userId);
                String message = "💬 " + commenterName + " commented on your post";
                notificationService.createNotification(postOwnerId, message);

            }
        }

        return success;
    }

    // ================= VIEW COMMENTS =================
    public List<String> viewComments(int postId) {
        return commentDAO.getCommentsByPost(postId);
    }

    // ================= DELETE COMMENT =================
    public boolean deleteComment(int commentId, int userId) {
        return commentDAO.deleteComment(commentId, userId);
    }
}
