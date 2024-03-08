package com.vianny.cloudstorageapi.repositories;

import com.vianny.cloudstorageapi.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findUserByLogin(String login);
    Boolean existsUserByLogin(String login);
}
