package com.addukkanapp.models;

import java.io.Serializable;

public class UserRateModel implements Serializable {
    private int id;
    private int from_user_id;
    private int to_user_id;
    private int rate;
    private String comment;
    private String created_at;
    private UserModel.Data from_user;

    public int getId() {
        return id;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public int getRate() {
        return rate;
    }

    public String getComment() {
        return comment;
    }

    public UserModel.Data getFrom_user() {
        return from_user;
    }

    public String getCreated_at() {
        return created_at;
    }
}
