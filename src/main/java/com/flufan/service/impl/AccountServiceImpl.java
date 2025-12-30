package com.flufan.service.impl;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.entity.PasswordResetToken;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.BannedAccountRepo;
import com.flufan.repo.PasswordResetTokenRepo;
import com.flufan.repo.SuspendedAccountRepo;
import com.flufan.service.AccountService;
import com.flufan.dto.RegisterDto;
import com.flufan.service.JWTService;
import com.flufan.service.MailSenderService;
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
import java.util.Optional;
import java.util.UUID;

@Service
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
    public Account saveAccount(RegisterDto accountDto) {
        String username = accountDto.getUsername();

        if (username.length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters long.");
        }

        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("Username contains invalid characters. Allowed: letters, digits, underscore.");
        }

        if (accountRepo.findByEmailIgnoreCase(accountDto.getEmail()).isPresent() ||
                suspendedAccountRepo.findByEmailIgnoreCase(accountDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        } else if (accountRepo.findByUsernameIgnoreCase(username).isPresent() ||
                suspendedAccountRepo.findByUsernameIgnoreCase(username).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }

        return accountRepo.save(new Account(
                username,
                accountDto.getEmail(),
                passwordEncoder.encode(accountDto.getPassword())
        ));
    }

    @Override
    public void saveAccount(Account account) {
        accountRepo.save(account);
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
    public String verify(LoginDto loginDto) {
        try {
            Account account;

            if (loginDto.getEmail() != null) {
                account = accountRepo.findByEmailIgnoreCase(loginDto.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("Account with this email does not exist"));
            } else {
                account = accountRepo.findByUsernameIgnoreCase(loginDto.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("Account with this username does not exist"));
            }

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            account.getEmail(),
                            loginDto.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                throw new IllegalArgumentException("Incorrect password");
            }

            if (account.getDeletedAt() != null) {
                account.setDeletedAt(null);
                accountRepo.save(account);
            }

            return jwtService.generateToken(account.getEmail());

        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Incorrect password");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred. Please try again later");
        }
    }


    @Override
    public Account findById(Long id) {
        return accountRepo.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepo.findByUsernameIgnoreCase(username).orElseThrow(() -> new RuntimeException("Account not found"));
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

    @Override
    public void requestPasswordReset(String email) {
        Account account = accountRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        PasswordResetToken resetToken =
                generatePasswordResetToken(account, LocalDateTime.now().plusHours(1));
        tokenRepo.save(resetToken);

        try {
            mailService.sendPasswordResetEmail(account.getEmail(), account.getUsername(), resetToken.getToken());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expired");
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
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    private PasswordResetToken generatePasswordResetToken(Account account, LocalDateTime expiryDate) {
        PasswordResetToken resetToken = new PasswordResetToken();

        String token;
        do {
            token = UUID.randomUUID().toString();
        } while(tokenRepo.existsByToken(token));

        resetToken.setToken(token);
        resetToken.setAccount(account);
        resetToken.setExpiryDate(expiryDate);

        return resetToken;
    }
}
