package com.addukkanapp.models;

import java.io.Serializable;

public class AdminRoomModel implements Serializable {
    private String id;
    private String admin_id;
    private String user_id;

    public String getId() {
        return id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public String getUser_id() {
        return user_id;
    }
}
