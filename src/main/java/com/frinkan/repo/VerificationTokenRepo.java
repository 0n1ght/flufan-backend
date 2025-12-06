package com.frinkan.repo;

import com.frinkan.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {
    boolean existsByToken(String token);
    List<VerificationToken> findAllByEmail(String email);
}
