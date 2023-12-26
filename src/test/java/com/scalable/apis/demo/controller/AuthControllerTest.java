package com.scalable.apis.demo.controller;

import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.dto.SignupRequest;
import com.scalable.apis.demo.dto.JwtRequest;
import com.scalable.apis.demo.exception.RateLimitException;
import com.scalable.apis.demo.exception.UserAlreadyExistException;
import com.scalable.apis.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testSignupSuccess() throws UserAlreadyExistException, RateLimitException {
        SignupRequest signupRequest = new SignupRequest();
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(authService.signup(signupRequest)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = authController.signup(signupRequest);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(authService, times(1)).signup(signupRequest);
    }

    @Test
    void testSignupUserAlreadyExists() throws UserAlreadyExistException, RateLimitException {
        SignupRequest signupRequest = new SignupRequest();
        when(authService.signup(signupRequest)).thenThrow(new UserAlreadyExistException("User already exists"));

        ResponseEntity<CustomResponse> responseEntity = authController.signup(signupRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        verify(authService, times(1)).signup(signupRequest);
    }

    @Test
    void testSignupRateLimitExceeded() throws UserAlreadyExistException, RateLimitException {
        SignupRequest signupRequest = new SignupRequest();
        when(authService.signup(signupRequest)).thenThrow(new RateLimitException("Rate limit exceeded"));

        ResponseEntity<CustomResponse> responseEntity = authController.signup(signupRequest);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        verify(authService, times(1)).signup(signupRequest);
    }

    @Test
    void testLoginSuccess() throws RateLimitException {
        JwtRequest jwtRequest = new JwtRequest();
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(authService.login(jwtRequest)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = authController.login(jwtRequest);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(authService, times(1)).login(jwtRequest);
    }

    @Test
    void testLoginRateLimitExceeded() throws RateLimitException {
        JwtRequest jwtRequest = new JwtRequest();
        when(authService.login(jwtRequest)).thenThrow(new RateLimitException("Rate limit exceeded"));

        ResponseEntity<CustomResponse> responseEntity = authController.login(jwtRequest);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        verify(authService, times(1)).login(jwtRequest);
    }
}
