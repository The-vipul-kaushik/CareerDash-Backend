package com.jobtracker.jobapplicationtracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jobtracker.jobapplicationtracker.entity.User;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private User testUser;

	@BeforeEach
	public void setUp() {
		// Create and save a user for testing
		testUser = new User(null, "testUser", "testuser@example.com", "password123");
		userRepository.save(testUser);
	}

	@Test
	public void testFindByUsername() {
		// Test finding a user by username
		Optional<User> foundUser = userRepository.findByUsername("testUser");

		assertTrue(foundUser.isPresent());
		assertEquals("testUser", foundUser.get().getUsername());
	}

	@Test
	public void testFindByEmail() {
		// Test finding a user by email
		Optional<User> foundUser = userRepository.findByEmail("testuser@example.com");

		assertTrue(foundUser.isPresent());
		assertEquals("testuser@example.com", foundUser.get().getEmail());
	}

	@Test
	public void testExistsByUsername() {
		// Test checking if a user exists by username
		boolean exists = userRepository.existsByUsername("testUser");

		assertTrue(exists);
	}

	@Test
	public void testExistsByEmail() {
		// Test checking if a user exists by email
		boolean exists = userRepository.existsByEmail("testuser@example.com");

		assertTrue(exists);
	}

	@Test
	public void testFindByUsernameNotFound() {
		// Test finding a user by username when the user doesn't exist
		Optional<User> foundUser = userRepository.findByUsername("nonExistentUser");

		assertFalse(foundUser.isPresent());
	}

	@Test
	public void testFindByEmailNotFound() {
		// Test finding a user by email when the user doesn't exist
		Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

		assertFalse(foundUser.isPresent());
	}
}
