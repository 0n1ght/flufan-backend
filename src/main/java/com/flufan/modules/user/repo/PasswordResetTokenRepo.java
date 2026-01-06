package com.flufan.modules.user.repo;

import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByAccount(Account account);
}
