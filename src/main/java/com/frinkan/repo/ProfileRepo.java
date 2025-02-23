package com.frinkan.repo;

import com.frinkan.dto.ProfileDto;
import com.frinkan.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends JpaRepository<Profile, Long> {
    void saveProfile(ProfileDto profileDto);
}
