package com.addukkanapp.uis.activity_location_detials;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.addukkanapp.R;
import com.addukkanapp.databinding.ActivityAddressInformationBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AddOrderModel;
import com.addukkanapp.models.LocationDetialsModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_complete_order_detials.CompleteOrderDetialsActivity;

import io.paperdb.Paper;

public class LocationDetialsActivity extends AppCompatActivity {
    private ActivityAddressInformationBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private LocationDetialsModel locationDetialsModel;
    private String lang = "ar";
    private AddOrderModel addorderModel;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        addorderModel = (AddOrderModel) intent.getSerializableExtra("data");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_information);
       getDataFromIntent();
        initView();

    }

    private void initView() {
        binding.setTitle(addorderModel.getAddress());
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        locationDetialsModel = new LocationDetialsModel();
        if (userModel != null) {
            locationDetialsModel.setName(userModel.getData().getName());

            locationDetialsModel.setPhone(userModel.getData().getPhone());
        }

        binding.setModel(locationDetialsModel);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.btnLogin.setOnClickListener(view -> {
            if (locationDetialsModel.isDataValid(this)) {
                addorderModel.setPhone(locationDetialsModel.getPhone());
                addorderModel.setNotes(locationDetialsModel.getDetials());
                addorderModel.setName(locationDetialsModel.getName());
                Intent intent=new Intent(LocationDetialsActivity.this, CompleteOrderDetialsActivity.class);
                intent.putExtra("data",addorderModel);
                startActivity(intent);
                finish();
            }
        });
        binding.llBack.setOnClickListener(view -> finish());
    }

}