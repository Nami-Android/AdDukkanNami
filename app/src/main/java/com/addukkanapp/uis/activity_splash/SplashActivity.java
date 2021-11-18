package com.addukkanapp.uis.activity_splash;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import com.addukkanapp.R;
import com.addukkanapp.databinding.ActivitySplashBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_home.HomeActivity;
import com.addukkanapp.uis.activity_language.LanguageActivity;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        initView();
    }

    private void initView() {

        checkData();


    }

    private void checkData() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Preferences preferences = Preferences.getInstance();
            UserModel userModel = preferences.getUserData(this);
            AppLocalSettings settings = preferences.isLanguageSelected(this);

            Intent intent;

            if (settings == null) {
                intent = new Intent(this, LanguageActivity.class);
                intent.putExtra("data", true);

                startActivityForResult(intent, 100);

            } else {
                if (settings.isLanguageSelected()) {
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    intent = new Intent(this, LanguageActivity.class);
                    intent.putExtra("data", true);
                    startActivityForResult(intent, 100);

                }
            }


        }, 2000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String lang = data.getStringExtra("lang");
            String countrycode=data.getStringExtra("countrycode");
            String currency=data.getStringExtra("currency");

            refreshActivity(lang,countrycode,currency);
        }
    }

    public void refreshActivity(String lang, String countrycode,String currency) {

        Preferences preferences = Preferences.getInstance();
        AppLocalSettings settings = preferences.isLanguageSelected(this);
        if (settings == null) {
            settings = new AppLocalSettings();

        }
        settings.setLanguageSelected(true);
        settings.setCountry_code(countrycode);
        settings.setCurrency(currency);

        preferences.setIsLanguageSelected(this, settings);
        Paper.init(this);
        Paper.book().write("lang", lang);
        Language.updateResources(this, lang);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


}