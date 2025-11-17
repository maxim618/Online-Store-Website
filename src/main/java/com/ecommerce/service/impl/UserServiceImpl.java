package com.ecommerce.service.impl;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.UserDto;
import com.ecommerce.web.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDto registerUser(String email, String name, String rawPassword) {
        if(userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole("ROLE_USER");

        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getByEmail(String email) {
        return userRepository.findByEmail(email).map(mapper::toDto).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean validateCredentials(String email, String rawPassword) {
        return userRepository.findByEmail(email).map(u -> encoder.matches(rawPassword, u.getPassword())).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
    }
}
