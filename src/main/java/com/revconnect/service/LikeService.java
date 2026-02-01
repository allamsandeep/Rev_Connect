package com.revconnect.service;

import com.revconnect.dao.LikeDAO;
import com.revconnect.dao.PostDAO;

public class LikeService {

    private LikeDAO likeDAO = new LikeDAO();
    private PostDAO postDAO = new PostDAO();                    // ✅ NEW
    private NotificationService notificationService =
            new NotificationService();                          // ✅ NEW

    // ================= TOGGLE LIKE =================
    public boolean toggleLike(int postId, int userId) {

        // 1️⃣ Check if user already liked the post
        boolean alreadyLiked = likeDAO.hasUserLiked(postId, userId);

        if (alreadyLiked) {
            // 2️⃣ If already liked → UNLIKE
            return likeDAO.unlikePost(postId, userId);

        } else {
            // 3️⃣ If not liked → LIKE
            boolean success = likeDAO.likePost(postId, userId);

            // 🔔 SEND NOTIFICATION (ONLY ON LIKE)
            if (success) {
                int postOwnerId = postDAO.getPostOwnerId(postId);

                // 🚫 Don’t notify if user likes own post
                if (postOwnerId != userId && postOwnerId != -1) {
                    notificationService.createNotification(
                            postOwnerId,
                            "❤️ Someone liked your post"
                    );
                }
            }
            return success;
        }
    }

    // ================= CHECK LIKE STATUS =================
    public boolean hasUserLiked(int postId, int userId) {
        return likeDAO.hasUserLiked(postId, userId);
    }

    // ================= GET LIKE COUNT =================
    public int getLikeCount(int postId) {
        return likeDAO.getLikeCount(postId);
    }
}
