package com.addukkanapp.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.addukkanapp.BR;
import com.addukkanapp.R;

public class LocationDetialsModel extends BaseObservable {
    private String name;
    private String phone;
    private String detials;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();


    public boolean isDataValid(Context context) {

        if (!name.isEmpty() &&
                !phone.isEmpty()

        ) {


            error_name.set(null);
            error_phone.set(null);


            return true;

        } else {

            if (name.isEmpty()){
                error_name.set(context.getString(R.string.field_required));
            }else {
                error_name.set(null);

            }



            if (phone.isEmpty()){
                error_phone.set(context.getString(R.string.field_required));
            }else {
                error_phone.set(null);

            }

            return false;

        }

    }

    public LocationDetialsModel() {
        name = "";
        phone ="";
        detials="";
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
@Bindable
    public String getDetials() {
        return detials;
    }

    public void setDetials(String detials) {
        this.detials = detials;
        notifyPropertyChanged(BR.detials);
    }



    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }


}
