package com.revconnect.model;

public class BusinessProfile extends Profile {

    private String industry;
    private String address;
    private String contactInfo;
    private String businessHours;

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nIndustry: " + industry +
                "\nAddress: " + address +
                "\nContact: " + contactInfo +
                "\nBusiness Hours: " + businessHours;
    }
}
