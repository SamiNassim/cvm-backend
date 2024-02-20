package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.dto.request.LoginRequest;
import com.saminassim.cvm.dto.request.RegisterRequest;
import com.saminassim.cvm.dto.response.JwtAuthenticationCookieResponse;
import com.saminassim.cvm.entity.Role;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.exception.UserAlreadyExistsException;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.AuthenticationService;
import com.saminassim.cvm.service.JWTService;
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

    public User register(RegisterRequest registerRequest) {

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists.");
        }

        User user = new User();

        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

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
//// Old method without cookies
//    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
//        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
//        User user = userRepository.findByEmail(userEmail).orElseThrow();
//
//        if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
//            var jwt = jwtService.generateToken(user);
//
//            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
//
//            jwtAuthenticationResponse.setToken(jwt);
//            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
//
//            return jwtAuthenticationResponse;
//        }
//        return null;
//    }
}