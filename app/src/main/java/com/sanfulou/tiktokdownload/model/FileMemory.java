package com.sanfulou.tiktokdownload.model;

import java.io.Serializable;

public class FileMemory implements Serializable {

    private String name;
    private String date;
    private String path;
    private String size;
    private String duration;

    public FileMemory() {
    }

    public FileMemory(FileMemory fileMemory) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public FileMemory(String name, String date, String path, String size, String duration) {
        this.name = name;
        this.date = date;
        this.path = path;
        this.size = size;
        this.duration = duration;
    }
}