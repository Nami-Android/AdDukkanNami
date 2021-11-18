package com.addukkanapp.models;

import java.io.Serializable;

public class ChatRoomModel implements Serializable {
    private int room_id;
    private int user_id;
    private String user_image;
    private String user_name;

    public ChatRoomModel(int room_id, int user_id, String user_image, String user_name) {
        this.room_id = room_id;
        this.user_id = user_id;
        this.user_image = user_image;
        this.user_name = user_name;
    }

    public int getRoom_id() {
        return room_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getUser_name() {
        return user_name;
    }
}
