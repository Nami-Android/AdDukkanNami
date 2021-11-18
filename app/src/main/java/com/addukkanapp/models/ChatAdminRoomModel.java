package com.addukkanapp.models;

import java.io.Serializable;

public class ChatAdminRoomModel implements Serializable {
    private int room_id;
    private int user_id;


    public ChatAdminRoomModel(int room_id, int user_id) {
        this.room_id = room_id;
        this.user_id = user_id;

    }

    public int getRoom_id() {
        return room_id;
    }

    public int getUser_id() {
        return user_id;
    }


}
