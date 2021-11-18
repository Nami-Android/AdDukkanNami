package com.addukkanapp.models;

import java.util.List;

public class AdminMessageDataModel extends StatusResponse{
    private int current_page;
    private AdminRoomModel room;
    private List<AdminMessageModel> data;

    public int getCurrent_page() {
        return current_page;
    }

    public AdminRoomModel getRoom() {
        return room;
    }

    public List<AdminMessageModel> getData() {
        return data;
    }
}
