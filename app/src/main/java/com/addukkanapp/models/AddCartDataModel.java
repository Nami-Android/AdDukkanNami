package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class AddCartDataModel implements Serializable {
    private int user_id;
    private double total_price;
    private String country_code;
private List<AddCartProductItemModel> cart_products;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public List<AddCartProductItemModel> getCart_products() {
        return cart_products;
    }

    public void setCart_products(List<AddCartProductItemModel> cart_products) {
        this.cart_products = cart_products;
    }
}
