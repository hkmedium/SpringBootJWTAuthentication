package com.hk.jwtauth.service;

import com.hk.jwtauth.entity.User;
import com.hk.jwtauth.repository.IUserRepo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final IUserRepo userRepository;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public CustomOAuth2UserService(IUserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        String googleAccessToken = userRequest.getAccessToken().getTokenValue();
        String email = oAuth2User.getAttribute("email");

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            //userRepository.save(new User(email, oAuth2User.getAttribute("name")));
        }
        return oAuth2User;
    }
}