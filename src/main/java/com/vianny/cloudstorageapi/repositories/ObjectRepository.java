package com.vianny.cloudstorageapi.repositories;

import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjectRepository extends JpaRepository<ObjectDetails, Long> {
    @Query("SELECT od FROM ObjectDetails od WHERE od.objectName = :objectName AND od.objectType = :objectType AND od.objectLocation = :objectLocation AND od.account.login = :login")
    ObjectDetails findObjectDetailsByType(String objectName, TypeObject objectType, String objectLocation, String login);
    @Query("SELECT od FROM ObjectDetails od WHERE od.objectName = :objectName AND od.account.login = :login")
    List<ObjectDetails> findObjectsByName(@Param("objectName") String objectName, @Param("login") String login);
    ObjectDetails findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(String objectName, TypeObject objectType, String objectLocation, String login);
    @Modifying
    @Query("DELETE FROM ObjectDetails od WHERE od.objectName = :objectName AND od.objectType = :objectType AND od.objectLocation = :objectLocation AND od.account.login = :login")
    void deleteByObjectLocation(String objectName, TypeObject objectType, String objectLocation, String login);

    @Query("SELECT new com.vianny.cloudstorageapi.dto.ObjectDetailsDTO(od.objectName, od.objectType, od.objectLocation, od.objectSize, od.uploadDate) " +
            "FROM ObjectDetails od WHERE od.objectName = :objectName AND od.objectType = :objectType AND od.objectLocation = :objectLocation AND od.account.login = :login")
    List<ObjectDetailsDTO> getObjectDetailsByObjectLocation(@Param("objectName") String objectName, @Param("objectType") TypeObject objectType, @Param("objectLocation") String objectLocation, @Param("login") String login);

    @Query("SELECT new com.vianny.cloudstorageapi.dto.ObjectsInfoDTO(od.objectName, od.objectType) FROM ObjectDetails od WHERE od.objectLocation = :objectLocation AND od.account.login = :login")
    List<ObjectsInfoDTO> getObjectsNameByObjectLocation(@Param("objectLocation") String objectLocation, @Param("login") String login);
}
