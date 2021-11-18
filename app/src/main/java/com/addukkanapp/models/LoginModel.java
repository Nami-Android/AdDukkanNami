package com.addukkanapp.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;


import com.addukkanapp.BR;
import com.addukkanapp.R;

import java.io.Serializable;

public class LoginModel extends BaseObservable implements Serializable {
    private String phone_code;
    private String phone;
    private String password;
    private String country_code;
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();

    public LoginModel() {
        phone_code ="+966";
        phone="";
        password="";
        country_code = "sar";
    }
    public boolean isDataValid(Context context){
        if (!phone.isEmpty()&&
                !password.isEmpty()&&
                password.length()>=6&&
                !country_code.isEmpty()
        ){
            error_phone.set(null);
            error_password.set(null);
            return true;
        }else {
            if (phone.isEmpty()){
                error_phone.set(context.getString(R.string.field_required));
            }else {
                error_phone.set(null);

            }

            if (password.isEmpty()){
                error_password.set(context.getString(R.string.field_required));
            }else if (password.length()<6){
                error_password.set(context.getString(R.string.password_short));

            }else {
                error_password.set(null);

            }
            return false;
        }
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
