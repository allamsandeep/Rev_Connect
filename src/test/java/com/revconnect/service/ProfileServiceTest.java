package com.revconnect.service;

import com.revconnect.model.Profile;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileServiceTest {

    private static ProfileService profileService;
    private static int testUserId;

    @BeforeAll
    static void setup() {
        profileService = new ProfileService();

        // ⚠ user must exist in USERS table
        testUserId = 83;
    }

    @Test
    @Order(1)
    void testGetProfileByUserId() {
        Profile profile = profileService.getProfileByUserId(testUserId);
        assertNotNull(profile, "Profile should exist");
        assertEquals(testUserId, profile.getUserId());
    }

    @Test
    @Order(2)
    void testViewProfile() {
        String result = profileService.viewProfile(testUserId);

        assertNotNull(result);
        assertTrue(result.contains("PROFILE DETAILS"));
        assertTrue(result.contains("Name"));
    }

    @Test
    @Order(3)
    void testUpdateProfile() {
        Profile p = new Profile();
        p.setUserId(testUserId);
        p.setFullName("Service Updated Name");
        p.setBio("Updated via Service Test");
        p.setLocation("Chennai");
        p.setWebsite("https://service-test.com");
        p.setCategory("BUSINESS");
        p.setContactInfo("7777777777");
        p.setBusinessAddress("Service Address");
        p.setBusinessHours("9 AM - 5 PM");

        assertTrue(profileService.updateProfile(p));
    }

    @Test
    @Order(4)
    void testUpdatePrivacy() {
        assertTrue(profileService.updatePrivacy(testUserId, "PRIVATE"));
    }

    @Test
    @Order(5)
    void testUpdateBusinessHours() {
        assertTrue(profileService.updateBusinessHours(testUserId, "10 AM - 6 PM"));
    }

    @Test
    @Order(6)
    void testViewBusinessHours() {
        String hours = profileService.viewBusinessHours(testUserId);
        assertNotNull(hours);
        assertEquals("10 AM - 6 PM", hours);
    }

    @Test
    @Order(7)
    void testUpdateExternalLinks() {
        boolean updated =
                profileService.updateExternalLinks(
                        testUserId,
                        "https://github.com/service-test,https://linkedin.com/service-test"
                );

        assertTrue(updated);
    }
}
