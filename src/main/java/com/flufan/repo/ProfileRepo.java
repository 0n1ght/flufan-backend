package com.flufan.repo;

import com.flufan.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepo extends JpaRepository<Profile, Long> {

    Optional<Profile> findById(Long id);

    Optional<Profile> findByNick(String nick);

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN p.linkedAccounts la WHERE " +
            "p.active = true AND (" +
            "LOWER(p.nick) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :searchVal, '%')) OR " +
            "LOWER(la.identifier) LIKE LOWER(CONCAT('%', :searchVal, '%'))" +
            ") ORDER BY " +
            "CASE WHEN LOWER(p.nick) = LOWER(:searchVal) THEN 1 ELSE 0 END DESC, " +
            "p.interactionCounter DESC, " +
            "p.rating DESC, " +
            "p.respondTime ASC")
    List<Profile> searchByNickOrName(String searchVal);
}
