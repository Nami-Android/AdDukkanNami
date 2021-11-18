package com.addukkanapp.uis.activity_cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.CartProductAdapter;
import com.addukkanapp.adapters.CartProductOfflineAdapter;
import com.addukkanapp.databinding.ActivityCartBinding;
import com.addukkanapp.databinding.CartProductRowBinding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.AddCartProductItemModel;
import com.addukkanapp.models.AddOrderModel;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.CouponDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.SelectedLocation;
import com.addukkanapp.models.SingleOrderModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_location_detials.LocationDetialsActivity;
import com.addukkanapp.uis.activity_login.LoginActivity;
import com.addukkanapp.uis.activity_map.MapActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityCartBinding binding;
    private String lang;
    private LinearLayoutManager manager;
    private UserModel userModel;
    private Preferences preferences;
    private List<CartDataModel.Data.Detials> detialsList;
    private CartDataModel.Data data2;
    private CartProductAdapter cartProductAdapter;
    private CartProductOfflineAdapter cartProductOfflineAdapter;
    private String country_coude;
    private String currecny;
    private AppLocalSettings settings;
    private String couponid = null;
    private String copoun,prescription_id;
    private String bill_code = "";
    private boolean isDataChanged = false;
    private AddCartDataModel createOrderModel;
    private List<AddCartProductItemModel> itemCartModelList;
    private double total;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.getData() != null) {
            bill_code = intent.getData().getLastPathSegment();
            Log.e("codeeeee", bill_code + "__");
        }
    }


    private void initView() {
        detialsList = new ArrayList<>();
        itemCartModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBar.setVisibility(View.GONE);
        preferences = Preferences.getInstance();
        settings = preferences.isLanguageSelected(this);
        createOrderModel = preferences.getCartData(this);

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
        if (userModel != null) {
            cartProductAdapter = new CartProductAdapter(detialsList, this);
            binding.recView.setAdapter(cartProductAdapter);
        } else {
            cartProductOfflineAdapter = new CartProductOfflineAdapter(itemCartModelList, this);
            binding.recView.setAdapter(cartProductOfflineAdapter);
            binding.flcontain.setVisibility(View.GONE);

        }
        binding.btcheck.setOnClickListener(v -> {
            String copun = binding.edtCopun.getText().toString();
            if (!copun.isEmpty()) {
                binding.edtCopun.setError(null);
                checkCoupon(copun);
            } else {
                binding.edtCopun.setError(getResources().getString(R.string.field_required));
            }
        });
        if (userModel != null) {
            if (bill_code.isEmpty()) {
                getData();
            } else {
                scanOrder(bill_code);
            }
        } else {
            if (createOrderModel != null) {
//                itemCartModelList.addAll(createOrderModel.getCart_products());
//                cartProductOfflineAdapter.notifyDataSetChanged();
                if (itemCartModelList.size() > 0) {
                    binding.tvNoData.setVisibility(View.GONE);
                    binding.fltotal.setVisibility(View.VISIBLE);
                    binding.progBar.setVisibility(View.GONE);
                } else {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.fltotal.setVisibility(View.GONE);
                    binding.progBar.setVisibility(View.GONE);
                }
                calculateTotal();
            }
        }
        binding.btBuy.setOnClickListener(v -> {
            if (userModel != null) {
                Intent intent = new Intent(CartActivity.this, MapActivity.class);
                startActivityForResult(intent, 100);
            } else {
                navigateToSignInActivity();
            }
        });
    }


    public void getData() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }

        binding.progBar.setVisibility(View.VISIBLE);
        detialsList.clear();
        cartProductAdapter.notifyDataSetChanged();
        Log.e("sllsks", user_id + "" + lang + "Bearer " + userModel.getData().getToken());
        Api.getService(Tags.base_url)
                .getMyCart("Bearer " + userModel.getData().getToken(), lang, user_id)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);

                        //     Log.e("Dldldl",response.message());
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                detialsList.clear();
                                if (response.body().getData() != null && response.body().getData().getDetails() != null) {
                                    detialsList.addAll(response.body().getData().getDetails());
                                    binding.setModel(response.body().getData());
                                    binding.setTotal(response.body().getData().getTotal_price() + "");
                                    data2 = response.body().getData();
                                } else {
                                    binding.flcontain.setVisibility(View.GONE);
                                    binding.fltotal.setVisibility(View.GONE);
                                }


                                if (detialsList.size() > 0) {
                                    cartProductAdapter.notifyDataSetChanged();

                                    binding.tvNoData.setVisibility(View.GONE);
                                    binding.fltotal.setVisibility(View.VISIBLE);
                                    binding.flcontain.setVisibility(View.VISIBLE);
                                    //Log.e(",dkdfkfkkfk", categoryDataModelDataList.get(0).getTitle());
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);
                                    binding.fltotal.setVisibility(View.GONE);
                                    binding.flcontain.setVisibility(View.GONE);
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
                    public void onFailure(Call<CartDataModel> call, Throwable t) {
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

    private void checkCoupon(String coupon_num) {
        binding.progBarcopun.setVisibility(View.VISIBLE);
        Api.getService(Tags.base_url).checkCoupon("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", coupon_num)
                .enqueue(new Callback<CouponDataModel>() {
                    @Override
                    public void onResponse(Call<CouponDataModel> call, Response<CouponDataModel> response) {
                        binding.progBarcopun.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                //     Toast.makeText(SignUpAdvisorActivity.this, R.string.coupon_vaild, Toast.LENGTH_SHORT).show();
                                if (response.body().getStatus() == 200) {
                                     UpdateData(response.body());


                                } else if (response.body().getStatus() == 406) {


                                    Toast.makeText(CartActivity.this, R.string.expierd, Toast.LENGTH_SHORT).show();
                                } else if (response.body().getStatus() == 404) {


                                    Toast.makeText(CartActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            binding.progBarcopun.setVisibility(View.GONE);
                            if (response.code() == 422) {
                                //     Toast.makeText(SignUpAdvisorActivity.this, R.string.inv_coupon, Toast.LENGTH_SHORT).show();


                            }
                            try {
                                Log.e("error_code", response.code() + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<CouponDataModel> call, Throwable t) {
                        try {
                            binding.progBarcopun.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //Toast.makeText(SignUpAdvisorActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    // Toast.makeText(SignUpAdvisorActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }


                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void UpdateData(CouponDataModel body) {
        couponid = body.getData().getId() + "";

        if (body.getData().getType().equals("value")) {
            copoun = body.getData().getDiscount_val() + "";
            binding.tvtotal.setText(getResources().getString(R.string.total) + Math.round((Double.parseDouble(binding.tvtotal.getText().toString().replaceAll(getResources().getString(R.string.total), "")) - body.getData().getDiscount_val())));
        } else {
            copoun = (body.getData().getDiscount_val() * Double.parseDouble(binding.tvtotal.getText().toString().replaceAll(getResources().getString(R.string.total), "")) / 100) + "";
            binding.tvtotal.setText(getResources().getString(R.string.total) + Math.round((Double.parseDouble(binding.tvtotal.getText().toString().replaceAll(getResources().getString(R.string.total), "")) - (body.getData().getDiscount_val() * Double.parseDouble(binding.tvtotal.getText().toString().replaceAll(getResources().getString(R.string.total), "")) / 100))));

        }
    }

    private void scanOrder(String barcode) {


        Api.getService(Tags.base_url)
                .scanOrder("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", barcode, country_coude)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                getData();

                            } else if (response.body().getStatus() == 404) {
                                Toast.makeText(CartActivity.this, getString(R.string.not_found), Toast.LENGTH_SHORT).show();

                            } else if (response.body().getStatus() == 406) {
                                Toast.makeText(CartActivity.this, getString(R.string.no_product), Toast.LENGTH_SHORT).show();

                            } else {
                                // Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                            try {
                                Toast.makeText(CartActivity.this, response.errorBody().string()+"__", Toast.LENGTH_SHORT).show();
//                                Log.e("cccccc", response.errorBody().string()+"");
//                                Log.e("cccccc", "ttttttttt");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {

                                Toast.makeText(CartActivity.this, "server error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CartActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(CartActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CartActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }


    private void addTocart(CartDataModel.Data.Detials addCartDataModel, String increment, int pos) {


        Api.getService(Tags.base_url)
                .incrementDecrementCart("Bearer " + userModel.getData().getToken(), country_coude, userModel.getData().getId() + "", addCartDataModel.getId() + "", addCartDataModel.getCart_id() + "", 1 + "", increment)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                detialsList.clear();
                                isDataChanged = true;
                                if (response.body().getData() != null && response.body().getData().getDetails() != null) {
                                    detialsList.addAll(response.body().getData().getDetails());
                                    getData();
                                    binding.tvtotal.setText(response.body().getData().total_price + "");

                                }
                                cartProductAdapter.notifyDataSetChanged();
                                if (detialsList.size() == 0) {
                                    CartActivity.this.binding.tvNoData.setVisibility(View.VISIBLE);
                                } else {
                                    CartActivity.this.binding.tvNoData.setVisibility(View.GONE);
                                }

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
                    public void onFailure(Call<CartDataModel> call, Throwable t) {
                        try {
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

    public void additemtoCart(CartDataModel.Data.Detials detials, int pos, String increment) {
        addTocart(detials, increment, pos);
    }

    public void deleteItemFromcart(CartDataModel.Data.Detials addCartDataModel) {


        Api.getService(Tags.base_url)
                .deleteItemCart("Bearer " + userModel.getData().getToken(), addCartDataModel.getId() + "", addCartDataModel.getCart_id() + "")
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                detialsList.clear();
                                isDataChanged = true;
                                if (response.body().getData() != null && response.body().getData().getDetails() != null) {
                                    detialsList.addAll(response.body().getData().getDetails());
                                    getData();
                                    binding.tvtotal.setText(response.body().getData().total_price + "");
                                }
                                cartProductAdapter.notifyDataSetChanged();
                                if (detialsList.size() == 0) {
                                    binding.tvNoData.setVisibility(View.VISIBLE);
                                    binding.fltotal.setVisibility(View.GONE);
                                    binding.flcontain.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.GONE);
                                    binding.fltotal.setVisibility(View.VISIBLE);
                                    binding.flcontain.setVisibility(View.VISIBLE);


                                }

//                                if(increment.equals("increment")) {
//                                    binding.tvCounter.setText((Integer.parseInt(binding.tvCounter.getText().toString()) + 1) + "");
//                                }
//                                else {
//                                    binding.tvCounter.setText((Integer.parseInt(binding.tvCounter.getText().toString()) - 1) + "");
//
//                                }
//                                binding.tvtotal.setText((Integer.parseInt(binding.tvCounter.getText().toString())*addCartDataModel.getPrice())+"");
//if(addCartDataModel.getHave_offer().equals("yes")){
//    if(addCartDataModel.getOffer_type().equals("amount")){
//
//    }
//}
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
                    public void onFailure(Call<CartDataModel> call, Throwable t) {
                        try {

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            SelectedLocation location = (SelectedLocation) data.getSerializableExtra("location");
            AddOrderModel addOrderModel = new AddOrderModel();
            addOrderModel.setAddress(location.getAddress());
            addOrderModel.setAddress_lat(location.getLat() + "");
            addOrderModel.setAddress_long(location.getLng() + "");
            addOrderModel.setCountry_code(country_coude);
            addOrderModel.setCoupon_id(couponid);
            addOrderModel.setName(userModel.getData().getName());
            addOrderModel.setPhone(userModel.getData().getPhone());
            addOrderModel.setNotes("");
            addOrderModel.setPhone_code(userModel.getData().getPhone_code());
            addOrderModel.setShipping("0");
            addOrderModel.setPayment_method("when_recieving");
            addOrderModel.setUser_id(userModel.getData().getId());
            addOrderModel.setProduct_list(detialsList);
            addOrderModel.setSubtotal(data2.getTotal_price() + "");
            addOrderModel.setTotal_payments(data2.getTotal_price() + "");
            addOrderModel.setCopoun(copoun);
            if (data2.getPrescription_id()==null){
                addOrderModel.setPrescription_id("");

            }else {
                addOrderModel.setPrescription_id(data2.getPrescription_id()+"");

            }

            Intent intent = new Intent(CartActivity.this, LocationDetialsActivity.class);
            intent.putExtra("data", addOrderModel);
            startActivity(intent);
        }
    }

    private void createOrder(AddOrderModel addOrderModel) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //   Log.e("sllsks", user_id + lang + country_coude);
        Api.getService(Tags.base_url)
                .addOrder("Bearer " + userModel.getData().getToken(), addOrderModel)
                .enqueue(new Callback<SingleOrderModel>() {
                    @Override
                    public void onResponse(Call<SingleOrderModel> call, Response<SingleOrderModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                getData();

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

    @Override
    protected void onResume() {
        super.onResume();
        if (userModel != null) {
            getData();
        } else {
            createOrderModel = preferences.getCartData(this);
            if (createOrderModel != null) {
                itemCartModelList.clear();
                itemCartModelList.addAll(createOrderModel.getCart_products());
                cartProductOfflineAdapter.notifyDataSetChanged();
                if (itemCartModelList.size() > 0) {
                    binding.tvNoData.setVisibility(View.GONE);
                    binding.fltotal.setVisibility(View.VISIBLE);
                    binding.progBar.setVisibility(View.GONE);
                } else {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.fltotal.setVisibility(View.GONE);
                    binding.progBar.setVisibility(View.GONE);
                }
                calculateTotal();

            }
        }
    }


    public void increase_decrease(AddCartProductItemModel model, int adapterPosition) {
        itemCartModelList.set(adapterPosition, model);
        cartProductOfflineAdapter.notifyItemChanged(adapterPosition);

        createOrderModel.setCart_products(itemCartModelList);
        preferences.create_update_cart(this, createOrderModel);
        calculateTotal();

    }

    private void calculateTotal() {
        total = 0;
        for (AddCartProductItemModel model : itemCartModelList) {

            total += model.getAmount() * model.getPrice();

        }
        binding.setTotal(total + "");
        //   binding.tvtotal.setText(String.valueOf(total));
    }

    public void deleteItem(AddCartProductItemModel model2, int adapterPosition) {
        itemCartModelList.remove(adapterPosition);
        cartProductOfflineAdapter.notifyItemRemoved(adapterPosition);
        createOrderModel.setCart_products(itemCartModelList);
        preferences.create_update_cart(this, createOrderModel);
        isDataChanged = true;
        calculateTotal();
        if (itemCartModelList.size() == 0) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.fltotal.setVisibility(View.GONE);
            binding.flcontain.setVisibility(View.GONE);
            preferences.clearCart(this);
        }
    }

    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
