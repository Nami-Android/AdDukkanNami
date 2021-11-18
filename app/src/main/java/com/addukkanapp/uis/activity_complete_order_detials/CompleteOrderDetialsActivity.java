package com.addukkanapp.uis.activity_complete_order_detials;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.OrderProductAdapter;
import com.addukkanapp.databinding.ActivityAddressInformationBinding;
import com.addukkanapp.databinding.ActivityCompleteOrderDetailsBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AddOrderModel;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.SingleOrderModel;
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

public class CompleteOrderDetialsActivity extends AppCompatActivity {
    private ActivityCompleteOrderDetailsBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang = "ar";
    private AddOrderModel addorderModel;
    private List<CartDataModel.Data.Detials> detialsList;
    private OrderProductAdapter orderProductAdapter;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_order_details);
        getDataFromIntent();
        initView();

    }

    private void initView() {
        detialsList = new ArrayList<>();
        detialsList.addAll(addorderModel.getProduct_list());
        binding.setTitle(addorderModel.getAddress());
        binding.setModel(addorderModel);
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        orderProductAdapter = new OrderProductAdapter(detialsList, this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(orderProductAdapter);
        binding.llBack.setOnClickListener(view -> finish());
        binding.radiocash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addorderModel.setPayment_method("when_recieving");
                binding.radioonline.setChecked(false);

            }
        });
        binding.radioonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addorderModel.setPayment_method("online");
                binding.radiocash.setChecked(false);

            }
        });
        binding.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(addorderModel);
            }
        });
        binding.flData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createOrder(AddOrderModel addOrderModel) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
          Log.e("sssssssssssssss", addOrderModel.getTotal_payments()+"____");
        Log.e("sssssssssssssss", addOrderModel.getSubtotal()+"____");

        Api.getService(Tags.base_url)
                .addOrder("Bearer " + userModel.getData().getToken(), addOrderModel)
                .enqueue(new Callback<SingleOrderModel>() {
                    @Override
                    public void onResponse(Call<SingleOrderModel> call, Response<SingleOrderModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                binding.flData.setVisibility(View.VISIBLE);
                                binding.btnComplete.setVisibility(View.GONE);
                                Handler h2 = new Handler();

                                CompleteOrderDetialsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    h2.postDelayed(this, 500);
                                    finish();
                                }
                            });
                            }
                        } else {

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
                            dialog.dismiss();
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

}