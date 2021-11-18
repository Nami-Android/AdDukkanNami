package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class MainCategoryDataModel implements Serializable {

    private List<Data> data;
    private int status;

    public List<Data> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public static class Data implements Serializable {
        private int id;
        private String available;
        private String is_shown;
        private String icon;
        private int parent_id;
        private int level;
        private String created_at;
        private String updated_at;
        private Departments department_trans_fk;
        private List<SubCategoryDataModel> sub_departments;
        private List<ProductData> product_list;

        public int getId() {
            return id;
        }

        public String getAvailable() {
            return available;
        }

        public String getIs_shown() {
            return is_shown;
        }

        public String getIcon() {
            return icon;
        }

        public int getParent_id() {
            return parent_id;
        }

        public int getLevel() {
            return level;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public Departments getDepartment_trans_fk() {
            return department_trans_fk;
        }

        public List<SubCategoryDataModel> getSub_departments() {
            return sub_departments;
        }

        public List<ProductData> getProduct_list() {
            return product_list;
        }

        public void setProduct_list(List<ProductData> product_list) {
            this.product_list = product_list;
        }

        public class Departments implements Serializable {
            private int id;
            private int department_id;
            private String title;
            private String image;
            private String banner;
            private String lang;
            private String created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public int getDepartment_id() {
                return department_id;
            }

            public String getTitle() {
                return title;
            }

            public String getImage() {
                return image;
            }

            public String getBanner() {
                return banner;
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

    }

    public static class ProductData implements Serializable{
        private SingleProductModel product_data;

        public SingleProductModel getProduct_data() {
            return product_data;
        }

        public void setProduct_data(SingleProductModel product_data) {
            this.product_data = product_data;
        }
    }

}
