package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class FilterModel implements Serializable {
    private List<Integer> departments;
    private List<Integer> brand_id;
    private List<Integer> product_company_id;
    private String country_code;
    private String user_id;
    private String product_order;
    private String price_order;
    private String rate_order;
    private String seach_name;

    public List<Integer> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Integer> departments) {
        this.departments = departments;
    }

    public List<Integer> getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(List<Integer> brand_id) {
        this.brand_id = brand_id;
    }

    public List<Integer> getProduct_company_id() {
        return product_company_id;
    }

    public void setProduct_company_id(List<Integer> product_company_id) {
        this.product_company_id = product_company_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_order() {
        return product_order;
    }

    public void setProduct_order(String product_order) {
        this.product_order = product_order;
    }

    public String getPrice_order() {
        return price_order;
    }

    public void setPrice_order(String price_order) {
        this.price_order = price_order;
    }

    public String getRate_order() {
        return rate_order;
    }

    public void setRate_order(String rate_order) {
        this.rate_order = rate_order;
    }

    public String getSeach_name() {
        return seach_name;
    }

    public void setSeach_name(String seach_name) {
        this.seach_name = seach_name;
    }
}
