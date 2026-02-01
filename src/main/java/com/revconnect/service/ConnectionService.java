package com.revconnect.service;

import com.revconnect.dao.ConnectionDAO;

import java.util.List;

public class ConnectionService {

    private ConnectionDAO connectionDAO = new ConnectionDAO();

    // ================= SEND CONNECTION REQUEST =================
    public boolean sendConnectionRequest(int senderId, int receiverId) {

        // ❌ cannot send request to self
        if (senderId == receiverId) {
            return false;
        }

        // ❌ prevent duplicate requests
        if (isAlreadyConnectedOrPending(senderId, receiverId)) {
            return false;
        }

        return connectionDAO.sendRequest(senderId, receiverId);
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

    // ================= CHECK DUPLICATE CONNECTION =================
    // (basic version – safe enough for console app)
    private boolean isAlreadyConnectedOrPending(int senderId, int receiverId) {

        List<Integer> connectedUsers =
                connectionDAO.getMyConnectionUserIds(senderId);

        return connectedUsers.contains(receiverId);
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
