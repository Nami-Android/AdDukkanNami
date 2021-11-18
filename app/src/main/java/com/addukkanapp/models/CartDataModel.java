package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class CartDataModel extends ResponseModel implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data implements Serializable{
        private int id;
        public String prescription_id;
        public int user_id;
        public double total_price;
        private List<Detials> details;

        public int getId() {
            return id;
        }

        public String getPrescription_id() {
            return prescription_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public double getTotal_price() {
            return total_price;
        }

        public List<Detials> getDetails() {
            return details;
        }

        public class Detials implements Serializable{
            private int id;
            private int cart_id;
            private int product_id;
            private int product_price_id;
            private int vendor_id;
            private double price;
            private double amount;
            private String have_offer;
            private String offer_type;
            private double offer_value;
            private int offer_min;
            private int offer_bonus;
            private double old_price;
            private SingleProductModel product_data;
            private boolean isLoading = false;

            public int getId() {
                return id;
            }

            public int getCart_id() {
                return cart_id;
            }

            public int getProduct_id() {
                return product_id;
            }

            public int getProduct_price_id() {
                return product_price_id;
            }

            public int getVendor_id() {
                return vendor_id;
            }

            public double getPrice() {
                return price;
            }

            public double getAmount() {
                return amount;
            }

            public String getHave_offer() {
                return have_offer;
            }

            public String getOffer_type() {
                return offer_type;
            }

            public double getOffer_value() {
                return offer_value;
            }

            public int getOffer_min() {
                return offer_min;
            }

            public int getOffer_bonus() {
                return offer_bonus;
            }

            public double getOld_price() {
                return old_price;
            }

            public SingleProductModel getProduct_data() {
                return product_data;
            }

            public boolean isLoading() {
                return isLoading;
            }

            public void setLoading(boolean loading) {
                isLoading = loading;
            }
        }
    }
}
