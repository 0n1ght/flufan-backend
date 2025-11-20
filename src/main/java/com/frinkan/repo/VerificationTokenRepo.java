package com.frinkan.repo;

import com.frinkan.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {
    boolean existsByToken(String token);
    VerificationToken findByEmail(String email);
    void deleteByEmail(String email);
}
