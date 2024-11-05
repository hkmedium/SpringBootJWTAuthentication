package com.hk.jwtauth.service;

import com.hk.jwtauth.dto.AuthRequest;
import com.hk.jwtauth.dto.AuthResponse;
import com.hk.jwtauth.entity.User;
import com.hk.jwtauth.exception.ResourceAlreadyExistsException;
import com.hk.jwtauth.exception.ResourceNotFoundException;
import com.hk.jwtauth.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private IUserRepo userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User already exist with user name: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "SUCCESS");
        responseMap.put("data", user);
        responseMap.put("httpStatus", HttpStatus.CREATED);
        return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
    }

    public ResponseEntity<?> createUsers(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User already exist with user name: " + user.getEmail());
        }
        userRepository.save(user);
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }

    public ResponseEntity<?> login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with this email: " + request.getEmail()));
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        return new ResponseEntity<>(new AuthResponse(accessToken, refreshToken), HttpStatus.OK);
    }

    public ResponseEntity<?> getAccessToken(String refreshToken) {
        String email = jwtTokenService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found"));

        if (jwtTokenService.isValidRefreshToken(refreshToken, user.getEmail())) {
            String newAccessToken = jwtTokenService.generateAccessToken(user);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

