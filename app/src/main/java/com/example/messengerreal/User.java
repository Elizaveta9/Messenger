package com.example.messengerreal;

public class User {
    private String id;
    private String name;
    private boolean online;

    public User(String id, String name, boolean online) {
        this.id = id;
        this.name = name;
        this.online = online;
    }

    public User(){
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }
}
