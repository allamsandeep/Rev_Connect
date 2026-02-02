package com.revconnect.service;

import com.revconnect.dao.BusinessHourDAO;
import com.revconnect.model.BusinessHour;

import java.util.List;

public class BusinessHourService {

    private final BusinessHourDAO businessHourDAO =
            new BusinessHourDAO();

    // ================= ADD BUSINESS HOURS =================
    public boolean addBusinessHours(int profileId,
                                    List<BusinessHour> hours) {

        // Rule: Clear existing hours first
        businessHourDAO.deleteBusinessHoursByProfile(profileId);

        boolean success = true;

        for (BusinessHour bh : hours) {
            bh.setProfileId(profileId);
            success &= businessHourDAO.addBusinessHour(bh);
        }

        return success;
    }

    // ================= VIEW BUSINESS HOURS =================
    public List<BusinessHour> getBusinessHours(int profileId) {
        return businessHourDAO.getBusinessHoursByProfile(profileId);
    }

    // ================= CHECK IF BUSINESS IS OPEN =================
    // (Future enhancement – NOT used in P1)
    public boolean isBusinessOpenNow(int profileId,
                                     String day,
                                     String currentTime) {

        List<BusinessHour> hours =
                businessHourDAO.getBusinessHoursByProfile(profileId);

        for (BusinessHour bh : hours) {
            if (bh.getDayOfWeek().equalsIgnoreCase(day)
                    && bh.getIsClosed().equals("N")) {

                return currentTime.compareTo(bh.getOpenTime()) >= 0
                        && currentTime.compareTo(bh.getCloseTime()) <= 0;
            }
        }
        return false;
    }
}
