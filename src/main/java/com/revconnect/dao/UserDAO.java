package com.revconnect.dao;

import com.revconnect.model.User;
import com.revconnect.model.UserType;
import com.revconnect.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.SQLIntegrityConstraintViolationException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final Logger logger =
            LogManager.getLogger(UserDAO.class);

    // ================= REGISTER USER =================
    public boolean insertUser(User user) {

        String sql =
                "INSERT INTO users " +
                        "(user_id, email, username, password, user_type, security_question, security_answer) " +
                        "VALUES (user_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getUserType().name());
            ps.setString(5, user.getSecurityQuestion());
            ps.setString(6, user.getSecurityAnswer());

            boolean inserted = ps.executeUpdate() > 0;
            logger.info("User inserted: {}", user.getUsername());
            return inserted;

        } catch (SQLIntegrityConstraintViolationException e) {

            logger.warn("Username or Email already exists: {}", user.getUsername());
            return false;

        } catch (Exception e) {

            logger.error("Error inserting user: {}", user.getUsername(), e);
            return false;
        }
    }


    // ================= LOGIN =================
    public User login(String username, String password) {

        String sql =
                "SELECT user_id, email, username, password, user_type, " +
                        "security_question, security_answer " +
                        "FROM users WHERE username = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return null;

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setEmail(rs.getString("email"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setUserType(UserType.valueOf(rs.getString("user_type")));
                    u.setSecurityQuestion(rs.getString("security_question"));
                    u.setSecurityAnswer(rs.getString("security_answer"));

                    logger.info("Login successful for user: {}", username);
                    return u;
                }
            }

        } catch (Exception e) {
            logger.error("Login failed for user: {}", username, e);
        }
        return null;
    }

    // ================= RESET PASSWORD =================
    public boolean resetPassword(String username, String answer, String newPassword) {

        String sql =
                "UPDATE users SET password=? WHERE username=? AND security_answer=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.setString(3, answer);

            boolean updated = ps.executeUpdate() > 0;
            logger.info("Password reset for user: {}", username);
            return updated;

        } catch (Exception e) {
            logger.error("Error resetting password for user: {}", username, e);
            return false;
        }
    }

    // ================= SEARCH USERS =================
    public List<User> searchUsers(String keyword) {

        List<User> list = new ArrayList<>();

        String sql =
                "SELECT user_id, username, email " +
                        "FROM users WHERE username LIKE ? OR email LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    list.add(u);
                }
            }

            logger.info("User search completed for keyword: {}", keyword);

        } catch (Exception e) {
            logger.error("Error searching users with keyword: {}", keyword, e);
        }
        return list;
    }

    // ================= GET USERNAME BY ID =================
    public String getUsernameById(int userId) {

        String sql = "SELECT username FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (Exception e) {
            logger.error("Error fetching username for userId: {}", userId, e);
        }

        return "Someone";
    }

    public String getSecurityQuestion(String username) {

        String sql = "SELECT security_question FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("security_question");
            }

        } catch (Exception e) {
            logger.error("Error fetching security question for user: {}", username, e);
        }

        return null;
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {

        String sql =
                "UPDATE users SET password=? WHERE user_id=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.setString(3, currentPassword);

            boolean updated = ps.executeUpdate() > 0;
            logger.info("Password changed for userId: {}", userId);
            return updated;

        } catch (Exception e) {
            logger.error("Error changing password for userId: {}", userId, e);
        }

        return false;
    }
}
