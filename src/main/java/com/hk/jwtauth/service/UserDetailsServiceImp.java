package com.hk.jwtauth.service;

import com.hk.jwtauth.exception.ResourceNotFoundException;
import com.hk.jwtauth.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private IUserRepo userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws ResourceNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User could not found with mail: " + email));
    }
}
