package com.addukkanapp.models;

import java.io.Serializable;

public class SingleMessageDataModel extends ResponseModel implements Serializable {
    private MessageModel data;

    public MessageModel getData() {
        return data;
    }
}
