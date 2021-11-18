package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class ProductDataModel extends ResponseModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private SingleProductModel product;
        private ParentAttributes parentAttributes;
        private List<Attribute> attributes;
        private List<Comment> comments;
        private List<SingleProductModel> others;

        public SingleProductModel getProduct() {
            return product;
        }

        public ParentAttributes getParentAttributes() {
            return parentAttributes;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public List<Comment> getComments() {
            return comments;
        }

        public List<SingleProductModel> getOthers() {
            return others;
        }
    }


    public static class Comment implements Serializable
    {
        private int id;
        private int product_id;
        private int user_id;
        private int rate;
        private String comment;
        private UserModel.Data user;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public int getRate() {
            return rate;
        }

        public String getComment() {
            return comment;
        }

        public UserModel.Data getUser() {
            return user;
        }
    }
    public static class Attribute implements Serializable
    {
        private int id;
        private int product_id;
        private int vendor_id;
        private int level;
        private String parent_id;
        private int attribute_id;
        private int price;
        private int stock;
        private String is_default;
        private String is_default_price;
        private String country_code;
        private String image;
        private String title;
        private List<Attribute> attributes;
        private AttributeTransFk attribute_trans_fk;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getVendor_id() {
            return vendor_id;
        }

        public int getLevel() {
            return level;
        }

        public String getParent_id() {
            return parent_id;
        }

        public int getAttribute_id() {
            return attribute_id;
        }

        public int getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public String getIs_default() {
            return is_default;
        }

        public String getIs_default_price() {
            return is_default_price;
        }

        public String getCountry_code() {
            return country_code;
        }

        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public AttributeTransFk getAttribute_trans_fk() {
            return attribute_trans_fk;
        }

        public void setIs_default(String is_default) {
            this.is_default = is_default;
        }

        public void setAttributes(List<Attribute> attributes) {
            this.attributes = attributes;
        }
    }
    public static class AttributeTransFk implements Serializable
    {
        private int id;
        private int attribute_id;
        private String title;
        private String lang;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public int getAttribute_id() {
            return attribute_id;
        }

        public String getTitle() {
            return title;
        }

        public String getLang() {
            return lang;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }
    public static class ParentAttributes implements Serializable
    {
        private int id;
        private int attribute_id;
        private List<Attribute> attributes;
        private String title;
        private AttributeTransFk attribute_trans_fk;

        public int getId() {
            return id;
        }

        public int getAttribute_id() {
            return attribute_id;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public String getTitle() {
            return title;
        }

        public AttributeTransFk getAttribute_trans_fk() {
            return attribute_trans_fk;
        }
    }




}
