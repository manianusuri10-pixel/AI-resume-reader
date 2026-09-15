package com.aicopilot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private String targetRole;

    private Integer yearsOfExperience;

    private String roles; // comma-separated e.g. "ROLE_USER"

    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.roles = "ROLE_USER";
    }

    public User(String email, String password, String fullName, String targetRole, Integer yearsOfExperience) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.targetRole = targetRole;
        this.yearsOfExperience = yearsOfExperience != null ? yearsOfExperience : 0;
        this.roles = "ROLE_USER";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
