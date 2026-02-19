package com.jobtracker.jobapplicationtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobapplicationtracker.dto.AuthRequest;
import com.jobtracker.jobapplicationtracker.dto.AuthResponse;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.service.UserService;
import com.jobtracker.jobapplicationtracker.utils.JwtUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	// Endpoint to authenticate user and generate JWT token
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
		// Authenticate the user (verify username and password)
		userService.authenticate(authRequest); // Pass the AuthRequest object

		// Generate JWT token for the user
		String token = jwtUtil.generateToken(authRequest.getUsername());

		// Return the token in the response
		return ResponseEntity.ok(new AuthResponse(token));
	}

	// Endpoint to register a new user
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
		// Convert AuthRequest to User entity
		User user = new User();
		user.setUsername(authRequest.getUsername());
		user.setPassword(authRequest.getPassword());
		user.setEmail(authRequest.getEmail());

		// Register the user
		userService.registerUser(user); // Pass the User object
		return ResponseEntity.ok("User registered successfully");
	}
}
