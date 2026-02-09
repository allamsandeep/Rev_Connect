package com.revconnect.dao;

import com.revconnect.util.DBConnection;

import com.revconnect.model.UserConnection;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ConnectionDAO {

    // ================= SEND CONNECTION REQUEST =================
    public boolean sendRequest(int senderId, int receiverId) {

        String sql =
                "INSERT INTO connections (connection_id, sender_id, receiver_id, status) " +
                        "VALUES (connection_seq.NEXTVAL, ?, ?, 'PENDING')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);

            return ps.executeUpdate() > 0;

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // ✅ duplicate or reverse request (UQ_SENDER_RECEIVER)
            return false;

        } catch (Exception e) {
            e.printStackTrace(); // real DB errors only
            return false;
        }
    }


    // ================= ACCEPT OR REJECT REQUEST =================
    public boolean updateRequestStatus(int connectionId, String status) {

        String sql =
                "UPDATE connections SET status=? WHERE connection_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, connectionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= VIEW PENDING REQUESTS (RECEIVER SIDE) =================
    public List<String> getPendingRequests(int userId) {

        List<String> requests = new ArrayList<>();

        String sql =
                "SELECT c.connection_id, u.username " +
                        "FROM connections c " +
                        "JOIN users u ON c.sender_id = u.user_id " +
                        "WHERE c.receiver_id = ? AND c.status = 'PENDING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String row =
                        "Request ID: " + rs.getInt("connection_id") +
                                " | From: " + rs.getString("username");
                requests.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    // ================= GET CONNECTED USER IDS =================
    // (Used for Personalized Feed)
    public List<Integer> getMyConnectionUserIds(int userId) {

        List<Integer> ids = new ArrayList<>();

        String sql =
                "SELECT sender_id, receiver_id " +
                        "FROM connections " +
                        "WHERE status = 'ACCEPTED' " +
                        "AND (sender_id = ? OR receiver_id = ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int sender = rs.getInt("sender_id");
                int receiver = rs.getInt("receiver_id");

                // add the OTHER user, not me
                if (sender != userId) {
                    ids.add(sender);
                } else {
                    ids.add(receiver);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }
    public boolean removeConnection(int userId1, int userId2) {

        String sql =
                "DELETE FROM connections " +
                        "WHERE status = 'ACCEPTED' " +
                        "AND ((sender_id = ? AND receiver_id = ?) " +
                        " OR  (sender_id = ? AND receiver_id = ?))";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public boolean connectionExists(int userId1, int userId2) {

        String sql =
                "SELECT 1 FROM connections " +
                        "WHERE status IN ('PENDING', 'ACCEPTED') " +
                        "AND ((sender_id = ? AND receiver_id = ?) " +
                        " OR  (sender_id = ? AND receiver_id = ?))";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> viewConnections(int userId) {

        List<String> connections = new ArrayList<>();

        String sql =
                "SELECT u.user_id, u.username " +
                        "FROM users u " +
                        "JOIN connections c ON " +
                        "((c.sender_id = ? AND c.receiver_id = u.user_id) " +
                        " OR (c.receiver_id = ? AND c.sender_id = u.user_id)) " +
                        "WHERE c.status = 'ACCEPTED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                connections.add(
                        "User ID: " + rs.getInt("user_id") +
                                " | Username: " + rs.getString("username")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connections;
    }
    public boolean areConnected(int userId1, int userId2) {

        String sql =
                "SELECT 1 FROM connections " +
                        "WHERE status = 'ACCEPTED' " +
                        "AND ((sender_id = ? AND receiver_id = ?) " +
                        " OR  (sender_id = ? AND receiver_id = ?))";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean rejectedRequestExists(int senderId, int receiverId) {

        String sql =
                "SELECT 1 FROM connections " +
                        "WHERE status = 'REJECTED' " +
                        "AND sender_id = ? AND receiver_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void deleteRejectedRequest(int senderId, int receiverId) {

        String sql =
                "DELETE FROM connections " +
                        "WHERE status = 'REJECTED' " +
                        "AND sender_id = ? AND receiver_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public UserConnection getConnectionById(int connectionId) {

        String sql = "SELECT * FROM connections WHERE connection_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, connectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UserConnection uc = new UserConnection();
                uc.setConnectionId(rs.getInt("connection_id"));
                uc.setSenderId(rs.getInt("sender_id"));
                uc.setReceiverId(rs.getInt("receiver_id"));
                uc.setStatus(rs.getString("status"));
                return uc;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
