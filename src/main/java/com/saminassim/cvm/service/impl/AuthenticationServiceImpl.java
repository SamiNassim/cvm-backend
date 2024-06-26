package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.dto.request.LoginRequest;
import com.saminassim.cvm.dto.request.RegisterRequest;
import com.saminassim.cvm.dto.response.JwtAuthenticationCookieResponse;
import com.saminassim.cvm.dto.response.UserResponse;
import com.saminassim.cvm.entity.Profile;
import com.saminassim.cvm.entity.Role;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.exception.UserAlreadyExistsException;
import com.saminassim.cvm.exception.UsernameAlreadyExistsException;
import com.saminassim.cvm.repository.ProfileRepository;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.AuthenticationService;
import com.saminassim.cvm.service.JWTService;
import com.saminassim.cvm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ProfileRepository profileRepository;
    private final MessageService messageService;

    public User register(RegisterRequest registerRequest) {

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Un compte avec cet e-mail existe déjà.");
        }

        if(userRepository.findByName(registerRequest.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException("Ce nom d'utilisateur est déjà pris.");
        }

        User user = new User();
        Profile userProfile = new Profile();

        profileRepository.save(userProfile);

        user.setName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        user.setProfile(userProfile);

        userProfile.setUser(user);

        return userRepository.save(user);
    }

    public JwtAuthenticationCookieResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        ResponseCookie jwtCookie = ResponseCookie.from("CVMJWT", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60)
                .sameSite("None")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("CVMRefresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(604800)
                .sameSite("None")
                .build();

        JwtAuthenticationCookieResponse jwtAuthenticationCookieResponse = new JwtAuthenticationCookieResponse();

        jwtAuthenticationCookieResponse.setTokenCookie(jwtCookie);
        jwtAuthenticationCookieResponse.setRefreshCookie(refreshCookie);

        return jwtAuthenticationCookieResponse;

    }

    public JwtAuthenticationCookieResponse refreshToken(String refreshToken){

        String userEmail = jwtService.extractUserName(refreshToken);
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        if(jwtService.isTokenValid(refreshToken, user)) {
            var jwt = jwtService.generateToken(user);

            JwtAuthenticationCookieResponse jwtAuthenticationCookieResponse = new JwtAuthenticationCookieResponse();

            ResponseCookie jwtCookie = ResponseCookie.from("CVMJWT", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60)
                    .sameSite("None")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("CVMRefresh", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(604800)
                    .sameSite("None")
                    .build();

            jwtAuthenticationCookieResponse.setTokenCookie(jwtCookie);
            jwtAuthenticationCookieResponse.setRefreshCookie(refreshCookie);

            return jwtAuthenticationCookieResponse;
        }
        return null;
    }

    @Override
    public UserResponse getCurrentUser(String token) {

        String userEmail = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Profile userProfile = profileRepository.findProfileByUserId(user.getId()).orElseThrow();
        Integer unreadMessages = messageService.getUnreadMessages();

        if(jwtService.isTokenValid(token, user)) {
            UserResponse userResponse = new UserResponse();

            userResponse.setUserId(user.getId());
            userResponse.setUsername(user.getName());
            userResponse.setEmail(userEmail);
            if (userProfile.getGender() != null) {
                userResponse.setGender(userProfile.getGender().getDisplayName());
            }
            userResponse.setCountry(userProfile.getCountry() != null ? userProfile.getCountry() : null);
            userResponse.setRegion(userProfile.getRegion() != null ? userProfile.getRegion() : null);
            userResponse.setDateOfBirth(userProfile.getDateOfBirth() != null ? String.valueOf(userProfile.getDateOfBirth()) : null);
            userResponse.setRelation(userProfile.getRelation() != null ? userProfile.getRelation().getDisplayName() : null);
            userResponse.setBio(userProfile.getBio());
            userResponse.setImageUrl(userProfile.getImageUrl());
            userResponse.setUnreadMessages(unreadMessages);

            return userResponse;
        }

        return null;

    }

    @Override
    public JwtAuthenticationCookieResponse logout() {

        ResponseCookie jwtCookie = ResponseCookie.from("CVMJWT", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("CVMRefresh", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        JwtAuthenticationCookieResponse jwtAuthenticationCookieResponse = new JwtAuthenticationCookieResponse();

        jwtAuthenticationCookieResponse.setTokenCookie(jwtCookie);
        jwtAuthenticationCookieResponse.setRefreshCookie(refreshCookie);

        return jwtAuthenticationCookieResponse;
    }
}