package com.flufan.service.impl;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.entity.PasswordResetToken;
import com.flufan.exception.*;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.PasswordResetTokenRepo;
import com.flufan.repo.SuspendedAccountRepo;
import com.flufan.service.AccountService;
import com.flufan.dto.RegisterDto;
import com.flufan.service.JWTService;
import com.flufan.service.MailSenderService;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final SuspendedAccountRepo suspendedAccountRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final MailSenderService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepo tokenRepo;

    public AccountServiceImpl(AccountRepo accountRepo, SuspendedAccountRepo suspendedAccountRepo,
                              @Lazy AuthenticationManager authManager, JWTService jwtService,
                              MailSenderService mailService, PasswordEncoder passwordEncoder,
                              PasswordResetTokenRepo tokenRepo) {
        this.accountRepo = accountRepo;
        this.suspendedAccountRepo = suspendedAccountRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findByEmailIgnoreCase(username);
        if (account.isPresent()) {
            var accountObj = account.get();

            User.UserBuilder builder = User.builder()
                    .username(accountObj.getEmail())
                    .password(accountObj.getPassword());

            if (accountObj.isVerifiedEmail()) {
                builder.roles("VERIFIED_USER");
            }

            return builder.build();
        }
        throw new UsernameNotFoundException(username);
    }


    @Override
    public Account saveAccount(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        validateUsername(username);

        if (accountRepo.findByEmailIgnoreCase(registerDto.getEmail()).isPresent() ||
                suspendedAccountRepo.findByEmailIgnoreCase(registerDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        } else if (accountRepo.findByUsernameIgnoreCase(username).isPresent() ||
                suspendedAccountRepo.findByUsernameIgnoreCase(username).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }

        return accountRepo.save(new Account(
                username,
                registerDto.getEmail(),
                passwordEncoder.encode(registerDto.getPassword())
        ));
    }

    @Override
    public void deleteAccount(String password) {
        Account account = getAuthenticatedAccount();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new AccessDeniedException("Incorrect password");
        }

        account.setDeletedAt(LocalDateTime.now());
        accountRepo.save(account);
    }

    @Override
    public Map<String, Object> verify(LoginDto loginDto) {
        try {
            Account account;

            if (loginDto.getEmail() != null) {
                account = accountRepo.findByEmailIgnoreCase(loginDto.getEmail())
                        .orElseThrow(() -> new AccountNotFoundException("Account with this email does not exist"));
            } else {
                account = accountRepo.findByUsernameIgnoreCase(loginDto.getUsername())
                        .orElseThrow(() -> new AccountNotFoundException("Account with this username does not exist"));
            }

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            account.getEmail(),
                            loginDto.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                throw new IncorrectPasswordException();
            }

            if (account.getDeletedAt() != null) {
                account.setDeletedAt(null);
                accountRepo.save(account);
            }

            String token = jwtService.generateToken(account.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("account", account);

            return response;

        } catch (BadCredentialsException e) {
            throw new IncorrectPasswordException();
        } catch (AccountNotFoundException | IncorrectPasswordException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericServiceException();
        }
    }

    @Override
    public Account findByPublicId(UUID publicId) {
        return accountRepo.findByPublicId(publicId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepo.findByUsernameIgnoreCase(username).orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public void updateUsername(String newUsername) {
        Account authenticatedAccount = getAuthenticatedAccount();

        if (authenticatedAccount.getLastUsernameChange() != null) {
            long daysSince = ChronoUnit.DAYS.between(authenticatedAccount.getLastUsernameChange(), LocalDateTime.now());
            if (daysSince < 14) {
                long daysLeft = 14 - daysSince;
                if (daysLeft > 1) throw new IllegalArgumentException("Username can be changed in " + daysLeft + " days");
                throw new IllegalArgumentException("Username can be changed in 1 day");
            }
        }

        if (accountRepo.findByUsernameIgnoreCase(newUsername).isPresent()
                && !authenticatedAccount.getUsername().equals(newUsername)) {
            throw new IllegalArgumentException("Username is taken");
        }

        authenticatedAccount.setUsername(newUsername);
        authenticatedAccount.setLastUsernameChange(LocalDateTime.now());

        accountRepo.save(authenticatedAccount);
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        Account authenticatedAccount = getAuthenticatedAccount();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(oldPassword, authenticatedAccount.getPassword())) {
            throw new AccessDeniedException("Incorrect password");
        }

        authenticatedAccount.setPassword(passwordEncoder.encode(newPassword));

        accountRepo.save(authenticatedAccount);
    }

    @Transactional
    @Override
    public void updateAccount(Account account) {
        accountRepo.save(account);
    }

    @Override
    public void verifyEmailUpdateRequest(String password, String newEmail) {
        Account authenticatedAccount = getAuthenticatedAccount();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, authenticatedAccount.getPassword())) {
            throw new AccessDeniedException("Incorrect password");
        }

        if (accountRepo.findByEmailIgnoreCase(newEmail).isPresent()
                && !authenticatedAccount.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }

    @Override
    public Account loadOrCreateGoogleUser(String email) {
        Optional<Account> existing = accountRepo.findByEmailIgnoreCase(email);

        if (existing.isPresent()) {
            return existing.get();
        }

        Account newAccount = new Account();
        newAccount.setEmail(email);
        newAccount.setUsername(email.split("@")[0]);

        String randomPwd = UUID.randomUUID().toString();
        newAccount.setPassword(passwordEncoder.encode(randomPwd));

        newAccount.setVerifiedEmail(true);

        accountRepo.save(newAccount);

        return newAccount;
    }

    @Transactional
    @Override
    public String requestPasswordReset(String login) {
        Optional<Account> optionalAccount = login.contains("@") ?
                accountRepo.findByEmailIgnoreCase(login) : accountRepo.findByUsernameIgnoreCase(login);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();

            tokenRepo.deleteByAccount(account);

            PasswordResetToken resetToken =
                    generatePasswordResetToken(account);
            tokenRepo.save(resetToken);

            try {
                mailService.sendPasswordResetEmail(
                        account.getEmail(),
                        account.getUsername(),
                        resetToken.getToken()
                );
                return account.getEmail();
            } catch (Exception e) {
                log.warn("Failed to send password reset email to {}", login, e);
                throw new AccountNotFoundException("Problems appeared during resetting password");
            }
        } else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (resetToken.isExpired()) {
            throw new TokenExpiredException();
        }

        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepo.save(account);

        tokenRepo.delete(resetToken);
    }

    @Override
    public boolean verifyPasswordResetToken(String token) {
        return tokenRepo.existsByToken(token);
    }

    @Override
    public Account getAuthenticatedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return accountRepo.findByEmailIgnoreCase(email)
                .orElseThrow(AuthenticatedAccountNotFoundException::new);
    }

    @Override
    public void authenticatePassword(String password) {
        String authAccPassword = getAuthenticatedAccount().getPassword();
        if (!passwordEncoder.matches(password, authAccPassword)) {
            throw new IncorrectPasswordException();
        }
    }

    private void validateUsername(String username) {
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }

        if (username.length() > 20) {
            throw new IllegalArgumentException("Username must not exceed 20 characters");
        }

        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("Username contains invalid characters. Allowed: letters, digits, underscore");
        }
    }

    private PasswordResetToken generatePasswordResetToken(Account account) {
        PasswordResetToken resetToken = new PasswordResetToken();

        String token;
        do {
            token = UUID.randomUUID().toString();
        } while(tokenRepo.existsByToken(token));

        resetToken.setToken(token);
        resetToken.setAccount(account);

        return resetToken;
    }
}
