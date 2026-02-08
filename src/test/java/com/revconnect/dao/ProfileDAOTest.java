package com.revconnect.dao;

import com.revconnect.model.Profile;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileDAOTest {

    private static ProfileDAO profileDAO;
    private static int testUserId;
    private static int profileId;

    @BeforeAll
    static void setup() {
        profileDAO = new ProfileDAO();

        // ⚠ User must exist in USERS table
        testUserId = 83;

        // Step 1: Check if profile exists
        Profile existing = profileDAO.findByUserId(testUserId);

        // Step 2: Create only if missing
        if (existing == null) {
            Profile profile = new Profile();
            profile.setUserId(testUserId);
            profile.setFullName("Test User");
            profile.setBio("JUnit Profile Test");
            profile.setLocation("India");
            profile.setWebsite("https://test.com");
            profile.setCategory("CREATOR");
            profile.setContactInfo("9999999999");
            profile.setProfileVisibility("PUBLIC");
            profile.setBusinessAddress("Test Address");
            profile.setBusinessHours("9 AM - 6 PM");

            profileDAO.create(profile); // ❌ NO ASSERT HERE
        }

        // Step 3: Fetch profileId safely
        profileId = profileDAO.getProfileIdByUserId(testUserId);
        assertTrue(profileId > 0, "Profile ID must exist");

    }

    @Test
    @Order(1)
    void testFindByUserId() {
        Profile profile = profileDAO.findByUserId(testUserId);
        assertNotNull(profile);
        assertEquals(testUserId, profile.getUserId());
    }

    @Test
    @Order(2)
    void testUpdateProfile() {
        Profile profile = new Profile();
        profile.setUserId(testUserId);
        profile.setFullName("Updated Name");
        profile.setBio("Updated Bio");
        profile.setLocation("Hyderabad");
        profile.setWebsite("https://updated.com");
        profile.setCategory("BUSINESS");
        profile.setContactInfo("8888888888");
        profile.setBusinessAddress("Updated Address");
        profile.setBusinessHours("10 AM - 7 PM");

        assertTrue(profileDAO.update(profile));
    }

    @Test
    @Order(3)
    void testUpdatePrivacy() {
        assertTrue(profileDAO.updatePrivacy(testUserId, "PRIVATE"));
    }

    @Test
    @Order(4)
    void testUpdateBusinessHours() {
        assertTrue(profileDAO.updateBusinessHours(testUserId, "11 AM - 8 PM"));
    }

    @Test
    @Order(5)
    void testGetBusinessHours() {
        String hours = profileDAO.getBusinessHours(testUserId);
        assertNotNull(hours);
        assertEquals("11 AM - 8 PM", hours);
    }

    @Test
    @Order(6)
    void testGetProfileIdByUserId() {
        assertTrue(profileId > 0);
    }

    @Test
    @Order(7)
    void testUpdateExternalLinks() {
        assertTrue(
                profileDAO.updateExternalLinks(
                        profileId,
                        "https://github.com/test,https://linkedin.com/test"
                )
        );
    }
}
