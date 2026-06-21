package com.example.requipassignment.service;

import com.example.requipassignment.model.User;
import com.example.requipassignment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByPrimaryMobile(user.getPrimaryMobile())) {
            throw new IllegalArgumentException("Primary mobile number already registered");
        }
        if (userRepository.existsByAadhaar(user.getAadhaar())) {
            throw new IllegalArgumentException("Aadhaar already exists");
        }
        if (userRepository.existsByPan(user.getPan())) {
            throw new IllegalArgumentException("PAN already exists");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User details) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(details.getName());
        user.setSecondaryMobile(details.getSecondaryMobile());
        user.setCurrentAddress(details.getCurrentAddress());
        user.setPermanentAddress(details.getPermanentAddress());
        // Typically, sensitive identifiers like PAN/Aadhaar/Email aren't modifiable
        // without verification, but you can add them here if needed.

        return userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id); // Triggers our soft delete @SQLDelete annotation
    }
}
