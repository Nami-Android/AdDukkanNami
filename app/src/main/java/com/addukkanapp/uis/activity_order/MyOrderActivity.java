package com.addukkanapp.uis.activity_order;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.MyOrderAdapter;
import com.addukkanapp.databinding.ActivityCartBinding;
import com.addukkanapp.databinding.ActivityMyOrdersBinding;
import com.addukkanapp.databinding.CartProductRowBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.ALLOrderDataModel;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.SingleOrderModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_order_detials.OrderDetialsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMyOrdersBinding binding;
    private String lang;

    private LinearLayoutManager manager;
    private UserModel userModel;
    private Preferences preferences;

    private List<SingleOrderModel.Data> detialsList;
    private MyOrderAdapter myOrderAdapter;
  private String country_coude;
    private String currecny;    private AppLocalSettings settings;
    private String couponid = null;
    private String copoun;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders);
        initView();
    }


    private void initView() {
        detialsList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        preferences = Preferences.getInstance();
        settings = preferences.isLanguageSelected(this);
        userModel = preferences.getUserData(this);
         if (userModel != null) {
            country_coude = userModel.getData().getCountry_code();
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            country_coude = settings.getCountry_code();
            currecny=settings.getCurrency();
        }

        manager = new GridLayoutManager(this, 1);
        binding.recView.setLayoutManager(manager);
        myOrderAdapter = new MyOrderAdapter(detialsList, this);
        binding.recView.setAdapter(myOrderAdapter);

    }


    public void getData() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }

        binding.progBar.setVisibility(View.VISIBLE);
        detialsList.clear();
        myOrderAdapter.notifyDataSetChanged();
        Api.getService(Tags.base_url)
                .getMyOrder("Bearer " + userModel.getData().getToken(), lang, user_id)
                .enqueue(new Callback<ALLOrderDataModel>() {
                    @Override
                    public void onResponse(Call<ALLOrderDataModel> call, Response<ALLOrderDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);

                        //     Log.e("Dldldl",response.message());
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                detialsList.clear();
                                if (response.body().getData() != null) {
                                    detialsList.addAll(response.body().getData());
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);
                                }


                                if (detialsList.size() > 0) {
                                    myOrderAdapter.notifyDataSetChanged();

                                    binding.tvNoData.setVisibility(View.GONE);
                                    //Log.e(",dkdfkfkkfk", categoryDataModelDataList.get(0).getTitle());
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

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
                    public void onFailure(Call<ALLOrderDataModel> call, Throwable t) {
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


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void back() {
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void showDetials(String s) {
        Intent intent = new Intent(MyOrderActivity.this, OrderDetialsActivity.class);
        intent.putExtra("id", s);
        startActivity(intent);
    }
}
