package com.vta.app.model;

public class CourceWithKey {
    private String key; // firebase node's unique key
    private String name;
    private String nvq_level;
    private String language;
    private String duration;
    private String imageUrl;

    public CourceWithKey() {
    }

    public CourceWithKey(String key, String name, String nvq_level, String language, String duration, String imageUrl) {
        this.key = key;
        this.name = name;
        this.nvq_level = nvq_level;
        this.language = language;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNvq_level() {
        return nvq_level;
    }

    public void setNvq_level(String nvq_level) {
        this.nvq_level = nvq_level;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
