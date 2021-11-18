package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class CompanyDataModel extends ResponseModel implements Serializable {
    private List<CompanyModel> data;

    public List<CompanyModel> getData() {
        return data;
    }
}
