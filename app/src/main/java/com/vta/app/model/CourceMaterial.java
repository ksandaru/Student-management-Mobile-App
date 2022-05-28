package com.vta.app.model;

public class CourceMaterial {
    private String courceKey;
    private String title;
    private String description;
    private String documentUrl;

    public CourceMaterial() {
    }

    public CourceMaterial(String courceKey, String title, String description, String documentUrl) {
        this.courceKey = courceKey;
        this.title = title;
        this.description = description;
        this.documentUrl = documentUrl;
    }

    public String getCourceKey() {
        return courceKey;
    }

    public void setCourceKey(String courceKey) {
        this.courceKey = courceKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}
