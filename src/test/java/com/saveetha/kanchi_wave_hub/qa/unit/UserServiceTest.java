package com.saveetha.kanchi_wave_hub.qa.unit;

import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.repository.UserRepository;
import com.saveetha.kanchi_wave_hub.service.UserService;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("securePassword123");
        user.setPhone(9876543210L);
        user.setAddress("123 Silk Lane");
        user.setUserType(100);
    }

    @Test
    void testSaveUser() {
        long start = System.currentTimeMillis();
        try {
            when(userRepository.save(any(Users.class))).thenReturn(user);

            Users saved = userService.saveUser(user);
            assertNotNull(saved);
            assertEquals("john@example.com", saved.getEmail());
            verify(userRepository, times(1)).save(user);

            TestResultCollector.addResult("testSaveUser", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully verified saving user records");
        } catch (Exception e) {
            TestResultCollector.addResult("testSaveUser", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Save user unit test failed");
            fail(e);
        }
    }

    @Test
    void testCheckMailExist() {
        long start = System.currentTimeMillis();
        try {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            boolean exists = userService.checkMailExist(user);
            assertTrue(exists);
            verify(userRepository, times(1)).existsByEmail("john@example.com");

            TestResultCollector.addResult("testCheckMailExist", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Verified email existence checks");
        } catch (Exception e) {
            TestResultCollector.addResult("testCheckMailExist", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Email check unit test failed");
            fail(e);
        }
    }

    @Test
    void testFindByEmail() {
        long start = System.currentTimeMillis();
        try {
            when(userRepository.findByEmail("john@example.com")).thenReturn(user);

            Users found = userService.findByEmail("john@example.com");
            assertNotNull(found);
            assertEquals("john@example.com", found.getEmail());
            verify(userRepository, times(1)).findByEmail("john@example.com");

            TestResultCollector.addResult("testFindByEmail", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Verified finding user by email");
        } catch (Exception e) {
            TestResultCollector.addResult("testFindByEmail", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Find by email unit test failed");
            fail(e);
        }
    }

    @Test
    void testGetUserProfile() {
        long start = System.currentTimeMillis();
        try {
            when(userRepository.getReferenceById(1)).thenReturn(user);

            Users profile = userService.getUserProfile(1);
            assertNotNull(profile);
            assertEquals("John Doe", profile.getName());
            verify(userRepository, times(1)).getReferenceById(1);

            TestResultCollector.addResult("testGetUserProfile", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Verified profile retrieval");
        } catch (Exception e) {
            TestResultCollector.addResult("testGetUserProfile", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Profile check unit test failed");
            fail(e);
        }
    }

    @Test
    void testUpdateUserProfile() {
        long start = System.currentTimeMillis();
        try {
            when(userRepository.getReferenceById(1)).thenReturn(user);
            when(userRepository.save(any(Users.class))).thenReturn(user);

            Users updatedData = new Users();
            updatedData.setName("John Updated");
            updatedData.setAddress("456 Cotton Rd");
            updatedData.setPhone(8888888888L);

            Users result = userService.updateUserProfile(1, updatedData);
            assertNotNull(result);
            assertEquals("John Updated", result.getName());
            assertEquals("456 Cotton Rd", result.getAddress());
            assertEquals(8888888888L, result.getPhone());
            verify(userRepository, times(1)).save(user);

            TestResultCollector.addResult("testUpdateUserProfile", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Verified profile update transaction");
        } catch (Exception e) {
            TestResultCollector.addResult("testUpdateUserProfile", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Update profile unit test failed");
            fail(e);
        }
    }

    @Test
    void testUpdateProfileImage() {
        long start = System.currentTimeMillis();
        try {
            when(userRepository.getReferenceById(1)).thenReturn(user);
            when(userRepository.save(any(Users.class))).thenReturn(user);

            Users result = userService.upateProfileImage(1, "new_profile.jpg");
            assertNotNull(result);
            assertEquals("new_profile.jpg", result.getProfileImage());
            verify(userRepository, times(1)).save(user);

            TestResultCollector.addResult("testUpdateProfileImage", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Verified profile image update transaction");
        } catch (Exception e) {
            TestResultCollector.addResult("testUpdateProfileImage", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "Update image unit test failed");
            fail(e);
        }
    }
}
