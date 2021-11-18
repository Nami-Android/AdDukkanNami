package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class AttrDataModel extends ResponseModel implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private List<ProductDataModel.Attribute> attributes;

        public List<ProductDataModel.Attribute> getAttributes() {
            return attributes;
        }
    }
}
