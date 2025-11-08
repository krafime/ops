package com.dansmultipro.ops.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseModel {
    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(length = 100, nullable = false)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}