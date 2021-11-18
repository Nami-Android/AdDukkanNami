package com.addukkanapp.uis.activity_home.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.addukkanapp.R;
import com.addukkanapp.databinding.FragmentHomeBinding;
import com.addukkanapp.databinding.FragmentProfileBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.models.NotificationCountModel;
import com.addukkanapp.models.SettingModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_about_us.AboutUsActivity;
import com.addukkanapp.uis.activity_admin_chat.ChatAdminActivity;
import com.addukkanapp.uis.activity_contact_us.ContactUsActivity;
import com.addukkanapp.uis.activity_countries.CountryActivity;
import com.addukkanapp.uis.activity_home.HomeActivity;
import com.addukkanapp.uis.activity_language.LanguageActivity;
import com.addukkanapp.uis.activity_login.LoginActivity;
import com.addukkanapp.uis.activity_my_favorite.MyFavoriteActivity;
import com.addukkanapp.uis.activity_notification.NotificationActivity;
import com.addukkanapp.uis.activity_order.MyOrderActivity;
import com.addukkanapp.uis.activity_rooms.RoomsActivity;
import com.addukkanapp.uis.activity_sign_up.SignUpActivity;

import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProfile extends Fragment implements Listeners.ProfileActions {
    private FragmentProfileBinding binding;
    private HomeActivity activity;
    private String lang = "ar";
    private UserModel userModel;
    private Preferences preferences;
    private SettingModel settingModel;


    public static FragmentProfile newInstance() {
        return new FragmentProfile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        binding.setModel(userModel);
        binding.setActions(this);
        binding.iconEdit.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SignUpActivity.class);
            startActivityForResult(intent, 100);
        });


        binding.setNotcount("0");

        if (userModel != null) {
            getNotificationCount();
        }
        getSetting();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivityForResult(intent, 100);
    }

    public void updateUserData() {
        userModel = preferences.getUserData(activity);
        binding.setModel(userModel);
    }


    @Override
    public void onOrder() {
        userModel = preferences.getUserData(activity);
        if (userModel == null) {
            navigateToLoginActivity();
        } else {
            Intent intent = new Intent(activity, MyOrderActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onFavorite() {
        userModel = preferences.getUserData(activity);
        if (userModel == null) {
            navigateToLoginActivity();
        } else {
            Intent intent = new Intent(activity, MyFavoriteActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onChat() {
        userModel = preferences.getUserData(activity);
        if (userModel == null) {
            navigateToLoginActivity();
        } else {
            Intent intent = new Intent(activity, RoomsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onChangeLanguage() {
        Intent intent = new Intent(activity, LanguageActivity.class);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onCountry() {
        userModel = preferences.getUserData(activity);
        if (userModel == null) {
            navigateToLoginActivity();
        } else {
            Intent intent = new Intent(activity, CountryActivity.class);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public void onAboutApp() {
        if (settingModel != null) {
            Intent intent = new Intent(activity, AboutUsActivity.class);
            String url = settingModel.getData().getAbout_us_link();
            intent.putExtra("url", url);
            intent.putExtra("type", "1");
            startActivity(intent);
        } else {
            Toast.makeText(activity, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onChatWithAdmin() {
        userModel = preferences.getUserData(activity);
        if (userModel == null) {
            navigateToLoginActivity();
        } else {
            Intent intent = new Intent(activity, ChatAdminActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTerms() {
        if (settingModel != null) {
            Intent intent = new Intent(activity, AboutUsActivity.class);
            String url = settingModel.getData().getTerms_link();
            intent.putExtra("url", url);
            intent.putExtra("type", "0");
            startActivity(intent);
        } else {
            Toast.makeText(activity, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onContactUs() {
        Intent intent = new Intent(activity, ContactUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFacebook() {
        if (settingModel != null && settingModel.getData() != null && settingModel.getData().getFacebook() != null && !settingModel.getData().getFacebook().equals("#")) {
            open(settingModel.getData().getFacebook());
        } else {
            Toast.makeText(activity, R.string.not_avail_now, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTwitter() {
        if (settingModel != null && settingModel.getData() != null && settingModel.getData().getTwitter() != null && !settingModel.getData().getTwitter().equals("#")) {
            open(settingModel.getData().getTwitter());

        } else {
            Toast.makeText(activity, R.string.not_avail_now, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInstagram() {
        if (settingModel != null && settingModel.getData() != null && settingModel.getData().getInstagram() != null && !settingModel.getData().getInstagram().equals("#")) {
            open(settingModel.getData().getInstagram());
        } else {
            Toast.makeText(activity, R.string.not_avail_now, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWhatsApp() {
        if (settingModel != null && settingModel.getData() != null && settingModel.getData().getWhatup() != null && !settingModel.getData().getWhatup().equals("#")) {
            open("https://api.whatsapp.com/send?phone=" + settingModel.getData().getWhatup());
        } else {
            Toast.makeText(activity, R.string.not_avail_now, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLogout() {
        activity.logout();
    }

    @Override
    public void onNotification() {
        if (userModel == null) {
            navigateToLoginActivity();
        } else {
            binding.setNotcount("0");
            Intent intent = new Intent(activity, NotificationActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            updateUserData();
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            String lang = data.getStringExtra("lang");
            activity.refreshActivity(lang);
        }
    }

    private void getNotificationCount() {
        if (userModel == null) {
            binding.setNotcount("0");

            return;
        }
        Api.getService(Tags.base_url).getNotificationCount(userModel.getData().getToken(), userModel.getData().getId())
                .enqueue(new Callback<NotificationCountModel>() {
                    @Override
                    public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                binding.setNotcount(String.valueOf(response.body().getData().getCount()));
                            }
                        } else {

                            try {
                                Log.e("error", response.code() + "_" + response.errorBody().string());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                            }


                        } catch (Exception e) {
                        }

                    }
                });
    }
    private void getSetting() {
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Api.getService(Tags.base_url).getSetting(lang)
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                settingModel = response.body();


                            }
                        } else {

                            dialog.dismiss();

                            try {
                                Log.e("error_code", response.code() + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    // Toast.makeText(SettingsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    //Toast.makeText(SettingsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }


                        } catch (Exception e) {

                        }
                    }
                });
    }
    private void open(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        startActivity(intent);
    }
}
