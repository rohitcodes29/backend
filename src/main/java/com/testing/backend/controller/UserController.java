package com.testing.backend.controller;

import com.testing.backend.entity.User;
import com.testing.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * GET API - Fetch all users and emails from database
     * Endpoint: GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    /**
     * GET API - Fetch a specific user by ID
     * Endpoint: GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * POST API - Save a new user to database
     * Endpoint: POST /api/users
     * Request body: { "name": "John Doe", "email": "john@example.com" }
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * PUT API - Update an existing user
     * Endpoint: PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * DELETE API - Delete a user by ID
     * Endpoint: DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * Health check endpoint
     * Endpoint: GET /api/users/health
     */
    @GetMapping("/health/check")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Backend API is running!", HttpStatus.OK);
    }
}
