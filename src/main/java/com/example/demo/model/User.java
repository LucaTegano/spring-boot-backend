package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a User in the system.
 * This entity is used for authentication and to own other data like notes and tasks.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor // Provides a no-argument constructor, required by JPA
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = false; // Default new users to disabled until verified

    @Column(length = 255) // Optional: Specify a max length for the URL/path
    private String picture;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    // --- Relationships ---
    // We will add relationships to other entities like Note, Group, etc.
    // as we create those entity classes. For example:
    // @OneToMany(mappedBy = "user")
    // private List<Note> notes;
    //
    // @OneToMany(mappedBy = "user")
    // private List<PersonalTask> personalTasks;
    //
    // @ManyToMany(mappedBy = "members")
    // private Set<Group> groups;


    /**
     * Constructor for creating a new, unverified user.
     * @param username The user's chosen name.
     * @param email The user's email address.
     * @param password The user's hashed password.
     */
    public User(String username,String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = false; // New users are disabled by default
    }

    // --- UserDetails Implementation ---
    // Spring Security requires these methods to handle authentication.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, we are not using roles. Return an empty list.
        // In a real app, you would map a user's roles (e.g., from a @ManyToMany Role field)
        // to SimpleGrantedAuthority objects.
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        // We don't have account expiration logic, so we return true.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // We don't have account locking logic, so we return true.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // We don't have password expiration logic, so we return true.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // This is tied directly to our 'enabled' field.
        return this.enabled;
    }
}