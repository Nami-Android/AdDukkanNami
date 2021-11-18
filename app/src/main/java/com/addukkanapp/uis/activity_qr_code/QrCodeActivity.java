package com.addukkanapp.uis.activity_qr_code;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;


import com.addukkanapp.R;
import com.addukkanapp.databinding.ActivityQrCodeBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;

import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeActivity extends AppCompatActivity {
    private ActivityQrCodeBinding binding;
    private String lang;
    private CodeScanner mCodeScanner;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int  CAMERA_REQ = 2;
    private Preferences preferences;
    private UserModel userModel;
    private AppLocalSettings settings;
  private String country_coude;
    private String currecny;    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
        initView();
    }

    private void initView() {
        preferences=Preferences.getInstance();
        userModel=preferences.getUserData(this);
        settings = preferences.isLanguageSelected(this);
        userModel = preferences.getUserData(this);
         if (userModel != null) {
            country_coude = userModel.getData().getCountry_code();
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            country_coude = settings.getCountry_code();
            currecny=settings.getCurrency();
        }
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        checkCameraPermission();

    }

    private void initScanner(){

        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.setScanMode(ScanMode.SINGLE);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            binding.scannerView.setVisibility(View.GONE);
            scanOrder(result.getText());
        }));
        binding.scannerView.setVisibility(View.VISIBLE);
        mCodeScanner.startPreview();
    }

    private void scanOrder(String barcode) {


        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Log.e("vvvvvvvv", barcode+"___");
        Api.getService(Tags.base_url)
                .scanOrder("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", barcode,country_coude)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                //  CreateDialogAlert(activity, response.body());
                                mCodeScanner.releaseResources();
                                mCodeScanner.stopPreview();
                                Toast.makeText(QrCodeActivity.this,getResources().getString(R.string.suc_and),Toast.LENGTH_LONG).show();

                            } else if (response.body().getStatus() == 404 ) {
                                Toast.makeText(QrCodeActivity.this, getString(R.string.not_found), Toast.LENGTH_SHORT).show();

                            } else if (response.body().getStatus() == 406) {
                                Toast.makeText(QrCodeActivity.this, getString(R.string.no_product), Toast.LENGTH_SHORT).show();

                            } else {
                               // Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }
                            finish();
                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(QrCodeActivity.this, "Server Error ssss", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(QrCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Log.e("error", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(QrCodeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(QrCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }



    public void checkCameraPermission() {


        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            initScanner();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();

            }

        }
    }



    @Override
    public void onBackPressed() {

            finish();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCodeScanner!=null){
            mCodeScanner.releaseResources();

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mCodeScanner!=null){
            mCodeScanner.startPreview();

        }
    }
}