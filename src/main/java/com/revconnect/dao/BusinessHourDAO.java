package com.revconnect.dao;

import com.revconnect.model.BusinessHour;
import com.revconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BusinessHourDAO {

    // ================= ADD BUSINESS HOUR =================
    public boolean addBusinessHour(BusinessHour bh) {

        String sql = """
            INSERT INTO business_hours
            (hour_id, profile_id, day_of_week, open_time, close_time, is_closed)
            VALUES (business_hours_seq.NEXTVAL, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bh.getProfileId());
            ps.setString(2, bh.getDayOfWeek());
            ps.setString(3, bh.getOpenTime());
            ps.setString(4, bh.getCloseTime());
            ps.setString(5, bh.getIsClosed());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            // log later if needed
            return false;
        }
    }

    // ================= GET HOURS BY PROFILE =================
    public List<BusinessHour> getBusinessHoursByProfile(int profileId) {

        List<BusinessHour> list = new ArrayList<>();

        String sql = """
            SELECT hour_id, profile_id, day_of_week,
                   open_time, close_time, is_closed
            FROM business_hours
            WHERE profile_id = ?
            ORDER BY hour_id
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, profileId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BusinessHour bh = new BusinessHour();
                    bh.setHourId(rs.getInt("hour_id"));
                    bh.setProfileId(rs.getInt("profile_id"));
                    bh.setDayOfWeek(rs.getString("day_of_week"));
                    bh.setOpenTime(rs.getString("open_time"));
                    bh.setCloseTime(rs.getString("close_time"));
                    bh.setIsClosed(rs.getString("is_closed"));
                    list.add(bh);
                }
            }

        } catch (Exception e) {
            // log later if needed
        }

        return list;
    }

    // ================= DELETE HOURS (PROFILE RESET) =================
    public boolean deleteBusinessHoursByProfile(int profileId) {

        String sql = "DELETE FROM business_hours WHERE profile_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, profileId);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
