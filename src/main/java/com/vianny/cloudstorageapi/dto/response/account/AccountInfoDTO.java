package com.vianny.cloudstorageapi.dto.response.account;

public class AccountInfoDTO {
    private String login;
    private int sizeStorage;

    public AccountInfoDTO(String login, int sizeStorage) {
        this.login = login;
        this.sizeStorage = sizeStorage;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getSizeStorage() {
        return sizeStorage;
    }

    public void setSizeStorage(int sizeStorage) {
        this.sizeStorage = sizeStorage;
    }
}
