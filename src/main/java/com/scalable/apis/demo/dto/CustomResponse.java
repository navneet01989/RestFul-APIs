package com.scalable.apis.demo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
public class CustomResponse {
    private HttpStatus status;
    private String message;
    private String timeStamp = Instant.now().toString();
    private Object data;
}
