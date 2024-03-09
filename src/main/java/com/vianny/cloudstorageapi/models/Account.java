package com.vianny.cloudstorageapi.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private String login;
    @Column
    private String password;
    @Column
    private int size_storage = 1073741824;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ObjectDetails> objectDetails;

    public Account() {
    }

    public Account(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSize_storage() {
        return size_storage;
    }

    public void setSize_storage(int size_storage) {
        this.size_storage = size_storage;
    }

    public List<ObjectDetails> getObjectDetails() {
        return objectDetails;
    }

    public void setObjectDetails(List<ObjectDetails> objectDetails) {
        this.objectDetails = objectDetails;
    }
}
