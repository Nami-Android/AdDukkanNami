package com.addukkanapp.models;

import java.io.Serializable;

public class CouponDataModel implements Serializable {
    private int status;
    private Data data;

    public Data getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public class Data {
        private int id;
        private String type;
        private double discount_val;
        private String code;
        private String from_date;
        private String to_date;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public double getDiscount_val() {
            return discount_val;
        }

        public String getCode() {
            return code;
        }

        public String getFrom_date() {
            return from_date;
        }

        public String getTo_date() {
            return to_date;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }
}
