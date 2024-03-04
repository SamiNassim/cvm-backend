package com.saminassim.cvm.service;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.entity.Profile;

public interface ProfileService {
    Profile modifyProfile(ProfileRequest profileRequest, String userId);
    Profile getProfile(String userId);
}
