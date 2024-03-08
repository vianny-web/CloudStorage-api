package com.vianny.cloudstorageapi.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class ObjectDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column(name = "object_name")
    private String objectName;
    @Column(name = "object_size")
    private int objectSize;
    @Column(name = "object_location")
    private String objectLocation;
    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "account_id")
    private Account account;

    public ObjectDetails(String objectName, int objectSize, String objectLocation, LocalDate uploadDate) {
        this.objectName = objectName;
        this.objectSize = objectSize;
        this.objectLocation = objectLocation;
        this.uploadDate = uploadDate;
    }

    public ObjectDetails() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
