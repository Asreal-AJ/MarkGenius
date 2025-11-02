package me.asreal.markgenius.service.impl;

import lombok.RequiredArgsConstructor;
import me.asreal.markgenius.entity.UserAccount;
import me.asreal.markgenius.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public List<UserAccount> getAllUserAccounts() {
        var userAccounts = new ArrayList<UserAccount>();
        userAccountRepository.findAll().forEach(userAccounts::add);
        return userAccounts;
    }

}
