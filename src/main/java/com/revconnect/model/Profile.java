package com.revconnect.model;

public class Profile {

    private int profileId;
    private int userId;

    private String fullName;
    private String bio;
    private String location;
    private String website;
    private String profilePic;

    private String category;        // creator / business
    private String contactInfo;     // business
    private String profileVisibility;

    // 🔥 NEW BUSINESS FIELDS
    private String businessAddress;
    private String businessHours;

    // ===== Getters & Setters =====

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    // 🔥 NEW BUSINESS GETTERS / SETTERS

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    @Override
    public String toString() {
        return "Name     : " + fullName +
                "\nBio      : " + bio +
                "\nLocation : " + location +
                "\nWebsite  : " + website +
                (category != null ? "\nCategory : " + category : "") +
                (contactInfo != null ? "\nContact  : " + contactInfo : "") +
                (businessAddress != null ? "\nAddress  : " + businessAddress : "") +
                (businessHours != null ? "\nHours    : " + businessHours : "");
    }
}
