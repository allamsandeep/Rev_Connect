package com.revconnect.model;

import java.util.List;

public class CreatorProfile extends Profile {

    private String category;
    private List<String> socialLinks;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getSocialLinks() { return socialLinks; }
    public void setSocialLinks(List<String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nCategory: " + category +
                "\nSocial Links: " + socialLinks;
    }
}
