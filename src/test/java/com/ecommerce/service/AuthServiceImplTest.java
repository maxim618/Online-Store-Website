package com.ecommerce.service;

import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.AuthResponse;
import com.ecommerce.web.dto.LoginRequest;
import com.ecommerce.web.dto.UserDto;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.security.JwtService;
import com.ecommerce.service.impl.AuthServiceImpl;
import com.ecommerce.web.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private JwtService jwtService;
    private UserService userService;
    private UserMapper userMapper;

    private AuthServiceImpl authService;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        userMapper = mock(UserMapper.class);
        encoder = new BCryptPasswordEncoder();


        authService = new AuthServiceImpl(userService,
                userMapper,
                jwtService);
    }

    @Test
    void loginSuccess() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setName("John");
        user.setRole("ROLE_USER");
        user.setPassword(encoder.encode("123"));

        when(userService.loadUserByEmail("test@mail.com"))
                .thenReturn(user);

        when(jwtService.generateToken(user.getEmail(),user.getId(),user.getName(),user.getRole()))
                .thenReturn("TOKEN123");

        when(userMapper.toDto(user))
                .thenReturn(new UserDto(user.getId(), user.getEmail(), user.getName(), user.getRole()));

        AuthResponse response = authService.login(request);

        assertEquals("TOKEN123", response.getToken());
        assertNotNull(response.getUser());
        assertEquals(1L, response.getUser().getId());
    }


    @Test
    void loginWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("wrong");

        UserEntity user = new UserEntity();
        user.setEmail("test@mail.com");
        user.setName("John");
        user.setRole("ROLE_USER");
        user.setPassword(encoder.encode("correct"));

        when(userService.loadUserByEmail("test@mail.com"))
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }
    @Test
    void loginUserNotFoundThrowsException() {

        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@mail.com");
        request.setPassword("123");

        when(userService.loadUserByEmail("wrong@mail.com"))
                .thenThrow(new IllegalArgumentException("User not found"));

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
    }

    @Test
    void loginWithNullEmail() {

        LoginRequest request = new LoginRequest();
        request.setEmail(null);
        request.setPassword("123");

        // userService должен выбросить IllegalArgumentException
        when(userService.loadUserByEmail(null))
                .thenThrow(new IllegalArgumentException("Email cannot be null"));

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
    }

    @Test
    void loginWithEmptyEmail() {

        LoginRequest request = new LoginRequest();
        request.setEmail(" "); // пустой email
        request.setPassword("123");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Email cannot be empty", ex.getMessage());
    }

}
