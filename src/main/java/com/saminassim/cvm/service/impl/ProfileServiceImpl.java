package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.entity.Profile;
import com.saminassim.cvm.exception.ProfileCannotBeModifiedException;
import com.saminassim.cvm.exception.ProfileNotFoundException;
import com.saminassim.cvm.repository.ProfileRepository;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.JWTService;
import com.saminassim.cvm.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JWTService jwtService;
    @Override
    public Profile modifyProfile(ProfileRequest profileRequest, String token) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();
        String currentAuthUserName = currentUser.getUsername();

        String currentTokenUserName = jwtService.extractUserName(token);

        if(!currentAuthUserName.equals(currentTokenUserName)){
            throw new ProfileCannotBeModifiedException("You can't modify this profile");
        }

        if(currentUser.getProfile() == null) {
            throw new ProfileNotFoundException("This profile doesn't exist");
        }

        Profile currentUserProfile = profileRepository.findProfileByUserId(currentUser.getId()).orElseThrow();

        currentUserProfile.setGender(profileRequest.getGender());
        currentUserProfile.setCountry(profileRequest.getCountry());
        currentUserProfile.setRegion(profileRequest.getRegion());
        currentUserProfile.setDateOfBirth(profileRequest.getDateOfBirth());
        currentUserProfile.setRelation(profileRequest.getRelation());
        currentUserProfile.setBio(profileRequest.getBio());

        return profileRepository.save(currentUserProfile);
    }

    @Override
    public Profile getProfile(String userId) {
        return profileRepository.findProfileByUserId(userId).orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
    }
}
