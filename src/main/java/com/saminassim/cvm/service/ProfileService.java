package com.saminassim.cvm.service;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.dto.response.UserResponse;
import com.saminassim.cvm.entity.Profile;

public interface ProfileService {
    Profile modifyProfile(ProfileRequest profileRequest, String userId);
    UserResponse getProfile(String userId);
}
