package com.revconnect.model;

public class BusinessHour {

    private int hourId;          // Primary Key
    private int profileId;       // FK → USER_PROFILE.PROFILE_ID
    private String dayOfWeek;    // MON, TUE, WED, THU, FRI, SAT, SUN
    private String openTime;     // HH:MM (24-hour format)
    private String closeTime;    // HH:MM (24-hour format)
    private String isClosed;     // Y / N

    // ===== Constructors =====

    public BusinessHour() {
    }

    public BusinessHour(int profileId, String dayOfWeek,
                        String openTime, String closeTime, String isClosed) {
        this.profileId = profileId;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed;
    }

    // ===== Getters & Setters =====

    public int getHourId() {
        return hourId;
    }

    public void setHourId(int hourId) {
        this.hourId = hourId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(String isClosed) {
        this.isClosed = isClosed;
    }

    @Override
    public String toString() {
        if ("Y".equalsIgnoreCase(isClosed)) {
            return dayOfWeek + ": Closed";
        }
        return dayOfWeek + ": " + openTime + " - " + closeTime;
    }
}
