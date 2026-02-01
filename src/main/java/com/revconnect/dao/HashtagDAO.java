package com.revconnect.dao;

import com.revconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class HashtagDAO {

    // ================= GET OR CREATE HASHTAG =================
    public int getOrCreateHashtag(String tagName) {

        String selectSql =
                "SELECT hashtag_id FROM hashtags WHERE tag_name = ?";

        String insertSql =
                "INSERT INTO hashtags (hashtag_id, tag_name) " +
                        "VALUES (hashtag_seq.NEXTVAL, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) return -1;

            // 1️⃣ Check if hashtag already exists
            PreparedStatement ps = con.prepareStatement(selectSql);
            ps.setString(1, tagName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("hashtag_id");
            }

            // 2️⃣ Insert new hashtag
            ps = con.prepareStatement(insertSql, new String[]{"hashtag_id"});
            ps.setString(1, tagName);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // ================= MAP POST ↔ HASHTAG =================
    public void mapPostHashtag(int postId, int hashtagId) {

        String sql =
                "INSERT INTO post_hashtags (post_id, hashtag_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, hashtagId);
            ps.executeUpdate();

        } catch (Exception e) {
            // Duplicate mappings are ignored safely
        }
    }
    // ================= TRENDING HASHTAGS =================
    public List<String> getTrendingHashtags(int limit) {

        List<String> tags = new ArrayList<>();

        String sql =
                "SELECT h.tag_name, COUNT(ph.post_id) AS usage_count " +
                        "FROM hashtags h " +
                        "JOIN post_hashtags ph ON h.hashtag_id = ph.hashtag_id " +
                        "GROUP BY h.tag_name " +
                        "ORDER BY usage_count DESC " +
                        "FETCH FIRST ? ROWS ONLY";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tags.add(rs.getString("tag_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tags;
    }
}
