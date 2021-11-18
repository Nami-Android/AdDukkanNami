package com.addukkanapp.uis.activity_order_detials;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.Order2ProductAdapter;
import com.addukkanapp.databinding.ActivityCompleteOrderDetailsBinding;
import com.addukkanapp.databinding.ActivityOrderDetailsBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.SingleOrderModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetialsActivity extends AppCompatActivity {
    private ActivityOrderDetailsBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang = "ar";
    private List<SingleOrderModel.Data.Detials> detialsList;
    private Order2ProductAdapter orderProductAdapter;
    private String id;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
      getDataFromIntent();
        initView();

    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

    }
    private void initView() {
        detialsList = new ArrayList<>();

        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        orderProductAdapter = new Order2ProductAdapter(detialsList, this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(orderProductAdapter);
        binding.llBack.setOnClickListener(view -> finish());
        getData();

    }
    public void getData() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }

        binding.progBar.setVisibility(View.VISIBLE);
        detialsList.clear();
        orderProductAdapter.notifyDataSetChanged();
        Log.e("Eerr",id);
        Api.getService(Tags.base_url)
                .getSingleOrder("Bearer " + userModel.getData().getToken(), lang, id)
                .enqueue(new Callback<SingleOrderModel>() {
                    @Override
                    public void onResponse(Call<SingleOrderModel> call, Response<SingleOrderModel> response) {
                        binding.progBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                if (response.body().getData() != null ) {
                                  updateData(response.body());
                                } else {
                                   // binding.tvNoData.setVisibility(View.VISIBLE);
                                }




                            }
                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                //Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleOrderModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //  Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void updateData(SingleOrderModel body) {
        Log.e("dldlld",body.getData().getAddress());
        detialsList.clear();
        binding.llData.setVisibility(View.VISIBLE);
        detialsList.addAll(body.getData().getOrder_details());
        orderProductAdapter.notifyDataSetChanged();
        binding.setModel(body.getData());

    }


}