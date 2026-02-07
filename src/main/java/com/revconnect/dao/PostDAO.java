package com.revconnect.dao;

import com.revconnect.model.Post;
import com.revconnect.util.DBConnection;
import com.revconnect.model.PostAnalytics;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    // ================= CREATE POST =================
    public boolean create(Post post) {

        String sql =
                "INSERT INTO posts " +
                        "(post_id, user_id, content, post_type, pinned, original_post_id, created_at) " +
                        "VALUES (post_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getPostType());   // NORMAL / PROMOTIONAL
            ps.setString(4, post.isPinned() ? "Y" : "N");

// 🔁 THIS IS THE KEY LINE
            if (post.getOriginalPostId() != null) {
                ps.setInt(5, post.getOriginalPostId()); // shared post
            } else {
                ps.setNull(5, Types.INTEGER);            // original post
            }

            ps.setTimestamp(6,
                    post.getCreatedAt() != null
                            ? new Timestamp(post.getCreatedAt().getTime())
                            : new Timestamp(System.currentTimeMillis())
            );


            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ================= VIEW GLOBAL FEED =================
    public List<Post> findAll() {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT p.post_id, p.user_id, u.username, p.content, p.created_at " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "ORDER BY p.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    // ================= PERSONALIZED FEED =================
    public List<Post> findPersonalizedFeed(int myUserId) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT DISTINCT p.post_id, p.user_id, u.username, p.content, p.created_at " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "LEFT JOIN connections c ON " +
                        " (c.status = 'ACCEPTED' AND " +
                        " ((c.sender_id = ? AND c.receiver_id = p.user_id) " +
                        " OR (c.receiver_id = ? AND c.sender_id = p.user_id))) " +
                        "LEFT JOIN follows f ON f.followed_id = p.user_id AND f.follower_id = ? " +
                        "WHERE p.user_id = ? " +
                        "   OR c.connection_id IS NOT NULL " +
                        "   OR f.follower_id IS NOT NULL " +
                        "ORDER BY p.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, myUserId);
            ps.setInt(2, myUserId);
            ps.setInt(3, myUserId);
            ps.setInt(4, myUserId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    // ================= VIEW MY POSTS ONLY =================
    public List<Post> findPostsByUser(int userId) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT p.post_id, p.user_id, u.username, p.content, p.created_at " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "WHERE p.user_id = ? " +
                        "ORDER BY p.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    // ================= FIND POSTS BY HASHTAG =================
    public List<Post> findPostsByHashtag(String tagName) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT p.post_id, u.username, p.content, p.created_at " +
                        "FROM posts p " +
                        "JOIN post_hashtags ph ON p.post_id = ph.post_id " +
                        "JOIN hashtags h ON ph.hashtag_id = h.hashtag_id " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "WHERE h.tag_name = ? " +
                        "ORDER BY p.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tagName.toLowerCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    // ================= FIND LATEST POST ID =================
    public int findLatestPostIdByUser(int userId) {

        String sql =
                "SELECT post_id FROM posts " +
                        "WHERE user_id = ? " +
                        "ORDER BY created_at DESC FETCH FIRST 1 ROWS ONLY";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("post_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ================= EDIT POST =================
    public boolean updatePost(int postId, int userId, String newContent) {

        String sql =
                "UPDATE posts SET content=? WHERE post_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newContent);
            ps.setInt(2, postId);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // ================= DELETE POST =================
    public boolean deletePost(int postId, int userId) {

        String sql =
                "DELETE FROM posts WHERE post_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= 🔔 GET POST OWNER (NEW) =================
    public int getPostOwnerId(int postId) {

        String sql = "SELECT user_id FROM posts WHERE post_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Post findPostById(int postId) {

        String sql =
                "SELECT p.post_id, p.user_id, u.username, p.content " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "WHERE p.post_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                return post;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public List<Post> findPostsByUserType(String userType) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT p.post_id, p.user_id, u.username, p.content, p.created_at " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "WHERE u.user_type = ? " +
                        "ORDER BY p.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userType);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Post p = new Post();
                p.setPostId(rs.getInt("post_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setUsername(rs.getString("username"));
                p.setContent(rs.getString("content"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }
    public List<Post> findSharedPosts() {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT p.post_id, p.user_id, u.username, p.content, p.created_at " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.user_id " +
                        "WHERE p.original_post_id IS NOT NULL\n" +
                        "ORDER BY p.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Post p = new Post();
                p.setPostId(rs.getInt("post_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setUsername(rs.getString("username"));
                p.setContent(rs.getString("content"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }
    // ================= PIN POST =================
    public boolean pinPost(int postId, int userId) {

        String sql = "UPDATE posts SET pinned='Y' WHERE post_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // ================= UNPIN POST =================
    public boolean unpinPost(int postId, int userId) {

        String sql = "UPDATE posts SET pinned='N' WHERE post_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // ================= POST ANALYTICS =================
    public List<PostAnalytics> getPostAnalytics(int userId) {

        List<PostAnalytics> list = new ArrayList<>();

        String sql = """
        SELECT
            p.post_id,
            COUNT(DISTINCT l.like_id) AS likes,
            COUNT(DISTINCT c.comment_id) AS comments,
            COUNT(DISTINCT s.post_id) AS shares
        FROM posts p
        LEFT JOIN post_likes l ON p.post_id = l.post_id
        LEFT JOIN post_comments c ON p.post_id = c.post_id
        LEFT JOIN posts s ON s.original_post_id = p.post_id
        WHERE p.user_id = ?
        GROUP BY p.post_id
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PostAnalytics a = new PostAnalytics();
                a.setPostId(rs.getInt("POST_ID"));
                a.setLikes(rs.getInt("LIKES"));
                a.setComments(rs.getInt("COMMENTS"));
                a.setShares(rs.getInt("SHARES"));
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    // ================= POST ANALYTICS =================

    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCommentCount(int postId) {
        String sql = "SELECT COUNT(*) FROM post_comments WHERE post_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getShareCount(int postId) {
        String sql = "SELECT COUNT(*) FROM posts WHERE original_post_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
