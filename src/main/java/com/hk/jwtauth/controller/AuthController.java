package com.hk.jwtauth.controller;


import com.hk.jwtauth.dto.AuthRequest;
import com.hk.jwtauth.dto.RefreshTokenRequest;
import com.hk.jwtauth.entity.User;
import com.hk.jwtauth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/users") //http://localhost:8084/api/auth/users
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return authService.createUser(user);
    }
    @PostMapping("/login") //http://localhost:8084/api/auth/login
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @PostMapping("/access-token") //http://localhost:8084/api/auth/access-token
    public ResponseEntity<?> getAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.getAccessToken(refreshTokenRequest.getRefreshToken());
    }
}

