package com.vianny.cloudstorageapi.dto;

public class ResponseFolder {
    private String folderName;
    private String path;

    public ResponseFolder(String folderName, String path) {
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
