package com.revconnect.dao;

import com.revconnect.model.User;
import com.revconnect.model.UserType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private final UserDAO userDAO = new UserDAO();

    // ================= INSERT USER =================
    @Test
    void testInsertUser() {

        long time = System.currentTimeMillis();

        User user = new User();
        user.setEmail("test" + time + "@mail.com");
        user.setUsername("test_user_" + time);
        user.setPassword("test123");
        user.setUserType(UserType.PERSONAL);
        user.setSecurityQuestion("pet?");
        user.setSecurityAnswer("dog");

        boolean result = userDAO.insertUser(user);

        assertTrue(result);
    }

    // ================= LOGIN =================
    @Test
    void testLoginSuccess() {

        long time = System.currentTimeMillis();

        User user = new User();
        user.setEmail("login" + time + "@test.com");
        user.setUsername("login_user_" + time);
        user.setPassword("login123");
        user.setUserType(UserType.PERSONAL);
        user.setSecurityQuestion("city?");
        user.setSecurityAnswer("hyd");

        userDAO.insertUser(user);

        User loggedIn = userDAO.login(user.getUsername(), "login123");

        assertNotNull(loggedIn);
        assertEquals(user.getUsername(), loggedIn.getUsername());
    }

    // ================= RESET PASSWORD =================
    @Test
    void testResetPassword() {

        long time = System.currentTimeMillis();

        User user = new User();
        user.setEmail("reset" + time + "@mail.com");
        user.setUsername("reset_user_" + time);
        user.setPassword("oldpass");
        user.setUserType(UserType.PERSONAL);
        user.setSecurityQuestion("pet?");
        user.setSecurityAnswer("dog");

        userDAO.insertUser(user);

        boolean result = userDAO.resetPassword(
                user.getUsername(),
                "dog",
                "newpass123"
        );

        assertTrue(result);
    }

    // ================= SEARCH USERS =================
    @Test
    void testSearchUsers() {

        List<User> users = userDAO.searchUsers("test");

        assertNotNull(users);
    }

    // ================= GET USERNAME BY ID =================
    @Test
    void testGetUsernameById() {

        String username = userDAO.getUsernameById(1);

        assertNotNull(username);
    }
}
