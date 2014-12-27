package com.igordeoliveira.yolo.model;

/**
 * Created by igor on 27/12/14.
 */
public class Uid {

    private long uid;
    private String provider;

    public Uid(long uid, String provider) {
        this.uid = uid;
        this.provider = provider;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
