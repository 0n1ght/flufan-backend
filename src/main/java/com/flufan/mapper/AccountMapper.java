package com.flufan.mapper;

import com.flufan.dto.AccountDto;
import com.flufan.dto.ProfileDto;
import com.flufan.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDto toAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setId(account.getId());
        accountDto.setUsername(account.getUsername());

        if (account.getProfile() != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setNick(account.getProfile().getNick());
            profileDto.setFirstName(account.getProfile().getFirstName());
            profileDto.setLastName(account.getProfile().getLastName());
            accountDto.setProfile(profileDto);
        }

        return accountDto;
    }
}
