package com.revconnect.dao;

import com.revconnect.model.Comment;
import com.revconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // ================= ADD COMMENT =================
    public boolean addComment(int postId, int userId, String commentText) {

        String sql =
                "INSERT INTO post_comments (comment_id, post_id, user_id, comment_text) " +
                        "VALUES (comment_seq.NEXTVAL, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, commentText);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= GET COMMENTS BY POST =================
    public List<Comment> getCommentsByPost(int postId) {

        List<Comment> comments = new ArrayList<>();

        String sql =
                "SELECT c.comment_id, u.username, c.comment_text, c.commented_at " +
                        "FROM post_comments c " +
                        "JOIN users u ON c.user_id = u.user_id " +
                        "WHERE c.post_id = ? " +
                        "ORDER BY c.commented_at ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setUsername(rs.getString("username"));
                c.setCommentText(rs.getString("comment_text"));
                c.setCommentedAt(rs.getTimestamp("commented_at"));

                comments.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return comments;
    }

    // ================= DELETE COMMENT (OWN ONLY) =================
    public boolean deleteComment(int commentId, int userId) {

        String sql =
                "DELETE FROM post_comments WHERE comment_id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
