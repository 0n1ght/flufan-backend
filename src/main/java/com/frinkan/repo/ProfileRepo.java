package com.frinkan.repo;

import com.frinkan.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepo extends JpaRepository<Profile, Long> {
    Optional<Profile> findByNick(String nick);
    @Query("SELECT p FROM Profile p WHERE " +
            "LOWER(p.nick) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :searchVal, '%'))")
    List<Profile> searchByNickOrName(String searchVal);
}
