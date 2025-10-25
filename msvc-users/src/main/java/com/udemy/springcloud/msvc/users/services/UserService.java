package com.udemy.springcloud.msvc.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udemy.springcloud.msvc.users.entities.Role;
import com.udemy.springcloud.msvc.users.entities.User;
import com.udemy.springcloud.msvc.users.repositories.RoleRepository;
import com.udemy.springcloud.msvc.users.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User save(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRoles(getRoles(user));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {
        
        Optional<User> userOptional = this.findById(id);

        return userOptional.map(userDb -> {
            userDb.setEmail(user.getEmail()); 
            userDb.setUsername(user.getUsername());
            if (user.isEnabled() == null) {
                userDb.setEnabled(true);
            } else {
                userDb.setEnabled(user.isEnabled());
            }
        userDb.setRoles(getRoles(user));
            return Optional.of(userRepository.save(userDb));

        }).orElseGet(() -> Optional.empty());
    }

    private List<Role> getRoles(User user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        roleOptional.ifPresent(roles::add);

        if(user.isAdmin()){
            Optional<Role> roleAdminOptional = roleRepository.findByName("ROLE_ADMIN");
            roleAdminOptional.ifPresent(roles::add);
        }
        return roles;
    }
}
