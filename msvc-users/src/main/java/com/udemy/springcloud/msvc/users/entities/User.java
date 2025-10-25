package com.udemy.springcloud.msvc.users.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users",
       indexes = {
         @Index(name = "idx_users_username", columnList = "username", unique = true),
         @Index(name = "idx_users_email", columnList = "email", unique = true)
       })
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String passwordHash; // guardamos hash, no texto plano

    @Column(nullable = false)
    private Boolean enabled = true;

    @Transient
    private boolean admin;

    @NotBlank @Email
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
    @ManyToMany
    @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"),
      uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private List<Role> roles;
    // getters/setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Boolean isEnabled() { return enabled; } public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }

    public List<Role> getRoles() {
      return roles;
    }
    public void setRoles(List<Role> roles) {
      this.roles = roles;
    }
   public boolean isAdmin() {
      return admin;
    }
    public void setAdmin(boolean admin) {
      this.admin = admin;
    }
}
