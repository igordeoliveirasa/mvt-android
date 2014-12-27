package com.igordeoliveira.yolo.model;

/**
 * Created by igor on 27/12/14.
 */
public class User {

    private long uid;
    private String name;
    private String email;
    private String image;
    private String provider;

    public User(String provider, long uid, String name, String email, String image) {
        this.provider = provider;
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
