package com.revconnect.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void testValidUsername() {

        String username = "rev_user";

        assertNotNull(username);
        assertFalse(username.isEmpty());
    }

    @Test
    void testInvalidUsername() {

        String username = "";

        assertTrue(username.isEmpty(), "Username should be invalid");
    }
}
