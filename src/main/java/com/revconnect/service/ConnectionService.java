package com.revconnect.service;

import com.revconnect.dao.ConnectionDAO;
import com.revconnect.dao.UserDAO;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.List;

public class ConnectionService {

    private ConnectionDAO connectionDAO = new ConnectionDAO();
    private final UserDAO userDAO = new UserDAO();   // ✅ ADD THIS

    // ================= SEND CONNECTION REQUEST =================
    public boolean sendConnectionRequest(int senderId, int receiverId) {

        if (senderId == receiverId) {
            System.out.println("❌ You cannot send a connection request to yourself");
            return false;
        }

        if (!userDAO.userExists(receiverId)) {
            System.out.println("❌ User does not exist");
            return false;
        }

        // ❌ Pending or Accepted → block
        if (connectionDAO.connectionExists(senderId, receiverId)) {
            System.out.println("⚠ Connection already exists or request already sent");
            return false;
        }

        // ✅ Rejected earlier → allow resend
        if (connectionDAO.rejectedRequestExists(senderId, receiverId)) {
            connectionDAO.deleteRejectedRequest(senderId, receiverId);
            System.out.println("🔁 Previous request was rejected. Sending again...");
        }

        boolean sent = connectionDAO.sendRequest(senderId, receiverId);

        if (sent) {
            System.out.println("📨 Connection request sent");
        }

        return sent;
    }


    // ================= ACCEPT REQUEST =================
    public boolean acceptRequest(int connectionId) {
        return connectionDAO.updateRequestStatus(connectionId, "ACCEPTED");
    }

    // ================= REJECT REQUEST =================
    public boolean rejectRequest(int connectionId) {
        return connectionDAO.updateRequestStatus(connectionId, "REJECTED");
    }

    // ================= VIEW PENDING REQUESTS =================
    public List<String> viewPendingRequests(int userId) {
        return connectionDAO.getPendingRequests(userId);
    }

    // ================= GET MY CONNECTION USER IDS =================
    public List<Integer> getMyConnectionUserIds(int userId) {
        return connectionDAO.getMyConnectionUserIds(userId);
    }


    public boolean removeConnection(int userId, int otherUserId) {
        return connectionDAO.removeConnection(userId, otherUserId);
    }
    public List<String> viewConnections(int userId) {
        return connectionDAO.viewConnections(userId);
    }
    public boolean areConnected(int userId1, int userId2) {
        return connectionDAO.areConnected(userId1, userId2);
    }

}
