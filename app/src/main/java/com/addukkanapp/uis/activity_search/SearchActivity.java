package com.addukkanapp.uis.activity_search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.ProductLisProductAdapter;
import com.addukkanapp.adapters.ProductOfferAdapter;
import com.addukkanapp.databinding.ActivityProductFilterBinding;
import com.addukkanapp.databinding.ListProductRowBinding;
import com.addukkanapp.databinding.OfferProductRowBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.ALLProductDataModel;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.AddCartProductItemModel;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.FilterModel;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_filter_search.FilterSearchActivity;
import com.addukkanapp.uis.activity_login.LoginActivity;
import com.addukkanapp.uis.activity_product_detials.ProductDetialsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProductFilterBinding binding;
    private String lang;
    private FilterModel filterModel;
    private Preferences preferences;
    private UserModel userModel;
    private AppLocalSettings settings;
    private MainCategoryDataModel.Data sub_departments;
    private List<Integer> departments;
    private List<Integer> brand_id;
    private List<Integer> product_company_id;
    private List<SingleProductModel> productModelList;
    private ProductOfferAdapter product2Adapter;
    private ProductLisProductAdapter productLisProductAdapter;
  private String country_coude;
    private String currecny;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_filter);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            sub_departments = (MainCategoryDataModel.Data) intent.getSerializableExtra("data");

        }
    }

    private void initView() {
        filterModel = new FilterModel();
        departments = new ArrayList<>();
        brand_id = new ArrayList<>();
        product_company_id = new ArrayList<>();
        productModelList = new ArrayList<>();
        product2Adapter = new ProductOfferAdapter(productModelList, this, null);
        productLisProductAdapter = new ProductLisProductAdapter(productModelList, this, null);

        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.anim);

        binding.recView.setLayoutAnimation(controller);
        binding.recView.setAdapter(product2Adapter);
        binding.recView.scheduleLayoutAnimation();
        binding.imagfilteraccord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSheet();
            }
        });
        binding.imfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, FilterSearchActivity.class);
                intent.putExtra("data", sub_departments);
                startActivityForResult(intent, 100);
            }
        });
        filterModel.setDepartments(departments);
        filterModel.setBrand_id(brand_id);
        filterModel.setSeach_name("all");
        filterModel.setProduct_company_id(product_company_id);
        preferences = Preferences.getInstance();
        binding.setTitle(getResources().getString(R.string.search2));
        settings = preferences.isLanguageSelected(this);

        userModel = preferences.getUserData(this);
         if (userModel != null) {
            country_coude = userModel.getData().getCountry_code();
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            country_coude = settings.getCountry_code();
            currecny=settings.getCurrency();
        }
        if (userModel != null) {
            filterModel.setCountry_code(userModel.getData().getCountry_code());
        } else {
            filterModel.setCountry_code(settings.getCountry_code());
        }
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.flData.setOnClickListener(v -> openSheet());
        binding.cardclose.setOnClickListener(v -> closeSheet());
        //binding.progBar.setVisibility(View.GONE);

        // binding.recView.scheduleLayoutAnimation();
        binding.imlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imlist.setColorFilter(ContextCompat.getColor(SearchActivity.this, R.color.colorAccent));
                binding.immenu.setColorFilter(ContextCompat.getColor(SearchActivity.this, R.color.gray11));

                binding.recView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                binding.recView.setAdapter(productLisProductAdapter);

            }
        });
        binding.immenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.immenu.setColorFilter(ContextCompat.getColor(SearchActivity.this, R.color.colorAccent));
                binding.imlist.setColorFilter(ContextCompat.getColor(SearchActivity.this, R.color.gray11));
                binding.recView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                binding.recView.setAdapter(product2Adapter);
            }
        });
        binding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == binding.rbRate.getId()) {
                    filterModel.setPrice_order("all");
                    filterModel.setProduct_order("all");
                    filterModel.setRate_order("asc");


                } else if (checkedId == binding.rbRecent.getId()) {
                    filterModel.setPrice_order("all");
                    filterModel.setProduct_order("asc");
                    filterModel.setRate_order("all");
                } else if (checkedId == binding.rbLowPrice.getId()) {
                    filterModel.setPrice_order("lowest_price");
                    filterModel.setProduct_order("all");
                    filterModel.setRate_order("all");
                } else if (checkedId == binding.rbHighPrice.getId()) {
                    filterModel.setPrice_order("highest_price");
                    filterModel.setProduct_order("all");
                    filterModel.setRate_order("all");
                }
                closeSheet();
            }
        });
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    filterModel.setSeach_name("all");
                } else {


                    filterModel.setSeach_name(editable.toString());
                }
                filterData();
            }
        });
        filterData();

    }

    private void openSheet() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        binding.flData.clearAnimation();
        binding.flData.startAnimation(animation);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.flData.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void closeSheet() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        binding.flData.clearAnimation();
        binding.flData.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.flData.setVisibility(View.GONE);
                filterData();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void filterData() {
        productModelList.clear();
        product2Adapter.notifyDataSetChanged();
        productLisProductAdapter.notifyDataSetChanged();
        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }
        filterModel.setUser_id(user_id);
        binding.progBar.setVisibility(View.VISIBLE);
        //   Log.e("sllsks", user_id + lang + country_coude);
        Api.getService(Tags.base_url)
                .Filter(lang, filterModel)
                .enqueue(new Callback<ALLProductDataModel>() {
                    @Override
                    public void onResponse(Call<ALLProductDataModel> call, Response<ALLProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        Log.e("Slslls", response.message());
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                productModelList.clear();
                                productModelList.addAll(response.body().getData());

                                if (productModelList.size() > 0) {
                                    product2Adapter.notifyDataSetChanged();
                                    productLisProductAdapter.notifyDataSetChanged();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            FilterModel filterModel = (FilterModel) data.getSerializableExtra("data");
            this.filterModel.setProduct_company_id(filterModel.getProduct_company_id());
            this.filterModel.setDepartments(filterModel.getDepartments());
            this.filterModel.setBrand_id(filterModel.getBrand_id());
            filterData();
        }

    }

    public void like_dislike(SingleProductModel productModel, int pos, int i) {
        if (userModel != null) {
            try {
                Log.e("llll", productModel.getId() + "");

                Api.getService(Tags.base_url)
                        .addFavoriteProduct("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", productModel.getId() + "")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                Log.e("dlldl", response.body().getStatus() + "");
                                if (response.isSuccessful() && response.body().getStatus() == 200) {
                                    filterData();


                                } else {


                                    if (response.code() == 500) {
                                        //      Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                    } else {
                                        //       Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                        try {

                                            Log.e("error", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
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
            navigateToSignInActivity();
            // Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));

        }
    }

    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void additemtoCart(SingleProductModel data, ListProductRowBinding binding) {
        if (userModel != null) {
            binding.progBar.setVisibility(View.VISIBLE);
            AddCartDataModel addCartDataModel;

            if (userModel != null) {
                addCartDataModel = new AddCartDataModel();
            } else {
                addCartDataModel = preferences.getCartData(this);
                if (addCartDataModel == null) {
                    addCartDataModel = new AddCartDataModel();
                }
            }
            List<AddCartProductItemModel> addCartProductItemModelList;
            if (addCartDataModel.getCart_products() != null) {
                addCartProductItemModelList = addCartDataModel.getCart_products();
            } else {
                addCartProductItemModelList = new ArrayList<>();
            }
            AddCartProductItemModel addCartProductItemModel = new AddCartProductItemModel();
            addCartDataModel.setCountry_code(country_coude);
            if (userModel != null) {
                addCartDataModel.setUser_id(userModel.getData().getId());
            }
            double totalprice = 0;
            if (data.getHave_offer().equals("yes")) {
                if (data.getOffer_type().equals("value")) {
                    totalprice = data.getProduct_default_price().getPrice() - data.getOffer_value();
                } else if (data.getOffer_type().equals("per")) {
                    totalprice = (data.getProduct_default_price().getPrice()) - ((data.getProduct_default_price().getPrice() * data.getOffer_value()) / 100);
                } else {
                    totalprice = data.getProduct_default_price().getPrice();

                }
            } else {
                totalprice = data.getProduct_default_price().getPrice();
            }
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
            addCartProductItemModelList.add(addCartProductItemModel);
            addCartDataModel.setCart_products(addCartProductItemModelList);
            addTocart(addCartDataModel, binding);
        } else {
            navigateToSignInActivity();
        }
    }

    private void addTocart(AddCartDataModel addCartDataModel, ListProductRowBinding binding) {

        binding.imgIncrease.setClickable(false);

        //   Log.e("sllsks", user_id + lang + country_coude);
        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        binding.imgIncrease.setClickable(true);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                binding.tvCounter.setText((Integer.parseInt(binding.tvCounter.getText().toString()) + 1) + "");


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
                            binding.imgIncrease.setClickable(true);
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

    public void additemtoCart(SingleProductModel data, int adapterPosition, int type) {
        AddCartDataModel addCartDataModel;

        if (userModel != null) {
            addCartDataModel = new AddCartDataModel();
        } else {
            addCartDataModel = preferences.getCartData(this);
            if (addCartDataModel == null) {
                addCartDataModel = new AddCartDataModel();
            }
        }
        List<AddCartProductItemModel> addCartProductItemModelList;
        if (addCartDataModel.getCart_products() != null) {
            addCartProductItemModelList = addCartDataModel.getCart_products();
        } else {
            addCartProductItemModelList = new ArrayList<>();
        }
        AddCartProductItemModel addCartProductItemModel = new AddCartProductItemModel();
        addCartDataModel.setCountry_code(country_coude);
        if (userModel != null) {
            addCartDataModel.setUser_id(userModel.getData().getId());
        }
        double totalprice = 0;
        if (data.getHave_offer().equals("yes")) {
            if (data.getOffer_type().equals("value")) {
                totalprice = data.getProduct_default_price().getPrice() - data.getOffer_value();
            } else if (data.getOffer_type().equals("per")) {
                totalprice = (data.getProduct_default_price().getPrice()) - ((data.getProduct_default_price().getPrice() * data.getOffer_value()) / 100);
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
            int pos = -1;
            for (int i = 0; i < addCartProductItemModelList.size(); i++) {
                if (addCartProductItemModelList.get(i).getProduct_id().equals(data.getId() + "")) {
                    addCartProductItemModel = addCartProductItemModelList.get(i);
                    pos = i;
                    break;
                }
            }
            if (pos > -1) {
                addCartProductItemModel.setAmount(addCartProductItemModel.getAmount() + 1);
                addCartProductItemModelList.set(pos, addCartProductItemModel);
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
            addTocart(addCartDataModel, data, adapterPosition, type);
        } else {

        //    data.setLoading(false);

            data.setAmount(data.getAmount() + 1);
            preferences.create_update_cart(this, addCartDataModel);
            productModelList.set(adapterPosition, data);
            product2Adapter.notifyItemChanged(adapterPosition);
        }

    }

    private void addTocart(AddCartDataModel addCartDataModel, SingleProductModel data, int adapterPosition, int type) {

        //   Log.e("sllsks", user_id + lang + country_coude);
        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        data.setLoading(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                data.setAmount(data.getAmount() + 1);

                                productModelList.set(adapterPosition, data);
                                product2Adapter.notifyItemChanged(adapterPosition);

                                //binding.set(response.body().getData().getDetails().size() + "");

                            }
                        } else {
                            productModelList.set(adapterPosition, data);
                            product2Adapter.notifyItemChanged(adapterPosition);
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
                            productModelList.set(adapterPosition, data);
                            product2Adapter.notifyItemChanged(adapterPosition);

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

    public void showData(String s) {
        Intent intent = new Intent(this, ProductDetialsActivity.class);
        intent.putExtra("id", s);
        startActivityForResult(intent, 100);
    }
}
