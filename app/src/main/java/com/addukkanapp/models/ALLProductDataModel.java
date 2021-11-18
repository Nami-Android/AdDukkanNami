package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class ALLProductDataModel implements Serializable {
   private List<SingleProductModel> data;
   private int status;

    public List<SingleProductModel> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}
