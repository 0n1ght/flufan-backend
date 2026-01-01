package com.flufan.repo;

import com.flufan.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHashAndUsedFalse(String tokenHash);

    Optional<RefreshToken> findByTokenHashAndUsedTrue(String tokenHash);

    void deleteAllByAccount_Id(Long accountId);

    void deleteAllByExpirationDateBefore(Instant now);
}
