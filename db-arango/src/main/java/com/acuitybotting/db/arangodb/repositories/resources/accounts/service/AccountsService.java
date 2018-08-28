package com.acuitybotting.db.arangodb.repositories.resources.accounts.service;

import com.acuitybotting.db.arangodb.repositories.resources.accounts.AccountRepository;
import com.acuitybotting.db.arangodb.repositories.resources.accounts.domain.RsAccount;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 8/15/2018.
 */
@Service
public class AccountsService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Set<RsAccount> loadAccounts(String principalId) {
        return accountRepository.findByFields("principalId", principalId).collect(Collectors.toSet());
    }

    public Optional<RsAccount> findAccount(String principalId, String accountEmail) {
        return accountRepository.findByKey(principalId + "_" + accountEmail);
    }

    public boolean updatePassword(String principalId, String accountEmail, String encryptedPassword) {
        if (accountEmail == null || accountEmail.isEmpty() || encryptedPassword == null || encryptedPassword.isEmpty()) return false;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("encryptedPassword", encryptedPassword);
        accountRepository.update(principalId + "_" + accountEmail, jsonObject.toString());
        return true;
    }
}
