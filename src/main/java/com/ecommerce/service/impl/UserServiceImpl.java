package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.UserDto;
import com.ecommerce.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void register(String email, String name, String rawPassword) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        UserEntity user = UserEntity.builder()
                .email(email)
                .name(name)
                .password(encoder.encode(rawPassword))
                .role("USER")       // по умолчанию каждый пользователь = USER
                .enabled(true)      // учётная запись активна
                .build();

        userRepository.save(user);
    }

    @Override
    public UserEntity loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    @Override
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}
