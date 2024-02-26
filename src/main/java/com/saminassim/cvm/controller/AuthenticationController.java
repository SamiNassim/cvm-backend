package com.saminassim.cvm.controller;

import com.saminassim.cvm.dto.request.LoginRequest;
import com.saminassim.cvm.dto.request.RegisterRequest;
import com.saminassim.cvm.dto.response.JwtAuthenticationCookieResponse;
import com.saminassim.cvm.dto.response.UserResponse;
import com.saminassim.cvm.exception.UserAlreadyExistsException;
import com.saminassim.cvm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            return ResponseEntity.ok(authenticationService.register(registerRequest));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, authenticationService.login(loginRequest).getRefreshCookie().toString(),authenticationService.login(loginRequest).getTokenCookie().toString()).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationCookieResponse> refresh(@CookieValue("CVMRefresh") String refreshToken) {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, authenticationService.refreshToken(refreshToken).getRefreshCookie().toString(), authenticationService.refreshToken(refreshToken).getTokenCookie().toString()).build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getCurrentUser(@CookieValue("CVMJWT") String token) {
        return ResponseEntity.ok(authenticationService.getCurrentUser(token));
    }

    @GetMapping("/logout")
    public ResponseEntity<JwtAuthenticationCookieResponse> logout() {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, authenticationService.logout().getRefreshCookie().toString(), authenticationService.logout().getTokenCookie().toString()).build();
    }
}
