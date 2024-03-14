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

    @Query("SELECT account.size_storage FROM Account account WHERE account.login = :login")
    int findSizeStorageByLogin(@Param("login") String login);

    @Modifying
    @Query("UPDATE Account u SET u.size_storage = u.size_storage - :bytesToSubtract WHERE u.login = :login")
    void subtractBytesFromSizeStorage(@Param("login") String login, @Param("bytesToSubtract") int bytesToSubtract);

    @Modifying
    @Query("UPDATE Account a SET a.size_storage = a.size_storage + :sizeObject WHERE a.login = :login")
    void updateSizeStorageByLogin(@Param("login") String login, @Param("sizeObject") int sizeObject);
}
