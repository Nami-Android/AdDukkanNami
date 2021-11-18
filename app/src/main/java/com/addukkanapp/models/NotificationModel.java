package com.addukkanapp.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private int id;
    private int from_user_id;
    private int to_user_id;
    private int order_id;
    private String offer_id;
    private String title;
    private String message;
    private String title_en;
    private String message_en;
    private String action;
    private String is_read;
    private long notification_date;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle_en() {
        return title_en;
    }

    public String getMessage_en() {
        return message_en;
    }

    public String getAction() {
        return action;
    }

    public String getIs_read() {
        return is_read;
    }

    public long getNotification_date() {
        return notification_date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
