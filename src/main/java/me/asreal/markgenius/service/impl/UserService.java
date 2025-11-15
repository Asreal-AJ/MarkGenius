package me.asreal.markgenius.service.impl;

import lombok.RequiredArgsConstructor;
import me.asreal.markgenius.entity.UserAccount;
import me.asreal.markgenius.repository.UserAccountRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;

    @Cacheable(value = "USER_ACCOUNT", key = "#userId")
    public UserAccount getUserAccount(Long userId) {
        var userAccount = userAccountRepository.findById(userId);
        return userAccount.orElse(null);
    }

    public List<UserAccount> getAllUserAccounts() {
        var userAccounts = new ArrayList<UserAccount>();
        userAccountRepository.findAll().forEach(userAccounts::add);
        return userAccounts;
    }

}
