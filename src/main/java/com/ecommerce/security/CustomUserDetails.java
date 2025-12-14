package com.ecommerce.security;

import com.ecommerce.persistence.model.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String name;
    private final String password;
    private final String role;
    private final boolean enabled;

    // Конструктор для UserEntity (login/register)
    public CustomUserDetails(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
    }

    // Конструктор для JWT (без БД)
    public CustomUserDetails(Long id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = null; // пароль при JWT не нужен
        this.role = role;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
