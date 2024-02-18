package com.saminassim.cvm.dto.response;

import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
public class JwtAuthenticationCookieResponse {

    private ResponseCookie tokenCookie;
    private ResponseCookie refreshCookie;

}
