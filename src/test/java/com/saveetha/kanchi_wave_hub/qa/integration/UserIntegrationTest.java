package com.saveetha.kanchi_wave_hub.qa.integration;

import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.repository.UserRepository;
import com.saveetha.kanchi_wave_hub.service.UserService;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testUserRegistrationAndLifecycle() {
        long start = System.currentTimeMillis();
        try {
            // 1. Create and Save User
            Users user = new Users();
            user.setName("Alice Cooper");
            user.setEmail("alice@example.com");
            user.setPassword("secretPass123");
            user.setPhone(9998887776L);
            user.setAddress("456 Handloom Road");
            user.setUserType(100);

            Users saved = userService.saveUser(user);
            assertNotNull(saved);
            assertTrue(saved.getId() > 0);

            // 2. Validate email checking works on database
            boolean emailExists = userService.checkMailExist(user);
            assertTrue(emailExists);

            // 3. Find by email
            Users fetched = userService.findByEmail("alice@example.com");
            assertNotNull(fetched);
            assertEquals("Alice Cooper", fetched.getName());

            // 4. Update Profile
            Users updateData = new Users();
            updateData.setName("Alice Updated");
            updateData.setAddress("789 Weaving St");
            updateData.setPhone(1112223333L);

            Users updated = userService.updateUserProfile(saved.getId(), updateData);
            assertNotNull(updated);
            assertEquals("Alice Updated", updated.getName());
            assertEquals("789 Weaving St", updated.getAddress());

            // 5. Verify database update is persistent
            Users finalCheck = userService.getUserProfile(saved.getId());
            assertNotNull(finalCheck);
            assertEquals("Alice Updated", finalCheck.getName());

            TestResultCollector.addResult("testUserRegistrationAndLifecycle", "Integration", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully executed user registration & lifecycle updates on Testcontainers MySQL instance");
        } catch (Exception e) {
            TestResultCollector.addResult("testUserRegistrationAndLifecycle", "Integration", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "User lifecycle integration test failed");
            fail(e);
        }
    }
}
