package com.addukkanapp.models;

import java.io.Serializable;

public class SpecialModel implements Serializable {

    private int id;
    private String image;
    private String is_shown;
    private String created_at;
    private String updated_at;
    private SpecializationTransFk specialization_trans_fk;
    private boolean isSelected = false;


    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getIs_shown() {
        return is_shown;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public SpecializationTransFk getSpecialization_trans_fk() {
        return specialization_trans_fk;
    }

    public void setSpecialization_trans_fk(SpecializationTransFk specialization_trans_fk) {
        this.specialization_trans_fk = specialization_trans_fk;
    }

    public static class SpecializationTransFk {
        private int id;
        private int specialization_id;
        private String title;
        private String lang;
        private String created_at;
        private String updated_at;

        public SpecializationTransFk(String title) {
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public int getSpecialization_id() {
            return specialization_id;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
