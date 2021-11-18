package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class AddOrderModel implements Serializable {
    private int user_id;
    private String prescription_id;
    private String country_code;
    private String name;
    private String phone;
    private String phone_code;
    private String address;
    private String address_lat;
    private String address_long;
    private String notes;
    private String subtotal;
    private String shipping;
    private String total_payments;
    private String payment_method;
    private String coupon_id;
    private String copoun;
    private List<CartDataModel.Data.Detials> product_list;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPrescription_id() {
        return prescription_id;
    }

    public void setPrescription_id(String prescription_id) {
        this.prescription_id = prescription_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_lat() {
        return address_lat;
    }

    public void setAddress_lat(String address_lat) {
        this.address_lat = address_lat;
    }

    public String getAddress_long() {
        return address_long;
    }

    public void setAddress_long(String address_long) {
        this.address_long = address_long;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getTotal_payments() {
        return total_payments;
    }

    public void setTotal_payments(String total_payments) {
        this.total_payments = total_payments;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public List<CartDataModel.Data.Detials> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(List<CartDataModel.Data.Detials> product_list) {
        this.product_list = product_list;
    }

    public String getCopoun() {
        return copoun;
    }

    public void setCopoun(String copoun) {
        this.copoun = copoun;
    }
}
