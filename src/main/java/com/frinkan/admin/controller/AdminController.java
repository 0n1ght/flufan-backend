package com.frinkan.admin.controller;

import com.frinkan.admin.service.AdminService;
import com.frinkan.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/accounts")
    public List<AccountDto> getAccounts(@RequestParam(required = false) Boolean banned) {
        if (Boolean.TRUE.equals(banned)) {
            return adminService.getAllBannedAccounts();
        }
        return adminService.getAllAccounts();
    }

    @GetMapping("/accounts/{id}")
    public AccountDto getAccountById(@PathVariable Long id,
                                     @RequestParam(required = false) Boolean banned) {
        if (Boolean.TRUE.equals(banned)) {
            return adminService.getBannedAccountById(id);
        }
        return adminService.getAccountById(id);
    }

    @PostMapping("/accounts/{id}/ban")
    public void banAccount(@PathVariable Long id) {
        adminService.banAccount(id);
    }


    @PostMapping("/accounts/{id}/unban")
    public void unbanAccount(@PathVariable Long id) {
        adminService.unbanAccount(id);
    }

    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable Long id) {
        adminService.deleteAccount(id);
    }

    @PostMapping("/accounts/{id}/reset-password")
    public void sendResetPassword(@PathVariable Long id) {
        adminService.sendResetPassword(id);
    }

    @GetMapping("/profiles/{accountId}")
    public ProfileResDto getProfileByAccountId(@PathVariable Long accountId) {
        return adminService.getProfileByAccountId(accountId);
    }

    @GetMapping("/messages")
    public List<MessageDto> getAllMessages(@RequestParam long acc1Id,
                                           @RequestParam long acc2Id,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return adminService.getAllMessages(acc1Id, acc2Id, page, size);
    }

    @DeleteMapping("/messages/{id}")
    public void deleteMessage(@PathVariable Long id) {
        adminService.deleteMessage(id);
    }

    @GetMapping("/reviews/{profileId}")
    public List<UserReviewDto> getAllReviews(@PathVariable long profileId) {
        return adminService.getAllReviews(profileId);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
    }

    @GetMapping("/stats")
    public Map<String, Long> getSystemStats(@RequestParam(required = false) String type) {
        if ("accounts".equalsIgnoreCase(type)) return Map.of("accounts", adminService.countAccounts());
        if ("messages".equalsIgnoreCase(type)) return Map.of("messages", adminService.countMessages());
        return adminService.getSystemStats();
    }
}
