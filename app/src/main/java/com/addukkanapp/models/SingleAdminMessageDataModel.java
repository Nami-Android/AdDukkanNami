package com.addukkanapp.models;

import java.io.Serializable;

public class SingleAdminMessageDataModel extends ResponseModel implements Serializable {
    private AdminMessageModel data;

    public AdminMessageModel getData() {
        return data;
    }
}
