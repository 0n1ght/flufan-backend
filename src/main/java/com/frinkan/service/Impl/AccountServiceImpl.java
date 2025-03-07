package com.frinkan.service.Impl;

import com.frinkan.dto.LoginDto;
import com.frinkan.entity.Account;
import com.frinkan.repo.AccountRepo;
import com.frinkan.service.AccountService;
import com.frinkan.dto.RegisterDto;
import com.frinkan.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountServiceImpl(AccountRepo accountRepo, @Lazy AuthenticationManager authManager,
                              JWTService jwtService, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
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
        Optional<Account> existingAccount = accountRepo.findByEmail(accountDto.getEmail());
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        accountRepo.save(new Account(accountDto.getUsername(), accountDto.getEmail(), passwordEncoder.encode(accountDto.getPassword())));
    }

    @Override
    public Account getAuthenticatedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Pobranie emaila zalogowanego użytkownika

        return accountRepo.findByEmail(username)
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
        // Sprawdzam czy nowy email jest już w repozytorium przypisany do innego konta niż użytkownika
        Optional<Account> existingAccount = accountRepo.findByEmail(loginDto.getEmail());
        Account authenticatedAccount = getAuthenticatedAccount(); // Pobieram aktualnie zalogowanego użytkownika

        if (existingAccount.isPresent() && !existingAccount.get().getId().equals(authenticatedAccount.getId())) {
            // Jeśli email już istnieje i nie należy do aktualnie zalogowanego użytkownika
            throw new IllegalArgumentException("Email is already in use");
        }

        // Pobieram konto z bazy danych, które chcemy zaktualizować
        Account accountToUpdate = accountRepo.findByEmail(authenticatedAccount.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Podmieniam email i hasło na nowe dane z LoginDto
        accountToUpdate.setEmail(loginDto.getEmail());
        accountToUpdate.setPassword(passwordEncoder.encode(loginDto.getPassword())); // Pamiętaj o zakodowaniu hasła

        // Zapisuję zmodyfikowane konto spowrotem do bazy danych
        accountRepo.save(accountToUpdate);
    }
}
