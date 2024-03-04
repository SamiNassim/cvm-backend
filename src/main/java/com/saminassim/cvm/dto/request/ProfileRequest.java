package com.saminassim.cvm.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ProfileRequest {
    private String gender;
    private String country;
    private String region;
    private String dateOfBirth;
    private String relation;
    private String bio;
    private MultipartFile image;
}
