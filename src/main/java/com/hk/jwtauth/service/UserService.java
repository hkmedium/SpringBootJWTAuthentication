package com.hk.jwtauth.service;

import com.hk.jwtauth.entity.User;
import com.hk.jwtauth.exception.ResourceNotFoundException;
import com.hk.jwtauth.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private IUserRepo userRepo;

    public User getUserById(int userId) {
        return userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public ResponseEntity<?> deleteUser(int userId) {
        if (userRepo.findById(userId).isPresent()) {
            userRepo.deleteById(userId);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

}
