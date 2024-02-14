package com.saminassim.cvm.service;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.entity.Profile;


public interface ProfileService {

    Profile createProfile(ProfileRequest profileRequest);
    Profile modifyProfile(ProfileRequest profileRequest);
    Profile getProfile(String userId);
}
