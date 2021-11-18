package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class FavouriteProductDataModel implements Serializable {
    private int status;

    private List<Data> data;

    public int getStatus() {
        return status;
    }

    public List<Data> getData() {
        return data;
    }

    public class Data implements Serializable {
        private int id;
        private int product_id;
        private int user_id;
        private String created_at;
        private String updated_at;
        private SingleProductModel product_data;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public SingleProductModel getProduct_data() {
            return product_data;
        }
    }}
