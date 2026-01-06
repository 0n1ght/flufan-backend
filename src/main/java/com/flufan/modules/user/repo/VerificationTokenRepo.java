package com.flufan.modules.user.repo;

import com.flufan.modules.user.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {

    boolean existsByToken(String token);

    Optional<VerificationToken> findByToken(String token);
}
