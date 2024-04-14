package com.saminassim.cvm.service;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.dto.response.UserResponse;
import com.saminassim.cvm.entity.Profile;

import java.util.List;

public interface ProfileService {
    Profile modifyProfile(ProfileRequest profileRequest, String userId);
    UserResponse getProfile(String userId);
    List<UserResponse> getLikedBy();
    List<UserResponse> searchByCountry(String country);
    List<UserResponse> searchByRegion(String region);
}
