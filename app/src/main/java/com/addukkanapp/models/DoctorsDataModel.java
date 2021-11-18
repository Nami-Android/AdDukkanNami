package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class DoctorsDataModel extends ResponseModel implements Serializable {
    private List<UserModel.Data> data;

    public List<UserModel.Data> getData() {
        return data;
    }
}
