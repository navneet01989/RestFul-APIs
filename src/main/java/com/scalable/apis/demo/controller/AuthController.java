package com.scalable.apis.demo.controller;

import com.scalable.apis.demo.Utils.Utils;
import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.dto.SignupRequest;
import com.scalable.apis.demo.dto.JwtRequest;
import com.scalable.apis.demo.dto.JwtResponse;
import com.scalable.apis.demo.exception.RateLimitException;
import com.scalable.apis.demo.exception.UserAlreadyExistException;
import com.scalable.apis.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CustomResponse> signup(@RequestBody SignupRequest signupRequest) {
        try {
            CustomResponse customResponse =  authService.signup(signupRequest);
            return new ResponseEntity<>(customResponse, customResponse.getStatus());
        } catch (UserAlreadyExistException e) {
            return Utils.returnErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse> login(@RequestBody JwtRequest jwtRequest) {
        try {
            CustomResponse customResponse = authService.login(jwtRequest);
            return new ResponseEntity<>(customResponse, customResponse.getStatus());
        } catch (RateLimitException e) {
            return Utils.returnErrorResponse(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }

    }
}
