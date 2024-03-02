package com.saminassim.cvm.dto.request;

import com.saminassim.cvm.entity.Gender;
import com.saminassim.cvm.entity.Relation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileRequest {
    private Gender gender;
    private String country;
    private String region;
    private LocalDateTime dateOfBirth;
    private Relation relation;
    private String bio;
}
