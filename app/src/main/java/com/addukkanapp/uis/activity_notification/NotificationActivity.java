package com.addukkanapp.uis.activity_notification;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.addukkanapp.R;
import com.addukkanapp.adapters.NotificationAdapter;
import com.addukkanapp.databinding.ActivityNotificationBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.NotificationDataModel;
import com.addukkanapp.models.NotificationModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private List<NotificationModel> notificationModelList;
    private NotificationAdapter adapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        initView();
    }


    private void initView() {
          new Handler(Looper.myLooper()).postDelayed(() -> {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager!=null){
                manager.cancel(Tags.not_tag,Tags.not_id);
            }
        },1000);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);

        notificationModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        binding.swipeRefresh.setColorSchemeResources(R.color.color2);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.color2), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(notificationModelList,this);
        binding.recView.setAdapter(adapter);

        binding.flBack.setOnClickListener(view -> finish());
        getNotifications();
        binding.swipeRefresh.setOnRefreshListener(this::getNotifications);
    }

    private void getNotifications() {
        try {

            if (userModel==null){
                binding.progBar.setVisibility(View.GONE);
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);

                return;
            }
            Api.getService(Tags.base_url)
                    .getNotifications(userModel.getData().getToken(),lang, userModel.getData().getId())
                    .enqueue(new Callback<NotificationDataModel>() {
                        @Override
                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        notificationModelList.clear();
                                        notificationModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                    }else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);
                                        notificationModelList.clear();
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    public void deleteNotification(NotificationModel notificationModel, int adapterPosition) {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .deleteNotification(userModel.getData().getToken(),notificationModel.getId())
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                notificationModelList.remove(adapterPosition);
                                adapter.notifyItemRemoved(adapterPosition);
                                if (notificationModelList.size()==0){
                                    binding.tvNoData.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(NotificationActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }



}
