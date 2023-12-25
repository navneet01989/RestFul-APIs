package com.scalable.apis.demo.Utils;

import com.scalable.apis.demo.dto.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {

    public static ResponseEntity<CustomResponse> returnErrorResponse(String message, HttpStatus httpStatus) {
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(message);
        customResponse.setStatus(httpStatus);
        return new ResponseEntity<>(customResponse, httpStatus);
    }
}
