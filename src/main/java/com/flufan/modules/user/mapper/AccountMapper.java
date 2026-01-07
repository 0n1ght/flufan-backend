package com.flufan.modules.user.mapper;

import com.flufan.modules.user.dto.AccountDto;
import com.flufan.modules.user.dto.ProfileDto;
import com.flufan.modules.user.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDto toAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setPublicId(account.getPublicId());
        accountDto.setUsername(account.getUsername());

        if (account.getProfile() != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setFirstName(account.getProfile().getFirstName());
            profileDto.setLastName(account.getProfile().getLastName());
            accountDto.setProfile(profileDto);
        }

        return accountDto;
    }
}
