package com.example.requipassignment.service;

import com.example.requipassignment.model.User;
import com.example.requipassignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("Rahul Sharma");
        sampleUser.setEmail("rahul@example.com");
        sampleUser.setPrimaryMobile("9876543210");
        sampleUser.setAadhaar("123456789012");
        sampleUser.setPan("ABCDE1234F");
        sampleUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        sampleUser.setPlaceOfBirth("Delhi");
        sampleUser.setCurrentAddress("Hostel 3, College Campus");
        sampleUser.setPermanentAddress("Delhi NCR");
    }

    @Test
    @DisplayName("Should successfully create a user when all fields are unique")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPrimaryMobile(anyString())).thenReturn(false);
        when(userRepository.existsByAadhaar(anyString())).thenReturn(false);
        when(userRepository.existsByPan(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // When
        User createdUser = userService.createUser(sampleUser);

        // Then
        assertNotNull(createdUser);
        assertEquals("Rahul Sharma", createdUser.getName());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should throw exception when trying to create a user with a duplicate email")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail("rahul@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(sampleUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fetch paginated users correctly")
    void shouldReturnPaginatedUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        List<User> userList = Collections.singletonList(sampleUser);
        Page<User> userPage = new PageImpl<>(userList, pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Rahul Sharma", result.getContent().get(0).getName());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should successfully update specific fields of an existing user")
    void shouldUpdateUserSuccessfully() {
        // Given
        User updateDetails = new User();
        updateDetails.setName("Rahul Updated");
        updateDetails.setSecondaryMobile("9999999999");
        updateDetails.setCurrentAddress("New Delhi Address");
        updateDetails.setPermanentAddress("Delhi NCR");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User updatedUser = userService.updateUser(1L, updateDetails);

        // Then
        assertNotNull(updatedUser);
        assertEquals("Rahul Updated", updatedUser.getName());
        assertEquals("9999999999", updatedUser.getSecondaryMobile());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should soft delete user successfully if user exists")
    void shouldSoftDeleteUserSuccessfully() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when attempting to delete non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        when(userRepository.existsById(99L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(99L);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }
}