package com.revconnect.service;

import com.revconnect.dao.ProfileDAO;
import com.revconnect.model.Profile;

public class ProfileService {
    private final ProfileDAO profileDAO = new ProfileDAO();  // ✅ ADD THIS


    private ProfileDAO dao = new ProfileDAO();

    public boolean createProfile(Profile p) {
        return dao.create(p);
    }

    public String viewProfile(int userId) {

        Profile p = profileDAO.findByUserId(userId);
        if (p == null) return "❌ Profile not found";

        StringBuilder sb = new StringBuilder();

        sb.append("\n👤 PROFILE DETAILS\n");
        sb.append("Name      : ").append(p.getFullName()).append("\n");
        sb.append("Bio       : ").append(p.getBio()).append("\n");
        sb.append("Location  : ").append(p.getLocation()).append("\n");
        sb.append("Website   : ").append(p.getWebsite()).append("\n");

        if (p.getCategory() != null) {
            sb.append("Category  : ").append(p.getCategory()).append("\n");
        }

        if (p.getContactInfo() != null) {
            sb.append("Contact   : ").append(p.getContactInfo()).append("\n");
        }

        // ✅ THIS IS THE FIX
        if (p.getExternalLinks() != null && !p.getExternalLinks().isBlank()) {
            sb.append("\n🔗 External Links:\n");
            sb.append(p.getExternalLinks()).append("\n");
        }

        return sb.toString();
    }

    public boolean updateProfile(Profile p) {
        return dao.update(p);
    }
    public boolean updatePrivacy(int userId, String visibility) {
        return dao.updatePrivacy(userId, visibility);
    }

    public String viewBusinessHours(int userId) {return profileDAO.getBusinessHours(userId);
    }
    public boolean updateBusinessHours(int userId, String hours) {
        return profileDAO.updateBusinessHours(userId, hours);
    }
    public boolean updateExternalLinks(int userId, String links) {
        int profileId = profileDAO.getProfileIdByUserId(userId);
        if (profileId == -1) return false;

        return profileDAO.updateExternalLinks(profileId, links);
    }
    public Profile getProfileByUserId(int userId) {
        return profileDAO.findByUserId(userId);
    }



}
