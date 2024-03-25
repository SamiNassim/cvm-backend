package com.saminassim.cvm.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private String username;
    private String email;
    private String gender;
    private String country;
    private String region;
    private String dateOfBirth;
    private String relation;
    private String bio;
    private String imageUrl;
}
