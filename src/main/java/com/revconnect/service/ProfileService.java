package com.revconnect.service;

import com.revconnect.dao.ProfileDAO;
import com.revconnect.model.Profile;

public class ProfileService {
    private final ProfileDAO profileDAO = new ProfileDAO();  // ✅ ADD THIS


    private ProfileDAO dao = new ProfileDAO();

    public boolean createProfile(Profile p) {
        return dao.create(p);
    }

    public Profile viewProfile(int userId) {
        return dao.findByUserId(userId);
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


}
