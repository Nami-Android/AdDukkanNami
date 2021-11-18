package com.addukkanapp.uis.activity_countries;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.CountryAdapter;
import com.addukkanapp.databinding.ActivityCountryBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CountryDataModel;
import com.addukkanapp.models.CountryModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryActivity extends AppCompatActivity {
    private ActivityCountryBinding binding;
    private String lang;
    private List<CountryModel> countryModelList;
    private CountryAdapter adapter;
    private UserModel userModel;
    private Preferences preferences;
    private AppLocalSettings settings;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country);
        initView();

    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        settings = preferences.isLanguageSelected(this);

        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        countryModelList = new ArrayList<>();
        binding.setLang(lang);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CountryAdapter(this, countryModelList);
        binding.recView.setAdapter(adapter);

        binding.llBack.setOnClickListener(view -> finish());

        getCountries();

    }

    private void getCountries() {

        Api.getService(Tags.base_url)
                .getCountries(lang)
                .enqueue(new Callback<CountryDataModel>() {
                    @Override
                    public void onResponse(Call<CountryDataModel> call, Response<CountryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        binding.tvNoData.setVisibility(View.GONE);
                                        updateCountryData(response.body().getData());
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else {
                                Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            switch (response.code()) {
                                case 500:
                                    Toast.makeText(CountryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<CountryDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(CountryActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    Toast.makeText(CountryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void updateCountryData(List<CountryModel> data) {
        countryModelList.clear();
        countryModelList.addAll(data);
        int pos = getMyCountryPos();
        if (pos != -1) {
            CountryModel countryModel = countryModelList.get(pos);
            countryModel.setSelected(true);
            countryModelList.set(pos, countryModel);
        }
        adapter.notifyDataSetChanged();

        binding.recView.postDelayed(() -> {
            if (pos != -1) {
                binding.recView.scrollToPosition(pos);
            }
        }, 1000);
    }

    private int getMyCountryPos() {
        int pos = -1;
        if (userModel.getData().getUser_country() != null) {
            for (int index = 0; index < countryModelList.size(); index++) {
                CountryModel countryModel = countryModelList.get(index);
                if (countryModel.getId() == userModel.getData().getUser_country().getId()) {
                    pos = index;
                    return pos;
                }
            }
        }

        return pos;

    }


    public void setcountry(CountryModel countryModel) {
        if(userModel==null){
        settings.setCountry_code(countryModel.getCode());
        settings.setCurrency(countryModel.getCountry_setting_trans_fk().getCurrency());
        preferences.setIsLanguageSelected(this,settings);
        finish();}
        else{
            updateProfile(countryModel.getCode());
        }
    }
    private void updateProfile(String country_code) {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .updateProfile("Bearer "+userModel.getData().getToken(),userModel.getData().getId(),country_code)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body()!=null&&response.body().getStatus()==200){
                                if (response.body() != null&&response.body().getData()!=null){
                                    Preferences preferences = Preferences.getInstance();
                                    preferences.create_update_userdata(CountryActivity.this,response.body());
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }else if (response.body()!=null&&response.body().getStatus()==404){
                                Toast.makeText(CountryActivity.this, R.string.user_not_found, Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(CountryActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            dialog.dismiss();

                            switch (response.code()){
                                case 500:
                                    Toast.makeText(CountryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(CountryActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code",response.code()+"_");
                            } catch (NullPointerException e){

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(CountryActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                }
                                else if (t.getMessage().toLowerCase().contains("socket")||t.getMessage().toLowerCase().contains("canceled")){ }
                                else {
                                    Toast.makeText(CountryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });



    }

}