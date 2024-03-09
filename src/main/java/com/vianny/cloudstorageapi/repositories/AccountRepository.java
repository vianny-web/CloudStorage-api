package com.vianny.cloudstorageapi.repositories;

import com.vianny.cloudstorageapi.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findUserByLogin(String login);
    Boolean existsUserByLogin(String login);

    @Modifying
    @Query("UPDATE Account u SET u.size_storage = u.size_storage - :bytesToSubtract WHERE u.login = :login")
    void subtractBytesFromSizeStorage(@Param("login") String login, @Param("bytesToSubtract") int bytesToSubtract);
}
