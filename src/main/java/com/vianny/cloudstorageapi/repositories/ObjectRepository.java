package com.vianny.cloudstorageapi.repositories;

import com.vianny.cloudstorageapi.models.ObjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ObjectRepository extends JpaRepository<ObjectDetails, Long> {
    ObjectDetails findByObjectLocationAndAccount_Login(String objectLocation, String login);

    @Modifying
    @Query("DELETE FROM ObjectDetails od WHERE od.objectLocation = :objectLocation AND od.account.login = :login")
    void deleteObjectDetailsByObjectLocation(String objectLocation, String login);
}
