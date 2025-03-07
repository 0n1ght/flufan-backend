package com.frinkan.mapper;

import com.frinkan.dto.AccountDto;
import com.frinkan.dto.ProfileDto;
import com.frinkan.entity.Account;

public class AccountMapper {

    public AccountDto toAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setId(account.getId());
        accountDto.setUsername(account.getUsername());
        accountDto.setEmail(account.getEmail());

        if (account.getProfile() != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setNick(account.getProfile().getNick());
            profileDto.setFirstName(account.getProfile().getFirstName());
            profileDto.setLastName(account.getProfile().getLastName());
            profileDto.setProfilePicturePath(account.getProfile().getProfilePicturePath());
            accountDto.setProfile(profileDto);
        }

        return accountDto;
    }
}
