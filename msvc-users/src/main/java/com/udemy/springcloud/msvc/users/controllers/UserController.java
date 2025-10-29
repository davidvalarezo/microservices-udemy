package com.udemy.springcloud.msvc.users.controllers;

import com.udemy.springcloud.msvc.users.entities.User;
import com.udemy.springcloud.msvc.users.services.IUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
//@RequestMapping("/users")
public class UserController {
  private final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("UserController::createUser Creating user: {}", user.getUsername());
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long id) {
        logger.info("UserController::updateUser Updating user: {}", user.getUsername());
        Optional<User> userUpdatedOptional = userService.update(user,id);

        return userUpdatedOptional
        .map(userUpdated -> {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userUpdated));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.info("UserController::getUserByUsername Getting username: {}", username);
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        logger.info("UserController::getAllUsers Entering list method in UserController");
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("UserController::deleteUser Delete user id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
