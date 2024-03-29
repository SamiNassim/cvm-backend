package com.saminassim.cvm.repository;

import com.saminassim.cvm.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findProfileByUserId(String userId);
    List<Profile> findAllByCountry(String country);
    List<Profile> findAllByRegion(String region);
}
