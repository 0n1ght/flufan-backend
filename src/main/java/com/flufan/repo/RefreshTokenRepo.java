package com.flufan.repo;

import com.flufan.entity.Account;
import com.flufan.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHashAndUsedFalse(String tokenHash);

    void deleteAllByExpirationDateBefore(Instant now);

    long countByAccountAndUsedFalse(Account account);
}
