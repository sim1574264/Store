package com.store.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testDefaultConstructor() {
        User user = new User();
        assertEquals(0, user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    public void testParameterizedConstructor() {
        User user = new User(1, "John Doe", "john@example.com", "password123");
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();
        user.setId(5);
        user.setName("Jane Smith");
        user.setEmail("jane@example.com");
        user.setPassword("securepass");

        assertEquals(5, user.getId());
        assertEquals("Jane Smith", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("securepass", user.getPassword());
    }

    @Test
    public void testEmailValidation() {
        User user = new User();
        user.setEmail("test@domain.com");
        assertEquals("test@domain.com", user.getEmail());

        user.setEmail("another.email@sub.domain.org");
        assertEquals("another.email@sub.domain.org", user.getEmail());
    }

    @Test
    public void testPasswordHandling() {
        User user = new User();
        String password = "mySecretPassword123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testEmptyStrings() {
        User user = new User();
        user.setId(0);
        user.setName("");
        user.setEmail("");
        user.setPassword("");

        assertEquals(0, user.getId());
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPassword());
    }

    @Test
    public void testUserEquality() {
        User user1 = new User(1, "Test User", "test@example.com", "pass");
        User user2 = new User(1, "Test User", "test@example.com", "pass");

        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user1.getPassword(), user2.getPassword());
    }

    @Test
    public void testSpecialCharactersInName() {
        User user = new User();
        user.setName("O'Connor");
        assertEquals("O'Connor", user.getName());

        user.setName("José García");
        assertEquals("José García", user.getName());
    }
}
