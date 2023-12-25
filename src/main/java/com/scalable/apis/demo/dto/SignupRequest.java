package com.scalable.apis.demo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SignupRequest {

    private String username;
    private String password;
}
