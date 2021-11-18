package com.addukkanapp.interfaces;

public interface Listeners {


    interface BackListener
    {
        void back();
    }

    interface ProfileActions{
        void onOrder();
        void onFavorite();
        void onChat();
        void onChangeLanguage();
        void onCountry();
        void onAboutApp();
        void onChatWithAdmin();
        void onTerms();
        void onContactUs();
        void onFacebook();
        void onTwitter();
        void onInstagram();
        void onWhatsApp();
        void onLogout();
        void onNotification();
    }

}
