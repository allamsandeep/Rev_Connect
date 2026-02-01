package com.revconnect.service;

import com.revconnect.dao.HashtagDAO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagService {

    private HashtagDAO hashtagDAO = new HashtagDAO();

    // ================= EXTRACT HASHTAGS =================
    public Set<String> extractHashtags(String content) {

        Set<String> tags = new HashSet<>();

        if (content == null || content.isBlank()) {
            return tags;
        }

        // Optional fix: handle "# backend"
        content = content.replaceAll("#\\s+", "#");

        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            tags.add(matcher.group(1).toLowerCase());
        }

        return tags;
    }

    // ================= SAVE HASHTAGS FOR POST =================
    public void saveHashtags(int postId, String content) {

        Set<String> tags = extractHashtags(content);

        for (String tag : tags) {
            int hashtagId = hashtagDAO.getOrCreateHashtag(tag);
            if (hashtagId != -1) {
                hashtagDAO.mapPostHashtag(postId, hashtagId);
            }
        }
    }

    // ================= TRENDING HASHTAGS =================
    public List<String> getTrendingHashtags(int limit) {
        return hashtagDAO.getTrendingHashtags(limit);
    }
}
