package com.livingrank.service;

import com.livingrank.dto.UpdateProfileRequest;
import com.livingrank.dto.UserResponse;
import com.livingrank.entity.AuthProvider;
import com.livingrank.entity.User;
import com.livingrank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@test.com", "Original Name", "hash", AuthProvider.LOCAL);
        testUser.setId(UUID.randomUUID());
        testUser.setEmailVerified(true);
    }

    @Test
    void getProfile_shouldReturnUserResponse() {
        UserResponse response = userService.getProfile(testUser);

        assertEquals(testUser.getId(), response.id());
        assertEquals("test@test.com", response.email());
        assertEquals("Original Name", response.displayName());
    }

    @Test
    void updateProfile_validName_shouldUpdate() {
        UpdateProfileRequest request = new UpdateProfileRequest("New Name", null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateProfile(testUser, request);

        assertEquals("New Name", testUser.getDisplayName());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_withImageUrl_shouldUpdate() {
        UpdateProfileRequest request = new UpdateProfileRequest(null, "https://example.com/img.jpg");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile(testUser, request);

        assertEquals("https://example.com/img.jpg", testUser.getProfileImageUrl());
    }

    @Test
    void updateProfile_withHtmlInName_shouldSanitize() {
        UpdateProfileRequest request = new UpdateProfileRequest("<script>alert('xss')</script>Name", null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile(testUser, request);

        assertEquals("alert('xss')Name", testUser.getDisplayName());
    }

    @Test
    void updateProfile_blankName_shouldNotUpdate() {
        UpdateProfileRequest request = new UpdateProfileRequest("  ", null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile(testUser, request);

        assertEquals("Original Name", testUser.getDisplayName());
    }
}
