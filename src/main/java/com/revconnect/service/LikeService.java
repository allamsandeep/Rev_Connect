package com.revconnect.service;

import com.revconnect.dao.LikeDAO;
import com.revconnect.dao.PostDAO;
import com.revconnect.dao.UserDAO;

public class LikeService {

    private final LikeDAO likeDAO = new LikeDAO();
    private final PostDAO postDAO = new PostDAO();
    private final NotificationService notificationService =
            new NotificationService();
    private final UserDAO userDAO = new UserDAO();

    // ================= TOGGLE LIKE =================
    public boolean toggleLike(int postId, int userId) {

        boolean alreadyLiked = likeDAO.hasUserLiked(postId, userId);

        if (alreadyLiked) {
            likeDAO.unlikePost(postId, userId);
            return false; // 💔 unliked
        } else {
            boolean success = likeDAO.likePost(postId, userId);

            if (success) {
                int postOwnerId = postDAO.getPostOwnerId(postId);

                if (postOwnerId != userId && postOwnerId != -1) {
                    String userName = userDAO.getUsernameById(userId);

                    notificationService.createNotification(
                            postOwnerId,
                            "❤️ " + userName + " liked your post"
                    );
                }
            }
            return true; // ❤️ liked
        }
    }

    public boolean hasUserLiked(int postId, int userId) {
        return likeDAO.hasUserLiked(postId, userId);
    }

    public int getLikeCount(int postId) {
        return likeDAO.getLikeCount(postId);
    }
}
