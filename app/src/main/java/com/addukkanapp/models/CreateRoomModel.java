package com.addukkanapp.models;

import java.io.Serializable;

public class CreateRoomModel extends ResponseModel implements Serializable {
   private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable
    {
        private int id;

        public int getId() {
            return id;
        }
    }

}
