package com.scalable.apis.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.apis.demo.dto.SignupRequest;
import com.scalable.apis.demo.dto.JwtRequest;
import com.scalable.apis.demo.dto.JwtResponse;
import com.scalable.apis.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testSignupAndLogin() throws Exception {
        // Signup
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testUser");
        signupRequest.setPassword("testPassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Login
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("testUser");
        jwtRequest.setPassword("testPassword");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(jwtRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = new ObjectMapper().readValue(loginResult.getResponse().getContentAsString(), JwtResponse.class);

        // Validate the token
        // You may want to use a library like JWT library for token validation in a real application
        // For simplicity, here we just check that the response contains a non-empty token
        assertEquals(true, jwtResponse.getJwtToken().length() > 0);
    }
}

