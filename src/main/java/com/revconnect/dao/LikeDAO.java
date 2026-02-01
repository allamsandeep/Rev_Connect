package com.revconnect.dao;

import com.revconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LikeDAO {

    // ================= LIKE POST =================
    public boolean likePost(int postId, int userId) {

        String sql =
                "INSERT INTO post_likes (like_id, post_id, user_id) " +
                        "VALUES (like_seq.NEXTVAL, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            // UNIQUE constraint violation = already liked
            return false;
        }
    }

    // ================= UNLIKE POST =================
    public boolean unlikePost(int postId, int userId) {

        String sql =
                "DELETE FROM post_likes WHERE post_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= CHECK IF USER ALREADY LIKED =================
    public boolean hasUserLiked(int postId, int userId) {

        String sql =
                "SELECT 1 FROM post_likes WHERE post_id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= GET LIKE COUNT =================
    public int getLikeCount(int postId) {

        String sql =
                "SELECT COUNT(*) FROM post_likes WHERE post_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return 0;

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
