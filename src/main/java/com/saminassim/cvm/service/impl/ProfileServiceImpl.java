package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.dto.response.UserResponse;
import com.saminassim.cvm.entity.Gender;
import com.saminassim.cvm.entity.Relation;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.entity.Profile;
import com.saminassim.cvm.exception.PremiumFeatureException;
import com.saminassim.cvm.exception.ProfileCannotBeModifiedException;
import com.saminassim.cvm.exception.ProfileNotFoundException;
import com.saminassim.cvm.repository.ProfileRepository;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.JWTService;
import com.saminassim.cvm.service.ProfileService;
import com.saminassim.cvm.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.saminassim.cvm.entity.Role.PREMIUM;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JWTService jwtService;
    private final StorageService storageService;
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

        currentUserProfile.setGender(Gender.fromDisplayName(profileRequest.getGender()));
        currentUserProfile.setCountry(profileRequest.getCountry());
        currentUserProfile.setRegion(profileRequest.getRegion());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
        currentUserProfile.setDateOfBirth(LocalDateTime.parse(profileRequest.getDateOfBirth(), formatter));
        currentUserProfile.setRelation(Relation.fromDisplayName(profileRequest.getRelation()));
        currentUserProfile.setBio(profileRequest.getBio());

        if(profileRequest.getImage() != null && currentUserProfile.getImageUrl() == null) {
            storageService.store(profileRequest.getImage());
            currentUserProfile.setImageUrl("http://localhost:8080/images/" + profileRequest.getImage().getOriginalFilename());
        }

        if(profileRequest.getImage() != null && currentUserProfile.getImageUrl() != null) {
            String oldFilename = currentUserProfile.getImageUrl().substring(29);
            storageService.deleteFile(oldFilename);
            storageService.store(profileRequest.getImage());
            currentUserProfile.setImageUrl("http://localhost:8080/images/" + profileRequest.getImage().getOriginalFilename());
        }

        return profileRepository.save(currentUserProfile);
    }

    @Override
    public UserResponse getProfile(String userId) {

        Profile selectedProfile = profileRepository.findProfileByUserId(userId).orElseThrow();

        UserResponse userResponse = new UserResponse();

        userResponse.setUserId(selectedProfile.getUser().getId());
        userResponse.setUsername(selectedProfile.getUser().getUsername());
        userResponse.setEmail(selectedProfile.getUser().getEmail());
        if (selectedProfile.getGender() != null) {
            userResponse.setGender(selectedProfile.getGender().getDisplayName());
        }
        userResponse.setCountry(selectedProfile.getCountry() != null ? selectedProfile.getCountry() : null);
        userResponse.setRegion(selectedProfile.getRegion() != null ? selectedProfile.getRegion() : null);
        userResponse.setDateOfBirth(selectedProfile.getDateOfBirth() != null ? String.valueOf(selectedProfile.getDateOfBirth()) : null);
        userResponse.setRelation(selectedProfile.getRelation() != null ? selectedProfile.getRelation().getDisplayName() : null);
        userResponse.setBio(selectedProfile.getBio());
        userResponse.setImageUrl(selectedProfile.getImageUrl());

        return userResponse;

    }

    @Override
    public List<UserResponse> getLikedBy() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();

        List<User> userLikedBy = currentUser.getLikedBy();
        List<UserResponse> userLikedByResponse = new ArrayList<>();
        
        /* This feature could be a paid feature in the future.
        
        if(currentUser.getRole() != PREMIUM){
            throw new PremiumFeatureException("You are not a premium member.");
        }
        
        */

        for(User user : userLikedBy) {
            UserResponse newUserResponse = new UserResponse();
            newUserResponse.setUserId(user.getId());
            newUserResponse.setUsername(user.getName());
            if (user.getProfile().getGender() != null) {
                newUserResponse.setGender(user.getProfile().getGender().getDisplayName());
            }
            newUserResponse.setCountry(user.getProfile().getCountry() != null ? user.getProfile().getCountry() : null);
            newUserResponse.setRegion(user.getProfile().getRegion() != null ? user.getProfile().getRegion() : null);
            newUserResponse.setDateOfBirth(user.getProfile().getDateOfBirth() != null ? String.valueOf(user.getProfile().getDateOfBirth()) : null);
            newUserResponse.setRelation(user.getProfile().getRelation() != null ? user.getProfile().getRelation().getDisplayName() : null);
            newUserResponse.setBio(user.getProfile().getBio());
            newUserResponse.setImageUrl(user.getProfile().getImageUrl());

            userLikedByResponse.add(newUserResponse);
        }

        return userLikedByResponse;
    }

    @Override
    public List<UserResponse> searchByCountry(String country) {

        List<Profile> dbResult = profileRepository.findAllByCountry(country);

        return searchBy(dbResult);

    }

    @Override
    public List<UserResponse> searchByRegion(String region) {

        List<Profile> dbResult = profileRepository.findAllByRegion(region);

        return searchBy(dbResult);
    }

    private List<UserResponse> searchBy(List<Profile> dbResult) {
        List<UserResponse> searchResult = new ArrayList<>();

        for(Profile profile : dbResult) {
            UserResponse userResponse = new UserResponse();

            userResponse.setUserId(profile.getUser().getId());
            userResponse.setUsername(profile.getUser().getUsername());
            userResponse.setEmail(profile.getUser().getEmail());
            if (profile.getGender() != null) {
                userResponse.setGender(profile.getGender().getDisplayName());
            }
            userResponse.setCountry(profile.getCountry() != null ? profile.getCountry() : null);
            userResponse.setRegion(profile.getRegion() != null ? profile.getRegion() : null);
            userResponse.setDateOfBirth(profile.getDateOfBirth() != null ? String.valueOf(profile.getDateOfBirth()) : null);
            userResponse.setRelation(profile.getRelation() != null ? profile.getRelation().getDisplayName() : null);
            userResponse.setBio(profile.getBio());
            userResponse.setImageUrl(profile.getImageUrl());

            searchResult.add(userResponse);

        }

        return searchResult;
    }
}
