package com.hk.jwtauth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private long timestamp;
    private String message;
    private String path;
    private HttpStatus error;
    private int statusCode;
}
