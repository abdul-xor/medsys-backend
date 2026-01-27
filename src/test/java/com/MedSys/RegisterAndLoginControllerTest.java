package com.MedSys;

import com.MedSys.controller.RegisterAndLoginController;
import com.MedSys.dto.LoginRequest;
import com.MedSys.dto.LoginResponse;
import com.MedSys.entity.User;
import com.MedSys.jwt.JwtUtil;
import com.MedSys.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterAndLoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private RegisterAndLoginController controller;

    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "john", "pass", "john@test.com", "HOSPITAL");
        loginRequest = new LoginRequest("john", "pass");
    }

    @Test
    void registerUser_Success() {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = controller.registerUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void registerUser_UserAlreadyExists() {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("User Already Exists"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.registerUser(user));

        assertEquals("User Already Exists", ex.getMessage());
    }

    @Test
    void registerUser_NullUser() {
        when(userService.registerUser(null))
                .thenThrow(new NullPointerException("User is null"));

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> controller.registerUser(null));

        assertEquals("User is null", ex.getMessage());
    }

    @Test
    void loginUser_Success() {
        when(userService.loadUserByUsername("john"))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        "john", "pass", java.util.List.of(() -> "HOSPITAL")
                ));

        when(jwtUtil.generateToken("john")).thenReturn("token123");
        when(userService.getUserByUsername("john")).thenReturn(user);

        // ✅ IMPORTANT: stub authenticate for success
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        ResponseEntity<LoginResponse> response = controller.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token123", response.getBody().getToken());
    }

    @Test
    void loginUser_InvalidCredentials() {
        doThrow(new AuthenticationException("Bad credentials") {})
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.loginUser(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void loginUser_UserNotFound() {
        when(userService.loadUserByUsername("john"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        org.springframework.security.core.userdetails.UsernameNotFoundException ex =
                assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                        () -> controller.loginUser(loginRequest));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void loginUser_JwtFailure() {
        when(userService.loadUserByUsername("john"))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        "john", "pass", java.util.List.of(() -> "HOSPITAL")
                ));

        when(jwtUtil.generateToken("john"))
                .thenThrow(new RuntimeException("JWT Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.loginUser(loginRequest));

        assertEquals("JWT Error", ex.getMessage());
    }

    @Test
    void loginUser_NullRequest() {
        assertThrows(NullPointerException.class, () -> controller.loginUser(null));
    }
    
    
    @Test
    void loginUser_AuthenticationManagerThrows() {
        doThrow(new AuthenticationException("Bad credentials") {})
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.loginUser(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void loginUser_UserDetailsNull() {
        when(userService.loadUserByUsername("john"))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        "john", "pass", java.util.List.of(() -> "HOSPITAL")
                ));

        when(jwtUtil.generateToken("john")).thenReturn("token123");

        when(userService.getUserByUsername("john")).thenReturn(null);

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> controller.loginUser(loginRequest));

        assertTrue(ex.getMessage().contains("null"));
    }

}
