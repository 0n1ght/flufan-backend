package com.flufan.service.impl;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.exception.ResourceNotFoundException;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.BannedAccountRepo;
import com.flufan.repo.SuspendedAccountRepo;
import com.flufan.service.AccountService;
import com.flufan.dto.RegisterDto;
import com.flufan.service.JWTService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final BannedAccountRepo bannedAccountRepo;
    private final SuspendedAccountRepo suspendedAccountRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepo accountRepo, BannedAccountRepo bannedAccountRepo,
                              SuspendedAccountRepo suspendedAccountRepo, @Lazy AuthenticationManager authManager,
                              JWTService jwtService, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        this.bannedAccountRepo = bannedAccountRepo;
        this.suspendedAccountRepo = suspendedAccountRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findByEmail(username);
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

        if (accountRepo.findByEmail(accountDto.getEmail()).isPresent() ||
                suspendedAccountRepo.findByEmail(accountDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        } else if (accountRepo.findByUsername(accountDto.getUsername()).isPresent() ||
                suspendedAccountRepo.findByUsername(accountDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        } else if (bannedAccountRepo.findByEmail(accountDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Account is banned");
        }

        return accountRepo.save(new Account(accountDto.getUsername(), accountDto.getEmail(), passwordEncoder.encode(accountDto.getPassword())));
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepo.save(account);
    }

    @Override
    public void deleteAccount(LoginDto loginDto) {
        Optional<Account> account = accountRepo.findByEmail(loginDto.getEmail());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (account.isEmpty()) {
            throw new ResourceNotFoundException("Account with the given email not found");
        }

        if (!authentication.getName().equals(account.get().getEmail())) {
            throw new AccessDeniedException("You are not authorized to delete this account");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginDto.getPassword(), account.get().getPassword())) {
            throw new AccessDeniedException("Incorrect password");
        }

        accountRepo.delete(account.get());
    }

    @Override
    public Account getAuthenticatedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return accountRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @Override
    public String verify(LoginDto loginDto) {

        String email;
        if (loginDto.getEmail() != null) {
            email = loginDto.getEmail();
        } else {
            email = accountRepo.findByUsername(loginDto.getUsername()).orElseThrow().getEmail();
        }

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginDto.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(email);
        }

        return "Fail";
    }

    @Override
    public Account findById(Long id) {
        return accountRepo.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Account not found"));
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

        if (accountRepo.findByUsername(newUsername).isPresent()
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

        if (accountRepo.findByEmail(newEmail).isPresent()
                && !authenticatedAccount.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }

    @Override
    public Account loadOrCreateGoogleUser(String email) {
        Optional<Account> existing = accountRepo.findByEmail(email);

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
}
