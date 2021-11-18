package com.addukkanapp.uis.activity_login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import com.addukkanapp.R;
import com.addukkanapp.adapters.CountryAdapter2;
import com.addukkanapp.databinding.ActivityLoginBinding;
import com.addukkanapp.databinding.DialogCountryBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.CountryDataModel;
import com.addukkanapp.models.CountryModel;
import com.addukkanapp.models.LoginModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_sign_up.SignUpActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginModel loginModel;
    private String lang = "ar";
    private CountryAdapter2 adapter;
    private List<CountryModel> list;
    private AlertDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }

    private void initView() {
        list = new ArrayList<>();

        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.tvSignUp.setText(Html.fromHtml(getString(R.string.don_t_have_account_sign_up)));
        loginModel = new LoginModel();
        binding.setModel(loginModel);
        binding.btnLogin.setOnClickListener(v -> {
            if (loginModel.isDataValid(this)) {
                login();
            }
        });


        binding.tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivityForResult(intent,100);
        });
        binding.arrow.setOnClickListener(v -> createDialogAlert());

        binding.llBack.setOnClickListener(v -> finish());


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
                                        list.clear();
                                        list.addAll(response.body().getData());
                                        binding.arrow.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.arrow.setVisibility(View.GONE);

                                    }
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            switch (response.code()) {
                                case 500:
                                    Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(LoginActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            Log.e("ddd",e.getMessage()+"__");
                        }
                    }
                });

    }

    private void login() {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .login(loginModel.getPhone_code(),loginModel.getPhone(),loginModel.getPassword())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body()!=null&&response.body().getStatus()==200){
                                if (response.body() != null&&response.body().getData()!=null){
                                    Preferences preferences = Preferences.getInstance();
                                    preferences.create_update_userdata(LoginActivity.this,response.body());
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }else if (response.body()!=null&&response.body().getStatus()==404){
                                Toast.makeText(LoginActivity.this, R.string.user_not_found, Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(LoginActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            dialog.dismiss();

                            switch (response.code()){
                                case 500:
                                    Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(LoginActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                }
                                else if (t.getMessage().toLowerCase().contains("socket")||t.getMessage().toLowerCase().contains("canceled")){ }
                                else {
                                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });


    }

    public void setItemCountry(CountryModel model) {
        binding.tvPhoneCode.setText(model.getPhone_code());

        loginModel.setPhone_code(model.getPhone_code());
        loginModel.setCountry_code(model.getCode());
        if (dialog!=null){
            dialog.dismiss();
        }
    }

    private  void createDialogAlert() {
         dialog = new AlertDialog.Builder(this)
                .create();

        DialogCountryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_country, null, false);
        if (adapter==null){
            adapter = new CountryAdapter2(this,list);
            binding.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.recView.setAdapter(adapter);
        }
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}