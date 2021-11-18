package com.addukkanapp.models;

import java.io.Serializable;

public class SubCategoryDataModel implements Serializable {
    public int id;
    public String available;
    public String is_shown;
    public String icon;
    public int parent_id;
    public int level;
    public String created_at;
    public String updated_at;
    public DepartmentTransFk department_trans_fk;

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

    public DepartmentTransFk getDepartment_trans_fk() {
        return department_trans_fk;
    }

    public class DepartmentTransFk implements Serializable{
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
