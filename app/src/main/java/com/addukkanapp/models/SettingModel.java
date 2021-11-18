package com.addukkanapp.models;

import java.io.Serializable;

public class SettingModel implements Serializable {
    private Setting data;
    private int status;

    public Setting getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public static class Setting implements Serializable {
        private String logo;
        private String title;
        private String address;
        private String phone;
        private String email;
        private String facebook;
        private String whatup;
        private String instagram;
        private String twitter;
        private String happy_clients;
        private String total_clients;
        private String prizes;
        private String happiness_rate;
        private String free_shipping;
        private String money_back;
        private String safe_payment;
        private String client_support;
        private String login_banner;
        private String terms_link;
        private String about_us_link;

        public String getLogo() {
            return logo;
        }

        public String getTitle() {
            return title;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getWhatup() {
            return whatup;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getHappy_clients() {
            return happy_clients;
        }

        public String getTotal_clients() {
            return total_clients;
        }

        public String getPrizes() {
            return prizes;
        }

        public String getHappiness_rate() {
            return happiness_rate;
        }

        public String getFree_shipping() {
            return free_shipping;
        }

        public String getMoney_back() {
            return money_back;
        }

        public String getSafe_payment() {
            return safe_payment;
        }

        public String getClient_support() {
            return client_support;
        }

        public String getLogin_banner() {
            return login_banner;
        }

        public String getTerms_link() {
            return terms_link;
        }

        public String getAbout_us_link() {
            return about_us_link;
        }
    }
}
