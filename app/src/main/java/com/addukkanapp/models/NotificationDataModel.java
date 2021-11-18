package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {
    private List<NotificationModel> data;
    private int status;

    public List<NotificationModel> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}
