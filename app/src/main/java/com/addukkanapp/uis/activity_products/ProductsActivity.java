package com.addukkanapp.uis.activity_products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.addukkanapp.R;
import com.addukkanapp.adapters.Product4Adapter;
import com.addukkanapp.databinding.ActivityProductsBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.ALLProductDataModel;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.AddCartProductItemModel;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_login.LoginActivity;
import com.addukkanapp.uis.activity_product_detials.ProductDetialsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProductsBinding binding;
    private String lang;
    private LinearLayoutManager manager;
    private UserModel userModel;
    private Preferences preferences;
    private List<SingleProductModel> list;
    private Product4Adapter adapter;
  private String country_coude;
    private String currecny;    private AppLocalSettings settings;
    private String type = "";
    private boolean isDataChange = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

    }


    private void initView() {
        list = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        preferences = Preferences.getInstance();
        settings = preferences.isLanguageSelected(this);
        userModel = preferences.getUserData(this);
        if (userModel != null) {
            country_coude = userModel.getData().getCountry_code();
            Log.e("Ddd", country_coude+"__");
        } else {
            country_coude = settings.getCountry_code();
        }

        manager = new GridLayoutManager(this, 2);
        binding.recView.setLayoutManager(manager);
        adapter = new Product4Adapter(list, this);
        binding.recView.setAdapter(adapter);

        if (type.equals("mostSeller")){
            getMostSell();
        }else {
            getRecentArrived();
        }
    }



    public void getRecentArrived() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
            Log.e("user_id", user_id+"__");

        }
        Api.getService(Tags.base_url)
                .getRecentlyArrived(lang, user_id, country_coude, "off")
                .enqueue(new Callback<ALLProductDataModel>() {
                    @Override
                    public void onResponse(Call<ALLProductDataModel> call, Response<ALLProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                list.clear();
                                list.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();


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
                    public void onFailure(Call<ALLProductDataModel> call, Throwable t) {
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

    public void getMostSell() {

        Log.e("ff", "yyyy");

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
            Log.e("user_id", user_id+"__");
        }
        Api.getService(Tags.base_url)
                .getMostSell(lang, user_id, country_coude, "off")
                .enqueue(new Callback<ALLProductDataModel>() {
                    @Override
                    public void onResponse(Call<ALLProductDataModel> call, Response<ALLProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                list.clear();
                                list.addAll(response.body().getData());

                                if (list.size() > 0) {
                                    adapter.notifyDataSetChanged();

                                } else {

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
                    public void onFailure(Call<ALLProductDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //         Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
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
        if (isDataChange){
            setResult(RESULT_OK);
        }
        finish();
    }



    public void showData(String s) {
        Intent intent = new Intent(this, ProductDetialsActivity.class);
        intent.putExtra("id", s);
        startActivityForResult(intent, 100);
    }
    public void like_dislike(SingleProductModel productModel, int pos) {
        if (userModel != null) {
            try {

                Api.getService(Tags.base_url)
                        .addFavoriteProduct("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", productModel.getId() + "")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                if (response.isSuccessful() && response.body().getStatus() == 200) {
                                    isDataChange = true;
                                    if (type.equals("mostSeller")){
                                        getMostSell();
                                    }else {
                                        getRecentArrived();
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
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                try {

                                    if (t.getMessage() != null) {
                                        Log.e("error", t.getMessage());
                                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                            //            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                        } else {
                                            //          Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        } else {


        }
    }

    public void addToCart(SingleProductModel data, int pos) {

                 AddCartDataModel addCartDataModel;

        if(userModel!=null){
            addCartDataModel = new AddCartDataModel();
        }
        else {
            addCartDataModel=preferences.getCartData(this);
            if(addCartDataModel==null){
                addCartDataModel=new AddCartDataModel();
            }
        }
        List<AddCartProductItemModel> addCartProductItemModelList;
        if(addCartDataModel.getCart_products()!=null){
             addCartProductItemModelList=addCartDataModel.getCart_products();
         }
         else {
          addCartProductItemModelList  = new ArrayList<>();
         }
            AddCartProductItemModel addCartProductItemModel = new AddCartProductItemModel();
            addCartDataModel.setCountry_code(country_coude);
 if(userModel!=null){
            addCartDataModel.setUser_id(userModel.getData().getId());}double totalprice = 0;
            if (data.getHave_offer().equals("yes")) {
                if (data.getOffer_type().equals("value")) {
                    totalprice = data.getProduct_default_price().getPrice() - data.getOffer_value();
                } else if (data.getOffer_type().equals("per")) {
                    totalprice = (data.getProduct_default_price().getPrice()) - ((data.getProduct_default_price().getPrice() * data.
                            getOffer_value()) / 100);
                } else {
                    totalprice = data.getProduct_default_price().getPrice();

                }
            } else {
                totalprice = data.getProduct_default_price().getPrice();
            }
        if (userModel != null) {
            addCartDataModel.setTotal_price(totalprice);
            addCartProductItemModel.setAmount(1);
            addCartProductItemModel.setHave_offer(data.getHave_offer());
            addCartProductItemModel.setOffer_bonus(data.getOffer_bonus());
            addCartProductItemModel.setOffer_min(data.getOffer_min());
            addCartProductItemModel.setOffer_type(data.getOffer_type());
            addCartProductItemModel.setOld_price(data.getProduct_default_price().getPrice());
            addCartProductItemModel.setPrice(totalprice);
            addCartProductItemModel.setProduct_id(data.getId() + "");
            addCartProductItemModel.setOffer_value(data.getOffer_value());
            addCartProductItemModel.setProduct_price_id(data.getProduct_default_price().getId() + "");
            addCartProductItemModel.setVendor_id(data.getVendor_id() + "");
            addCartProductItemModel.setName(data.getProduct_trans_fk().getTitle());
            addCartProductItemModel.setImage(data.getMain_image());
            addCartProductItemModel.setRate(data.getRate());
            addCartProductItemModel.setDesc(data.getProduct_trans_fk().getDescription());
            addCartProductItemModelList.add(addCartProductItemModel);
            addCartDataModel.setCart_products(addCartProductItemModelList);
        }
        else {
            int pos1 = -1;
            for (int i = 0; i < addCartProductItemModelList.size(); i++) {
                if (addCartProductItemModelList.get(i).getProduct_id().equals(data.getId() + "")) {
                    addCartProductItemModel = addCartProductItemModelList.get(i);
                    pos1 = i;
                    break;
                }
            }
            if (pos1 > -1) {
                addCartProductItemModel.setAmount(addCartProductItemModel.getAmount() + 1);
                addCartProductItemModelList.set(pos1, addCartProductItemModel);
                addCartDataModel.setCart_products(addCartProductItemModelList);

            } else {
                addCartDataModel.setTotal_price(totalprice);
                addCartProductItemModel.setAmount(1);
                addCartProductItemModel.setHave_offer(data.getHave_offer());
                addCartProductItemModel.setOffer_bonus(data.getOffer_bonus());
                addCartProductItemModel.setOffer_min(data.getOffer_min());
                addCartProductItemModel.setOffer_type(data.getOffer_type());
                addCartProductItemModel.setOld_price(data.getProduct_default_price().getPrice());
                addCartProductItemModel.setPrice(totalprice);
                addCartProductItemModel.setProduct_id(data.getId() + "");
                addCartProductItemModel.setOffer_value(data.getOffer_value());
                addCartProductItemModel.setProduct_price_id(data.getProduct_default_price().getId() + "");
                addCartProductItemModel.setVendor_id(data.getVendor_id() + "");
                addCartProductItemModel.setName(data.getProduct_trans_fk().getTitle());
                addCartProductItemModel.setImage(data.getMain_image());
                addCartProductItemModel.setRate(data.getRate());
                addCartProductItemModel.setDesc(data.getProduct_trans_fk().getDescription());
                addCartProductItemModelList.add(addCartProductItemModel);
                addCartDataModel.setCart_products(addCartProductItemModelList);
            }

        }
            if (userModel != null) {
                uploadData(addCartDataModel,data,pos);
            } else {

               //  data.setLoading(false);

                data.setAmount(data.getAmount() + 1);
            preferences.create_update_cart(this, addCartDataModel);
            list.set(pos, data);
            adapter.notifyItemChanged(pos);
            }




    }

    private void uploadData(AddCartDataModel addCartDataModel, SingleProductModel model, int pos ) {

        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        model.setLoading(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                model.setAmount(model.getAmount()+1);
                                list.set(pos,model);
                                adapter.notifyItemChanged(pos);


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

                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }


    private void navigateToSignInActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            if (type.equals("mostSeller")){
                getMostSell();
            }else {
                getRecentArrived();
            }


        }else if (requestCode==200&&resultCode==RESULT_OK){
            userModel = preferences.getUserData(this);
        }
    }

}