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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.OK;


@Service
public class AuthService {
    @Autowired
    private Bucket loginBucket;
    @Autowired
    private Bucket signupBucket;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomResponse signup(SignupRequest signupRequest) throws UserAlreadyExistException {
        if (!signupBucket.tryConsume(1)) {
            throw new RuntimeException("Rate limit exceeded for signup !!");
        }
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserAlreadyExistException("Username is already taken!");
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userRepository.save(user);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setStatus(OK);
        customResponse.setMessage("User registered successfully!");
        return customResponse;
    }

    public CustomResponse login(JwtRequest jwtRequest) throws RateLimitException {
        if (!loginBucket.tryConsume(1)) {
            throw new RateLimitException("Rate limit exceeded for login !!");
        }
        this.doAuthenticate(jwtRequest.getUsername(), jwtRequest.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token = this.jwtHelper.generateToken(userDetails);
        JwtResponse jwtResponse = JwtResponse.builder().jwtToken(token).username(userDetails.getUsername()).build();
        CustomResponse customResponse = new CustomResponse();
        customResponse.setStatus(OK);
        customResponse.setData(jwtResponse);
        customResponse.setMessage("User Login successfully");
        return customResponse;
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid username or password !! {}" + e.getMessage());
        }
    }
}
