package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class UserModel extends ResponseModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class SpecializationTransFk implements Serializable {
        private int id;
        private int specialization_id;
        private String title;
        private String lang;

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
    }

    public static class CountrySettingTransFk implements Serializable {
        private int id;
        private int country_setting_id;
        private String title;
        private String lang;
        public String currency;

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

        public String getCurrency() {
            return currency;
        }
    }

    public static class UserSpecialization implements Serializable {
        public int id;
        public String image;
        public String is_shown;
        public SpecializationTransFk specialization_trans_fk;

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public String getIs_shown() {
            return is_shown;
        }

        public SpecializationTransFk getSpecialization_trans_fk() {
            return specialization_trans_fk;
        }
    }

    public static class UserCountry implements Serializable {
        private int id;
        private String code;
        private String phone_code;
        private String flage;
        private String is_default;
        private CountrySettingTransFk country_setting_trans_fk;

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

        public CountrySettingTransFk getCountry_setting_trans_fk() {
            return country_setting_trans_fk;
        }
    }

    public static class Data implements Serializable {
        private int id;
        private String code;
        private String user_type;
        private String phone_code;
        private String phone;
        private String name;
        private String email;
        private int specialization_id;
        private String country_code;
        private String about_user;
        private String hospital_place;
        private String license_image;
        private String address;
        private double latitude;
        private double longitude;
        private String gender;
        private String birthday;
        private String logo;
        private String banner;
        private String approved_status;
        private String approved_by;
        private String is_blocked;
        private String is_login;
        private String logout_time;
        private String email_verified_at;
        private String confirmation_code;
        private String forget_password_code;
        private String software_type;
        private String deleted_at;
        private String token;
        private double rate;
        private UserSpecialization user_specialization;
        private UserCountry user_country;
        private List<UserRateModel> user_rates;
        private String firebase_token;


        public int getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public int getSpecialization_id() {
            return specialization_id;
        }

        public String getCountry_code() {
            return country_code;
        }

        public String getAbout_user() {
            return about_user;
        }

        public String getHospital_place() {
            return hospital_place;
        }

        public String getLicense_image() {
            return license_image;
        }

        public String getAddress() {
            return address;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getGender() {
            return gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getLogo() {
            return logo;
        }

        public String getBanner() {
            return banner;
        }

        public String getApproved_status() {
            return approved_status;
        }

        public String getApproved_by() {
            return approved_by;
        }

        public String getIs_blocked() {
            return is_blocked;
        }

        public String getIs_login() {
            return is_login;
        }

        public String getLogout_time() {
            return logout_time;
        }

        public String getEmail_verified_at() {
            return email_verified_at;
        }

        public String getConfirmation_code() {
            return confirmation_code;
        }

        public String getForget_password_code() {
            return forget_password_code;
        }

        public String getSoftware_type() {
            return software_type;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public String getToken() {
            return token;
        }

        public double getRate() {
            return rate;
        }

        public List<UserRateModel> getUser_rates() {
            return user_rates;
        }

        public UserSpecialization getUser_specialization() {
            return user_specialization;
        }

        public UserCountry getUser_country() {
            return user_country;
        }

        public String getFirebase_token() {
            return firebase_token;
        }

        public void setFirebase_token(String firebase_token) {
            this.firebase_token = firebase_token;
        }
    }


}
