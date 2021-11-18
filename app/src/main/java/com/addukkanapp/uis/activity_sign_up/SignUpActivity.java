package com.addukkanapp.uis.activity_sign_up;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.addukkanapp.R;
import com.addukkanapp.adapters.CountryAdapter2;
import com.addukkanapp.databinding.ActivityLoginBinding;
import com.addukkanapp.databinding.ActivitySignUpBinding;
import com.addukkanapp.databinding.DialogAlertBinding;
import com.addukkanapp.databinding.DialogCountryBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.CountryDataModel;
import com.addukkanapp.models.CountryModel;
import com.addukkanapp.models.SignUpModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_verification_code.VerificationCodeActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private SignUpModel signUpModel;
    private String lang = "ar";
    private CountryAdapter2 adapter;
    private List<CountryModel> list;
    private UserModel userModel;
    private Preferences preferences;
    private AlertDialog dialog;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2;
    private Uri uri = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        list = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.tvLogin.setText(Html.fromHtml(getString(R.string.sign_up_text)));
        signUpModel = new SignUpModel();
        binding.setModel(signUpModel);
        binding.btnSignUp.setOnClickListener(v -> {
            if (signUpModel.isDataValid(this)) {

                if (userModel==null){
                    Intent intent = new Intent(this, VerificationCodeActivity.class);
                    intent.putExtra("phone_code",signUpModel.getPhone_code());
                    intent.putExtra("phone", signUpModel.getPhone());
                    startActivityForResult(intent,100);
                }else {
                    updateProfile();
                }

            }
        });

        if (userModel!=null){
            signUpModel.setPhone_code(userModel.getData().getPhone_code());
            signUpModel.setPhone(userModel.getData().getPhone());
            signUpModel.setName(userModel.getData().getName());
            signUpModel.setCountry_code(userModel.getData().getUser_country().getCode());
            signUpModel.setPassword("123456");
            binding.setModel(signUpModel);
            binding.tvPhonCode.setText(userModel.getData().getPhone_code());
            binding.btnSignUp.setText(getString(R.string.update));
            binding.tvLogin.setVisibility(View.INVISIBLE);
        }



        binding.tvLogin.setOnClickListener(v -> finish());

        binding.llBack.setOnClickListener(v -> finish());

        binding.arrow.setOnClickListener(v -> createDialogAlert());

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
                                Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            switch (response.code()) {
                                case 500:
                                    Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            Log.e("ddd",e.getMessage()+"__");
                        }
                    }
                });

    }





    private void signUp() {
Log.e("cc000ode", signUpModel.getCountry_code());

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .signUp(signUpModel.getName(),signUpModel.getPhone_code(),signUpModel.getPhone(),signUpModel.getPassword(),"android",signUpModel.getCountry_code())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body()!=null&&response.body().getStatus()==200){
                                if (response.body() != null&&response.body().getData()!=null){
                                    Preferences preferences = Preferences.getInstance();
                                    preferences.create_update_userdata(SignUpActivity.this,response.body());
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }else if (response.body()!=null&&response.body().getStatus()==409){
                                Toast.makeText(SignUpActivity.this, R.string.ph_found, Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(SignUpActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            dialog.dismiss();

                            try {
                                Log.e("error_code",response.code()+"_");
                            } catch (NullPointerException e){

                            }
                            switch (response.code()){
                                case 500:
                                    Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SignUpActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
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
                                    Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                }
                                else if (t.getMessage().toLowerCase().contains("socket")||t.getMessage().toLowerCase().contains("canceled")){ }
                                else {
                                    Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateProfile() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .updateProfile("Bearer "+userModel.getData().getToken(),userModel.getData().getId(),signUpModel.getName(),signUpModel.getPhone_code(),signUpModel.getPhone(),signUpModel.getPassword(),"android",signUpModel.getCountry_code())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body()!=null&&response.body().getStatus()==200){
                                if (response.body() != null&&response.body().getData()!=null){
                                    Preferences preferences = Preferences.getInstance();
                                    preferences.create_update_userdata(SignUpActivity.this,response.body());
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }else if (response.body()!=null&&response.body().getStatus()==404){
                                Toast.makeText(SignUpActivity.this, R.string.user_not_found, Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(SignUpActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            dialog.dismiss();

                            switch (response.code()){
                                case 500:
                                    Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SignUpActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                }
                                else if (t.getMessage().toLowerCase().contains("socket")||t.getMessage().toLowerCase().contains("canceled")){ }
                                else {
                                    Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });



    }

    public void setItemCountry(CountryModel model) {
        binding.tvPhonCode.setText(model.getPhone_code());

        signUpModel.setPhone_code(model.getPhone_code());
        signUpModel.setCountry_code(model.getCode());
        dialog.dismiss();
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
            signUp();
        }
    }

}