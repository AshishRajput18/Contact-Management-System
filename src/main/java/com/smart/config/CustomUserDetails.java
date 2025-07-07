package com.smart.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.entities.User;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Make sure role has prefix ROLE_
        String role = user.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // ✅ Use email (or username) for login, not name
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // enable this feature as needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // enable this feature as needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // enable this feature as needed
    }

    @Override
    public boolean isEnabled() {
        return true; // enable/disable user account
    }

    public User getUser() {
        return this.user;
    }
}
