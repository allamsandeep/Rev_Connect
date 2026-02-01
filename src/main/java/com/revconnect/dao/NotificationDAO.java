package com.revconnect.dao;

import com.revconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // ================= CREATE NOTIFICATION =================
    public void createNotification(int userId, String message) {

        String sql =
                "INSERT INTO notifications (notification_id, user_id, message) " +
                        "VALUES (notification_seq.NEXTVAL, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= UNREAD COUNT =================
    public int getUnreadCount(int userId) {

        String sql =
                "SELECT COUNT(*) FROM notifications " +
                        "WHERE user_id=? AND is_read='N'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= UNREAD NOTIFICATIONS =================
    public List<String> getUnreadNotifications(int userId) {

        List<String> list = new ArrayList<>();

        String sql =
                "SELECT message FROM notifications " +
                        "WHERE user_id=? AND is_read='N' " +
                        "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("message"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= ALL NOTIFICATIONS =================
    public List<String> getAllNotifications(int userId) {

        List<String> list = new ArrayList<>();

        String sql =
                "SELECT message FROM notifications " +
                        "WHERE user_id=? ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("message"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= MARK ALL AS READ =================
    public void markAllAsRead(int userId) {

        String sql =
                "UPDATE notifications SET is_read='Y' WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addNotification(int userId, String message) {

        String sql =
                "INSERT INTO notifications (user_id, message, is_read) " +
                        "VALUES (?, ?, 'N')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
