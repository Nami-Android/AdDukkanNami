package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class ALLOrderDataModel implements Serializable {
   private List<SingleOrderModel.Data> data;
   private int status;

    public List<SingleOrderModel.Data> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}
