package com.saminassim.cvm.dto.response;

import com.saminassim.cvm.entity.Gender;
import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private String email;
    private Gender gender;
    private String country;
}
