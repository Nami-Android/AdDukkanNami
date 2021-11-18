package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class CountryDataModel extends ResponseModel implements Serializable {
    private List<CountryModel> data;

    public List<CountryModel> getData() {
        return data;
    }
}
