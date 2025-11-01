package me.asreal.markgenius.repository;

import me.asreal.markgenius.entity.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByVerificationCode(String verificationCode);

}
