package com.saminassim.cvm.dto.request;

import com.saminassim.cvm.entity.Gender;
import lombok.Data;

@Data
public class ProfileRequest {
    private String userId;
    private Gender gender;
    private String country;
}
