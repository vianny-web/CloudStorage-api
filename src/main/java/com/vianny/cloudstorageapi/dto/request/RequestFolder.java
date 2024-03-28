package com.vianny.cloudstorageapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RequestFolder {
    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[а-яА-Яa-zA-Z0-9]*$")
    private String folderName;

    private String path;

    public RequestFolder(String folderName, String path) {
        this.folderName = folderName;
        this.path = path;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
