package com.saminassim.cvm.service;

import com.saminassim.cvm.dto.request.LoginRequest;
import com.saminassim.cvm.dto.request.RegisterRequest;
import com.saminassim.cvm.dto.response.JwtAuthenticationCookieResponse;
import com.saminassim.cvm.entity.User;

public interface AuthenticationService {
    User register(RegisterRequest registerRequest);
    JwtAuthenticationCookieResponse login(LoginRequest loginRequest);
    JwtAuthenticationCookieResponse refreshToken(String refreshToken);
}
