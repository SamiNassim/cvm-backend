package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.entity.Profile;
import com.saminassim.cvm.exception.ProfileAlreadyExistsException;
import com.saminassim.cvm.exception.ProfileCannotBeModifiedException;
import com.saminassim.cvm.exception.ProfileNotFoundException;
import com.saminassim.cvm.repository.ProfileRepository;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    @Override
    public Profile createProfile(ProfileRequest profileRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        String currentUserId = userRepository.findByEmail(authentication.getName()).orElseThrow().getId();

        if(!currentUserId.equals(profileRequest.getUserId())){
            throw new IllegalArgumentException("You can't create this profile");
        }

        if(currentUser.orElseThrow().getProfile() != null){
            throw new ProfileAlreadyExistsException("This user profile already exists");
        }

        Profile newProfile = new Profile();

        newProfile.setGender(profileRequest.getGender());
        newProfile.setCountry(profileRequest.getCountry());
        newProfile.setUser(currentUser.orElseThrow());
        currentUser.orElseThrow().setProfile(newProfile);
        userRepository.save(currentUser.orElseThrow());

        return newProfile;
    }

    @Override
    public Profile modifyProfile(ProfileRequest profileRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        String currentUserId = userRepository.findByEmail(authentication.getName()).orElseThrow().getId();

        if(!currentUserId.equals(profileRequest.getUserId())){
            throw new ProfileCannotBeModifiedException("You can't modify this profile");
        }

        if(currentUser.orElseThrow().getProfile() == null) {
            throw new ProfileNotFoundException("This profile doesn't exist");
        }

        Profile currentUserProfile = profileRepository.findProfileByUserId(currentUserId).orElseThrow();

        currentUserProfile.setCountry(profileRequest.getCountry());
        currentUserProfile.setGender(profileRequest.getGender());

        return profileRepository.save(currentUserProfile);
    }

    @Override
    public Profile getProfile(String userId) {
        return profileRepository.findProfileByUserId(userId).orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
    }
}
