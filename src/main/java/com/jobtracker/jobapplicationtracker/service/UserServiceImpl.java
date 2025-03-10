package com.jobtracker.jobapplicationtracker.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtracker.jobapplicationtracker.dto.AuthRequest;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {
        logger.info("Registering user with username: {}", user.getUsername());
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with username: {}", user.getUsername());
        return savedUser;
    }

    @Override
    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new RuntimeException("User not found with username: " + username);
                });
    }

    @Override
    public User getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public boolean usernameExists(String username) {
        logger.info("Checking if username exists: {}", username);
        boolean exists = userRepository.existsByUsername(username);
        logger.info("Username {} exists: {}", username, exists);
        return exists;
    }

    @Override
    public boolean emailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        logger.info("Email {} exists: {}", email, exists);
        return exists;
    }

    @Override
    public void authenticate(AuthRequest authRequest) {
        logger.info("Authenticating user with username: {}", authRequest.getUsername());

        // Check if the user exists in the repository
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", authRequest.getUsername());
                    return new RuntimeException("User not found with username: " + authRequest.getUsername());
                });

        // Verify the password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            logger.error("Invalid credentials for username: {}", authRequest.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User authenticated successfully with username: {}", authRequest.getUsername());
    }
}
