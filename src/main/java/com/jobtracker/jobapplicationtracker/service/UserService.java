package com.jobtracker.jobapplicationtracker.service;

import java.util.Optional;

import com.jobtracker.jobapplicationtracker.dto.AuthRequest;
import com.jobtracker.jobapplicationtracker.entity.User;

public interface UserService {

	User registerUser(User user);

	User getUserByUsername(String username);

	User getUserByEmail(String email);

	Optional<User> getUserById(Long id);

	boolean usernameExists(String username);

	boolean emailExists(String email);

	// Add authenticate method for login functionality
	void authenticate(AuthRequest authRequest);
}
