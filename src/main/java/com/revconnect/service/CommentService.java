package com.revconnect.service;

import com.revconnect.dao.CommentDAO;
import com.revconnect.dao.PostDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.model.Comment;

import java.util.List;

public class CommentService {

    private final CommentDAO commentDAO = new CommentDAO();
    private final PostDAO postDAO = new PostDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NotificationService notificationService = new NotificationService();

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

    // ================= GET COMMENTS BY POST =================
    public List<Comment> getCommentsByPost(int postId) {
        return commentDAO.getCommentsByPost(postId);
    }

    // ================= DELETE COMMENT (OWN ONLY) =================
    public boolean deleteComment(int commentId, int userId) {
        return commentDAO.deleteComment(commentId, userId);
    }
}
