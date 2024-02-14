package com.saminassim.cvm.controller;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.exception.ProfileNotFoundException;
import com.saminassim.cvm.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> createProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            return ResponseEntity.ok(profileService.createProfile(profileRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable String id){
        try {
            return ResponseEntity.ok(profileService.getProfile(id));
        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> modifyProfile(@RequestBody ProfileRequest profileRequest) {
            return ResponseEntity.ok(profileService.modifyProfile(profileRequest));
    }

}
