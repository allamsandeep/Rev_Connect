package com.revconnect.dao;

import java.sql.*;

import com.revconnect.model.Profile;
import com.revconnect.util.DBConnection;

public class ProfileDAO {

    // CREATE PROFILE
    public boolean create(Profile profile) {

        String sql =
                "INSERT INTO user_profile " +
                        "(profile_id, user_id, full_name, bio, location, website, " +
                        " category, contact_info, profile_visibility, " +
                        " business_address, business_hours) " +
                        "VALUES (profile_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFullName());
            ps.setString(3, profile.getBio());
            ps.setString(4, profile.getLocation());
            ps.setString(5, profile.getWebsite());

            // creator / business fields
            ps.setString(6, profile.getCategory());
            ps.setString(7, profile.getContactInfo());

            // privacy (default PUBLIC)
            ps.setString(8,
                    profile.getProfileVisibility() == null
                            ? "PUBLIC"
                            : profile.getProfileVisibility()
            );

            // 🔥 BUSINESS FIELDS (nullable)
            ps.setString(9, profile.getBusinessAddress());
            ps.setString(10, profile.getBusinessHours());

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠ Profile already exists for this user");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // VIEW PROFILE
    public Profile findByUserId(int userId) {

        String sql = "SELECT * FROM user_profile WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Profile p = new Profile();

                p.setUserId(rs.getInt("USER_ID"));
                p.setFullName(rs.getString("FULL_NAME"));
                p.setBio(rs.getString("BIO"));
                p.setLocation(rs.getString("LOCATION"));
                p.setWebsite(rs.getString("WEBSITE"));
                p.setCategory(rs.getString("CATEGORY"));
                p.setContactInfo(rs.getString("CONTACT_INFO"));

                // 🔐 default privacy handling
                p.setProfileVisibility(
                        rs.getString("PROFILE_VISIBILITY") == null
                                ? "PUBLIC"
                                : rs.getString("PROFILE_VISIBILITY")
                );

                // 🔥 BUSINESS FIELDS
                p.setBusinessAddress(rs.getString("BUSINESS_ADDRESS"));
                p.setBusinessHours(rs.getString("BUSINESS_HOURS"));

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    // UPDATE PROFILE
    public boolean update(Profile profile) {

        String sql =
                "UPDATE user_profile SET " +
                        "full_name=?, bio=?, location=?, website=?, " +
                        "category=?, contact_info=?, " +
                        "business_address=?, business_hours=? " +
                        "WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setString(1, profile.getFullName());
            ps.setString(2, profile.getBio());
            ps.setString(3, profile.getLocation());
            ps.setString(4, profile.getWebsite());
            ps.setString(5, profile.getCategory());
            ps.setString(6, profile.getContactInfo());

            // 🔥 BUSINESS FIELDS
            ps.setString(7, profile.getBusinessAddress());
            ps.setString(8, profile.getBusinessHours());

            ps.setInt(9, profile.getUserId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updatePrivacy(int userId, String visibility) {

        String sql =
                "UPDATE user_profile SET profile_visibility = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, visibility);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateBusinessHours(int userId, String hours) {

        String sql =
                "UPDATE user_profile SET business_hours = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hours);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String getBusinessHours(int userId) {

        String sql =
                "SELECT business_hours FROM user_profile WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("business_hours");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
