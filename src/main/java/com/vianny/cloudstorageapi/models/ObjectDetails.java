package com.vianny.cloudstorageapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vianny.cloudstorageapi.enums.TypeObject;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "details_object")
public class ObjectDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(name = "object_name")
    private String objectName;
    @Enumerated(EnumType.STRING)
    @Column(name = "object_type")
    private TypeObject objectType;
    @Column(name = "object_size")
    private int objectSize;
    @Column(name = "object_location")
    private String objectLocation;
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "account_id")
    private Account account;

    public ObjectDetails(String objectName, TypeObject objectType, int objectSize, String objectLocation, LocalDateTime uploadDate) {
        this.objectName = objectName;
        this.objectType = objectType;
        this.objectSize = objectSize;
        this.objectLocation = objectLocation;
        this.uploadDate = uploadDate;
    }

    public ObjectDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public int getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(int objectSize) {
        this.objectSize = objectSize;
    }

    public String getObjectLocation() {
        return objectLocation;
    }

    public void setObjectLocation(String objectLocation) {
        this.objectLocation = objectLocation;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TypeObject getObjectType() {
        return objectType;
    }

    public void setObjectType(TypeObject objectType) {
        this.objectType = objectType;
    }
}
