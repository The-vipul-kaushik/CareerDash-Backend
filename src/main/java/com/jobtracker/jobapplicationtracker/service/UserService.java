package com.jobtracker.jobapplicationtracker.service;

import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Signup method
    public User registerUser(User user) {
        // Encode the raw password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.getRoles().add("USER");
        }

        return userRepository.save(user);
    }
}