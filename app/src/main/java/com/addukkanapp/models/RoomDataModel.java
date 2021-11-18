package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class RoomDataModel extends ResponseModel implements Serializable {
    private List<RoomModel> data;
    public List<RoomModel> getData() {
        return data;
    }
}
