package com.frinkan.service.impl;

import com.frinkan.dto.LoginDto;
import com.frinkan.entity.Account;
import com.frinkan.exception.ResourceNotFoundException;
import com.frinkan.repo.AccountRepo;
import com.frinkan.repo.BannedAccountRepo;
import com.frinkan.repo.SuspendedAccountRepo;
import com.frinkan.service.AccountService;
import com.frinkan.dto.RegisterDto;
import com.frinkan.service.JWTService;
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
            return User.builder()
                    .username(accountObj.getEmail())
                    .password(accountObj.getPassword())
                    .roles("USER")
                    .build();
        }
        throw new UsernameNotFoundException(username);
    }

    @Override
    public void saveAccount(RegisterDto accountDto) {

        if (accountRepo.findByEmail(accountDto.getEmail()).isPresent() ||
                suspendedAccountRepo.findByEmail(accountDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        } else if (accountRepo.findByUsername(accountDto.getUsername()).isPresent() ||
                suspendedAccountRepo.findByUsername(accountDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        } else if (bannedAccountRepo.findByEmail(accountDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Account is banned");
        }

        accountRepo.save(new Account(accountDto.getUsername(), accountDto.getEmail(), passwordEncoder.encode(accountDto.getPassword())));
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
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginDto.getEmail());
        }

        return "Fail";
    }

    @Override
    public Account getById(Long id) {
        return accountRepo.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public void changeLoginData(LoginDto loginDto) {
        Optional<Account> existingAccount = accountRepo.findByEmail(loginDto.getEmail());
        Account authenticatedAccount = getAuthenticatedAccount();

        if (existingAccount.isPresent() && !existingAccount.get().getId().equals(authenticatedAccount.getId())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Account accountToUpdate = accountRepo.findByEmail(authenticatedAccount.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!accountToUpdate.getEmail().equals(loginDto.getEmail())) {
            accountToUpdate.setVerifiedEmail(false);
        }
        accountToUpdate.setEmail(loginDto.getEmail());
        accountToUpdate.setPassword(passwordEncoder.encode(loginDto.getPassword()));

        accountRepo.save(accountToUpdate);
    }

    @Override
    public void updateAccount(Account account) {
        accountRepo.save(account);
    }

    @Override
    public void verifyAccountEmail(String email) {
        accountRepo.findByEmail(email).orElseThrow().setVerifiedEmail(true);
    }

    @Override
    public Account findAccountByEmail(String email) {
        return accountRepo.findByEmail(email).orElseThrow();
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
