package com.revconnect.service;

import com.revconnect.dao.PostDAO;
import com.revconnect.model.Post;
import com.revconnect.dao.NotificationDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.model.PostAnalytics;
import com.revconnect.model.UserType;   // ✅ ADD THIS



import java.util.List;

public class PostService {
    private UserDAO userDAO = new UserDAO();

    private PostDAO postDAO = new PostDAO();
    private HashtagService hashtagService = new HashtagService();
    private LikeService likeService = new LikeService();
    private NotificationDAO notificationDAO = new NotificationDAO();
    // ================= CREATE POST (SAFE + HASHTAGS) =================
    public boolean createPost(Post post, UserType userType) {

        // 1️⃣ EMPTY CONTENT CHECK
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            System.out.println("❌ Post content cannot be empty");
            return false;
        }

        // 2️⃣ DEFAULT POST TYPE
        if (post.getPostType() == null) {
            post.setPostType("NORMAL");
        }

        String postType = post.getPostType().toUpperCase();

        // 3️⃣ INVALID POST TYPE CHECK
        if (!postType.equals("NORMAL") && !postType.equals("PROMOTIONAL")) {
            System.out.println("❌ Invalid post type");
            return false;
        }

        post.setPostType(postType);

        // 4️⃣ PROMOTIONAL POST RULES
        if (postType.equals("PROMOTIONAL")) {

            if (userType == UserType.PERSONAL) {
                System.out.println("❌ Personal users cannot create promotional posts");
                return false;
            }

            if (post.getCtaText() == null || post.getCtaText().isBlank()
                    || post.getCtaLink() == null || post.getCtaLink().isBlank()) {

                System.out.println("❌ Promotional posts must include CTA text and link");
                return false;
            }
        }

        // 5️⃣ REMOVE CTA FROM NORMAL POSTS
        if (postType.equals("NORMAL")) {
            post.setCtaText(null);
            post.setCtaLink(null);
        }

        // 6️⃣ DEFAULT PIN
        post.setPinned(false);

        // ================= SAVE POST =================
        boolean created = postDAO.create(post);
        if (!created) return false;

        // ================= GET POST ID =================
        int postId = postDAO.findLatestPostIdByUser(post.getUserId());
        if (postId == -1) return false;

        // ================= SAVE HASHTAGS =================
        hashtagService.saveHashtags(postId, post.getContent());

        return true;
    }


    // ================= VIEW GLOBAL FEED =================
    public List<Post> viewAllPosts() {
        return postDAO.findAll();
    }

    // ================= VIEW PERSONALIZED FEED =================
    /*
     * Shows:
     * ✅ Own posts
     * ✅ Posts from accepted connections
     * ✅ Posts from followed users
     */
    public List<Post> viewPersonalizedFeed(int userId) {
        return postDAO.findPersonalizedFeed(userId);
    }

    // ================= VIEW MY POSTS ONLY =================
    public List<Post> viewMyPosts(int userId) {
        return postDAO.findPostsByUser(userId);
    }

// ================= EDIT POST =================
    public boolean editPost(Post post) {
        return postDAO.updatePost(post);
    }


    // ================= DELETE POST =================
    public boolean deletePost(int postId, int userId) {
        return postDAO.deletePost(postId, userId);
    }
    public List<Post> filterByUserType(String userType) {
        return postDAO.findPostsByUserType(userType);
    }

    public List<Post> filterSharedPosts() {
        return postDAO.findSharedPosts();
    }


    // ================= SEARCH POSTS BY HASHTAG =================
    public List<Post> searchPostsByHashtag(String tag) {
        return postDAO.findPostsByHashtag(tag.toLowerCase());
    }

    // ================= TRENDING HASHTAGS =================
    public List<String> getTrendingHashtags(int limit) {
        return hashtagService.getTrendingHashtags(limit);
    }

    // ================= LIKE / UNLIKE POST =================
    public boolean toggleLike(int postId, int userId) {

        boolean liked = likeService.toggleLike(postId, userId);

        int postOwnerId = postDAO.getPostOwnerId(postId);

        // ❌ Do not notify yourself
        if (postOwnerId == userId) {
            return liked;
        }


        String userName = userDAO.getUsernameById(userId);
        String message;

        // ✅ DIFFERENT messages for like & unlike
        if (liked) {
            message = "❤️ " + userName + " liked your post";
        } else {
            message = "💔 " + userName + " unliked your post";
        }

        // ✅ Prevent duplicate notification inserts
        if (!notificationDAO.notificationExists(postOwnerId, message)) {
            notificationDAO.addNotification(postOwnerId, message);
        }

        return liked; // true = liked, false = unliked
    }


    public boolean sharePost(int originalPostId, int userId, String comment) {

        Post original = postDAO.findPostById(originalPostId);
        if (original == null) return false;

        StringBuilder newContent = new StringBuilder();

        if (comment != null && !comment.isBlank()) {
            newContent.append(comment).append("\n\n");
        }

        newContent.append("🔁 Shared from: ")
                .append(original.getUsername())
                .append("\n")
                .append("Original: ")
                .append(original.getContent());

        Post sharedPost = new Post();
        sharedPost.setUserId(userId);
        sharedPost.setContent(newContent.toString());
        sharedPost.setPostType("NORMAL");

        // ✅ FIX HERE
        sharedPost.setPinned(true);   // was "Y"

        // ✅ enables share analytics
        sharedPost.setOriginalPostId(originalPostId);

        return postDAO.create(sharedPost);
    }




    // ================= GET LIKE COUNT =================
    public int getLikeCount(int postId) {
        return likeService.getLikeCount(postId);
    }

    // ================= CHECK IF USER LIKED =================
    public boolean hasUserLiked(int postId, int userId) {
        return likeService.hasUserLiked(postId, userId);
    }
    // ================= GET POST BY ID =================
    public Post getPostById(int postId) {
        return postDAO.findPostById(postId);
    }


    // ================= PIN POST (BUSINESS FEATURE) =================
    public boolean pinPost(int postId, int userId) {
        return postDAO.pinPost(postId, userId);
    }

    // ================= UNPIN POST (BUSINESS FEATURE) =================
    public boolean unpinPost(int postId, int userId) {
        return postDAO.unpinPost(postId, userId);
    }

    // ================= POST ANALYTICS (BUSINESS FEATURE) =================
    public List<PostAnalytics> getPostAnalytics(int userId) {
        return postDAO.getPostAnalytics(userId);
    }
    public void showPostAnalytics(int postId, int userId) {

        // Optional: allow only owner / business / creator
        int ownerId = postDAO.getPostOwnerId(postId);
        if (ownerId != userId) {
            System.out.println("❌ You can view analytics only for your own posts");
            return;
        }

        int likes = postDAO.getLikeCount(postId);
        int comments = postDAO.getCommentCount(postId);
        int shares = postDAO.getShareCount(postId);

        System.out.println("\n📊 POST ANALYTICS");
        System.out.println("-------------------------");
        System.out.println("Post ID   : " + postId);
        System.out.println("Likes     : " + likes);
        System.out.println("Comments  : " + comments);
        System.out.println("Shares    : " + shares);
        System.out.println("-------------------------");
    }

}


