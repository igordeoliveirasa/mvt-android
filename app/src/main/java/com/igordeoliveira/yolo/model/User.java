package com.igordeoliveira.yolo.model;

/**
 * Created by igor on 27/12/14.
 */
public class User {

    private long id;
    private String name;
    private String email;
    private Uid uid;

    public User(long id, String name, String email, Uid uid) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uid getUid() {
        return uid;
    }

    public void setUid(Uid uid) {
        this.uid = uid;
    }
}
