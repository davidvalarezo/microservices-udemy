package com.udemy.springcloud.msvc.users.repositories;

import com.udemy.springcloud.msvc.users.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    //User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
