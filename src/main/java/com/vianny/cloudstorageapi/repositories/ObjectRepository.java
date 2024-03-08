package com.vianny.cloudstorageapi.repositories;

import com.vianny.cloudstorageapi.models.ObjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectRepository extends JpaRepository<ObjectDetails, Long> {
    ObjectDetails findByObjectName(String objectName);
}
