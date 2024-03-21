package com.saminassim.cvm.controller;

import com.saminassim.cvm.dto.request.ProfileRequest;
import com.saminassim.cvm.exception.ProfileNotFoundException;
import com.saminassim.cvm.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class ProfileController {

    private final ProfileService profileService;
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getProfile(@PathVariable String id){
        try {
            return ResponseEntity.ok(profileService.getProfile(id));
        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> modifyProfile(@ModelAttribute ProfileRequest profileRequest, @CookieValue("CVMJWT") String token) {
            return ResponseEntity.ok(profileService.modifyProfile(profileRequest, token));
    }

    @GetMapping("/search/country")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> searchByCountry(@RequestBody String country){
        try {
            return ResponseEntity.ok(profileService.searchByCountry(country));
        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(404).body("Aucun résultat disponible.");
        }
    }

    @GetMapping("/search/region")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> searchByRegion(@RequestBody String region){
        try {
            return ResponseEntity.ok(profileService.searchByRegion(region));
        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(404).body("Aucun résultat disponible.");
        }
    }

}
