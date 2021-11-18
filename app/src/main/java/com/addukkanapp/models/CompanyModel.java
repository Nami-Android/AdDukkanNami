package com.addukkanapp.models;

import java.io.Serializable;

public class CompanyModel implements Serializable {
   private int id;
   private String is_shown;
   private String image;
   private ProductCompanyTransFk product_company_trans_fk;

    public int getId() {
        return id;
    }

    public String getIs_shown() {
        return is_shown;
    }

    public String getImage() {
        return image;
    }

    public ProductCompanyTransFk getProduct_company_trans_fk() {
        return product_company_trans_fk;
    }

    public static class ProductCompanyTransFk implements Serializable{
        private int id;
        private int product_company_id;
        private String title;
        private String lang;

        public int getId() {
            return id;
        }

        public int getProduct_company_id() {
            return product_company_id;
        }

        public String getTitle() {
            return title;
        }

        public String getLang() {
            return lang;
        }
    }



}
