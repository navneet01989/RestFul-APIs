package com.scalable.apis.demo.service;

import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.dto.SignupRequest;
import com.scalable.apis.demo.dto.JwtRequest;
import com.scalable.apis.demo.dto.JwtResponse;
import com.scalable.apis.demo.entity.User;
import com.scalable.apis.demo.exception.RateLimitException;
import com.scalable.apis.demo.exception.UserAlreadyExistException;
import com.scalable.apis.demo.repository.UserRepository;
import com.scalable.apis.demo.security.JwtHelper;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private Bucket bucket;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup() throws UserAlreadyExistException, RateLimitException {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testUser");
        signupRequest.setPassword("testPassword");

        when(bucket.tryConsume(1)).thenReturn(true);
        when(userRepository.existsByUsername("testUser")).thenReturn(false);

        CustomResponse response = authService.signup(signupRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("User registered successfully!", response.getMessage());

        verify(bucket, times(1)).tryConsume(1);
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogin() throws RateLimitException {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("testUser");
        jwtRequest.setPassword("testPassword");

        when(bucket.tryConsume(1)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(mock(UserDetails.class));
        when(jwtHelper.generateToken(any(UserDetails.class))).thenReturn("mockedToken");

        CustomResponse response = authService.login(jwtRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        JwtResponse jwtResponse = (JwtResponse) response.getData();
        assertEquals("mockedToken", jwtResponse.getJwtToken());

        verify(bucket, times(1)).tryConsume(1);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername("testUser");
        verify(jwtHelper, times(1)).generateToken(any(UserDetails.class));
    }
}
