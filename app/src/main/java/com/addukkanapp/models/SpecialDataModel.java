package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class SpecialDataModel extends ResponseModel implements Serializable {
    private List<SpecialModel> data;

    public List<SpecialModel> getData() {
        return data;
    }
}
