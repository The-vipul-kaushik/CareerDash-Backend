package com.jobtracker.jobapplicationtracker.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jobtracker.jobapplicationtracker.dto.AuthRequest;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	private User testUser;

	@BeforeEach
	public void setUp() {
		testUser = new User(null, "testUser", "testuser@example.com", "password123");
	}

	@Test
	public void testRegisterUser() {
		// Arrange
		when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(testUser)).thenReturn(testUser);

		// Act
		User savedUser = userService.registerUser(testUser);

		// Assert
		assertNotNull(savedUser);
		assertEquals("encodedPassword", savedUser.getPassword());
		verify(passwordEncoder, times(1)).encode("password123"); // Verify the original password is encoded
		verify(userRepository, times(1)).save(testUser);
	}

	@Test
	public void testGetUserByUsername() {
		// Arrange
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

		// Act
		User foundUser = userService.getUserByUsername("testUser");

		// Assert
		assertNotNull(foundUser);
		assertEquals("testUser", foundUser.getUsername());
	}

	@Test
	public void testGetUserByUsernameNotFound() {
		// Arrange
		when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			userService.getUserByUsername("nonExistentUser");
		});
		assertEquals("User not found with username: nonExistentUser", exception.getMessage());
	}

	@Test
	public void testGetUserByEmail() {
		// Arrange
		when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(testUser));

		// Act
		User foundUser = userService.getUserByEmail("testuser@example.com");

		// Assert
		assertNotNull(foundUser);
		assertEquals("testuser@example.com", foundUser.getEmail());
	}

	@Test
	public void testGetUserByEmailNotFound() {
		// Arrange
		when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			userService.getUserByEmail("nonexistent@example.com");
		});
		assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
	}

	@Test
	public void testUsernameExists() {
		// Arrange
		when(userRepository.existsByUsername("testUser")).thenReturn(true);

		// Act
		boolean exists = userService.usernameExists("testUser");

		// Assert
		assertTrue(exists);
	}

	@Test
	public void testUsernameNotExists() {
		// Arrange
		when(userRepository.existsByUsername("nonExistentUser")).thenReturn(false);

		// Act
		boolean exists = userService.usernameExists("nonExistentUser");

		// Assert
		assertFalse(exists);
	}

	@Test
	public void testEmailExists() {
		// Arrange
		when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);

		// Act
		boolean exists = userService.emailExists("testuser@example.com");

		// Assert
		assertTrue(exists);
	}

	@Test
	public void testEmailNotExists() {
		// Arrange
		when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

		// Act
		boolean exists = userService.emailExists("nonexistent@example.com");

		// Assert
		assertFalse(exists);
	}

	@Test
	public void testAuthenticateValid() {
		// Arrange
		AuthRequest authRequest = new AuthRequest("testUser", "password123", "testuser@example.com");
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
		when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

		// Act & Assert
		assertDoesNotThrow(() -> userService.authenticate(authRequest));
	}

	@Test
	public void testAuthenticateInvalidUsername() {
		// Arrange
		AuthRequest authRequest = new AuthRequest("nonExistentUser", "password123", "nonexistent@example.com");
		when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			userService.authenticate(authRequest);
		});
		assertEquals("User not found with username: nonExistentUser", exception.getMessage());
	}

	@Test
	public void testAuthenticateInvalidPassword() {
		// Arrange
		AuthRequest authRequest = new AuthRequest("testUser", "wrongPassword", "testuser@example.com");
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
		when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			userService.authenticate(authRequest);
		});
		assertEquals("Invalid credentials", exception.getMessage());
	}
}
