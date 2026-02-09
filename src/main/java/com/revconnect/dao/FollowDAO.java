package com.revconnect.dao;

import com.revconnect.model.User;
import com.revconnect.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    // ================= FOLLOW USER =================
    public boolean followUser(int followerId, int followedId) {

        String sql =
                "INSERT INTO follows (follower_id, followed_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, followerId);
            ps.setInt(2, followedId);

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // duplicate follow
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= UNFOLLOW USER =================
    public boolean unfollowUser(int followerId, int followedId) {

        String sql =
                "DELETE FROM follows WHERE follower_id=? AND followed_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, followerId);
            ps.setInt(2, followedId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= CHECK IF FOLLOWING =================
    public boolean isFollowing(int followerId, int followedId) {

        String sql =
                "SELECT 1 FROM follows WHERE follower_id=? AND followed_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, followerId);
            ps.setInt(2, followedId);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= GET FOLLOWERS =================
    public List<User> getFollowers(int userId) {

        List<User> followers = new ArrayList<>();

        String sql =
                "SELECT u.user_id, u.username " +
                        "FROM follows f " +
                        "JOIN users u ON f.follower_id = u.user_id " +
                        "WHERE f.followed_id = ? " +
                        "ORDER BY u.username";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                followers.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return followers;
    }

    // ================= GET FOLLOWING =================
    public List<User> getFollowing(int userId) {

        List<User> following = new ArrayList<>();

        String sql =
                "SELECT u.user_id, u.username " +
                        "FROM follows f " +
                        "JOIN users u ON f.followed_id = u.user_id " +
                        "WHERE f.follower_id = ? " +
                        "ORDER BY u.username";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                following.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return following;
    }

    // ================= FOLLOWER COUNT =================
    public int getFollowerCount(int userId) {

        String sql = "SELECT COUNT(*) FROM follows WHERE followed_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= FOLLOWING COUNT =================
    public int getFollowingCount(int userId) {

        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
