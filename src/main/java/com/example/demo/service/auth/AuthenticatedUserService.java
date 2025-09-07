package com.example.demo.service.auth;

import com.example.demo.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    /**
     * Retrieves the full User object for the currently authenticated user.
     * Assumes the principal is a User object, which is true if you've configured it correctly.
     *
     * @return The authenticated User entity.
     * @throws IllegalStateException if no user is authenticated.
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("No authenticated user found in security context.");
        }

        // The principal is the User object you configured in your UserDetailsService
        return (User) authentication.getPrincipal();
    }

    /**
     * A convenient helper method to directly get the ID of the authenticated user.
     *
     * @return The Long ID of the authenticated user.
     * @throws IllegalStateException if no user is authenticated.
     */
    public Long getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }
}