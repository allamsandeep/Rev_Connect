package com.revconnect.service;

import com.revconnect.dao.ConnectionDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.model.UserConnection;


import java.util.List;

public class ConnectionService {

    private ConnectionDAO connectionDAO = new ConnectionDAO();
    private final UserDAO userDAO = new UserDAO();   // ✅ ADD THIS
    private final NotificationService notificationService = new NotificationService();

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

            // 🔔 ✅ NEW: CONNECTION REQUEST NOTIFICATION
            String senderName = userDAO.getUsernameById(senderId);

            notificationService.createNotification(
                    receiverId,
                    "🤝 " + senderName + " sent you a connection request"
            );
        }

        return sent;
    }



    // ================= ACCEPT REQUEST =================
    public boolean acceptRequest(int connectionId) {

        UserConnection connection =
                connectionDAO.getConnectionById(connectionId);

        if (connection == null) {
            System.out.println("❌ Connection not found");
            return false;
        }

        boolean accepted =
                connectionDAO.updateRequestStatus(connectionId, "ACCEPTED");

        if (accepted) {
            int senderId = connection.getSenderId();
            int receiverId = connection.getReceiverId();

            String receiverName =
                    userDAO.getUsernameById(receiverId);

            notificationService.createNotification(
                    senderId,
                    "✅ " + receiverName + " accepted your connection request"
            );
        }

        return accepted;
    }



    // ================= REJECT REQUEST =================
    public boolean rejectRequest(int connectionId) {

        UserConnection connection =
                connectionDAO.getConnectionById(connectionId);

        if (connection == null) {
            System.out.println("❌ Connection not found");
            return false;
        }

        boolean rejected =
                connectionDAO.updateRequestStatus(connectionId, "REJECTED");

        if (rejected) {
            int senderId = connection.getSenderId();
            int receiverId = connection.getReceiverId();

            String receiverName =
                    userDAO.getUsernameById(receiverId);

            notificationService.createNotification(
                    senderId,
                    "❌ " + receiverName + " rejected your connection request"
            );
        }

        return rejected;
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
