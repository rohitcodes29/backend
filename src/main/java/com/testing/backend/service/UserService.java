package com.testing.backend.service;

import com.testing.backend.entity.User;
import com.testing.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Save a new user to the database
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Get all users from the database
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get a user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Update an existing user
     */
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (userDetails.getName() != null) {
                user.setName(userDetails.getName());
            }
            if (userDetails.getEmail() != null) {
                user.setEmail(userDetails.getEmail());
            }
            return userRepository.save(user);
        }
        return null;
    }
    
    /**
     * Delete a user by ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
