package com.addukkanapp.models;

import java.io.Serializable;

public class CountryModel implements Serializable {

    private int id;
    private String code;
    private String phone_code;
    private String flage;
    private String is_default;
    private CountrySettingTransFk country_setting_trans_fk;
    private boolean isSelected = false;

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public void setFlage(String flage) {
        this.flage = flage;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }

    public void setCountry_setting_trans_fk(CountrySettingTransFk country_setting_trans_fk) {
        this.country_setting_trans_fk = country_setting_trans_fk;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public String getFlage() {
        return flage;
    }

    public String getIs_default() {
        return is_default;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public CountrySettingTransFk getCountry_setting_trans_fk() {
        return country_setting_trans_fk;
    }

    public static class CountrySettingTransFk implements Serializable {
        private int id;
        private int country_setting_id;
        private String title;
        private String lang;
        private String currency;

        public int getId() {
            return id;
        }

        public int getCountry_setting_id() {
            return country_setting_id;
        }

        public String getTitle() {
            return title;
        }

        public String getLang() {
            return lang;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCountry_setting_id(int country_setting_id) {
            this.country_setting_id = country_setting_id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getCurrency() {
            return currency;
        }
    }


}
