package com.addukkanapp.models;

import java.io.Serializable;

public class NotificationCountModel  implements Serializable {

    private Data data;
    private int status;

    public Data getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public static class Data implements Serializable{
        private int count_unread;

        public int getCount() {
            return count_unread;
        }
    }

}
