package com.addukkanapp.uis.activity_product_detials;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.adapters.CommentAdapter;
import com.addukkanapp.adapters.Product3Adapter;
import com.addukkanapp.adapters.ProductAttrAdapter;
import com.addukkanapp.adapters.ProductChildParentAttrAdapter;
import com.addukkanapp.adapters.SliderProductAdapter;
import com.addukkanapp.databinding.ActivityProductDetialsBinding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.AddCartProductItemModel;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.AttrDataModel;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.ProductDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetialsActivity extends AppCompatActivity {
    private ActivityProductDetialsBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang = "ar";
    private List<ProductDataModel.ParentAttributes> list;
    private ProductAttrAdapter parentAdapter;
    private ProductChildParentAttrAdapter childAdapter;
    private List<ProductDataModel.Attribute> childList;
    private String id;
    private AppLocalSettings settings;
  private String country_coude;
    private String currecny;
    private List<ProductDataModel.Attribute> data = new ArrayList<>();
    private SingleProductModel singleProductModel;
    private double price = 0.0, oldPrice = 0.0;
    private int counter = 0;
    private boolean isDataChanged = false;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detials);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

    }

    private void initView() {

        list = new ArrayList<>();
        childList = new ArrayList<>();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        settings = preferences.isLanguageSelected(this);


         if (userModel != null) {
            country_coude = userModel.getData().getCountry_code();
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            country_coude = settings.getCountry_code();
            currecny=settings.getCurrency();
        }
         binding.setCurrency(currecny);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        parentAdapter = new ProductAttrAdapter(this, list);
        binding.recViewParent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.recViewParent.setAdapter(parentAdapter);


        binding.llBack.setOnClickListener(view ->
                {
                    if (isDataChanged) {
                        setResult(RESULT_OK);
                    }
                    finish();
                }
        );
        binding.tab1.addTab(binding.tab1.newTab().setText(getString(R.string.description)));
        binding.tab1.addTab(binding.tab1.newTab().setText(getString(R.string.reviews)));

        binding.tab1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) {
                    binding.flComments.setVisibility(View.GONE);
                    binding.tvDetails.setVisibility(View.VISIBLE);
                } else {
                    binding.flComments.setVisibility(View.VISIBLE);
                    binding.tvDetails.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.imageIncrease.setOnClickListener(v -> {
            addItemToCart();
        });


        binding.checkbox.setOnClickListener(v -> {
            boolean checked = binding.checkbox.isChecked();
            if (userModel != null) {
                like_dislikeMain(singleProductModel);

            } else {
                binding.checkbox.setChecked(!checked);
            }
        });

        getData();


    }

    public void getData() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }

        binding.progBar.setVisibility(View.VISIBLE);
        list.clear();
        parentAdapter.notifyDataSetChanged();
        Api.getService(Tags.base_url)
                .getSingleProduct(lang, id, user_id, country_coude)
                .enqueue(new Callback<ProductDataModel>() {
                    @Override
                    public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                if (response.body().getData() != null) {
                                    updateData(response.body().getData());
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
                    public void onFailure(Call<ProductDataModel> call, Throwable t) {
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

    private void updateData(ProductDataModel.Data body) {


        singleProductModel = body.getProduct();
        binding.tvOldprice.setPaintFlags(binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.tvDetails.setText(Html.fromHtml(body.getProduct().getProduct_trans_fk().getDescription() + ""));

        oldPrice = singleProductModel.getProduct_default_price().getPrice();
        price = singleProductModel.getPrice();
        list.clear();
        binding.nested.setVisibility(View.VISIBLE);
        list.add(body.getParentAttributes());
        parentAdapter.notifyDataSetChanged();

        if (body.getAttributes() != null) {
            childList.clear();
            childList.addAll(body.getAttributes());

            childAdapter = new ProductChildParentAttrAdapter(this, childList);
            binding.recViewChildren.setLayoutManager(new LinearLayoutManager(this));
            binding.recViewChildren.setAdapter(childAdapter);

        }


        binding.setModel(body.getProduct());


        if (body.getProduct().getProduct_images() != null && body.getProduct().getProduct_images().size() > 0) {
            SliderProductAdapter sliderProductAdapter = new SliderProductAdapter(body.getProduct().getProduct_images(), this);
            binding.tab.setupWithViewPager(binding.pager);
            binding.pager.setAdapter(sliderProductAdapter);
            binding.flSlider.setVisibility(View.VISIBLE);
            binding.flNoSlider.setVisibility(View.GONE);
        } else {
            binding.flSlider.setVisibility(View.GONE);
            binding.flNoSlider.setVisibility(View.VISIBLE);
        }

        if (body.getComments() != null && body.getComments().size() > 0) {
            CommentAdapter rateAdapter = new CommentAdapter(body.getComments(), this);
            binding.recViewComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.recViewComments.setAdapter(rateAdapter);
            binding.tvNoData.setVisibility(View.GONE);
        } else {
            binding.tvNoData.setVisibility(View.VISIBLE);

        }


        if (body.getOthers() != null && body.getOthers().size() > 0) {
            Product3Adapter adapter = new Product3Adapter(body.getOthers(), this);
            binding.recViewProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.recViewProducts.setAdapter(adapter);
            binding.tvNoProductData.setVisibility(View.GONE);
        } else {
            binding.tvNoProductData.setVisibility(View.VISIBLE);

        }


    }


    public void getFeatures(int attribute_id, int parent_pos, int child_pos, int old_pos, String type) {
        if (type.equals("child")) {
            data = getAttributeList(parent_pos);

            if (old_pos != -1) {
                ProductDataModel.Attribute attributeOld1 = data.get(parent_pos);
                List<ProductDataModel.Attribute> attributeOld2 = attributeOld1.getAttributes();

                ProductDataModel.Attribute attributeOld = attributeOld2.get(old_pos);
                attributeOld.setIs_default("no");
                attributeOld2.set(old_pos, attributeOld);
                attributeOld1.setAttributes(attributeOld2);
                data.set(parent_pos, attributeOld1);
            }


            ProductDataModel.Attribute attribute1 = data.get(parent_pos);
            List<ProductDataModel.Attribute> attribute2 = attribute1.getAttributes();

            ProductDataModel.Attribute attribute = attribute2.get(child_pos);
            attribute.setIs_default("yes");
            attribute2.set(child_pos, attribute);
            attribute1.setAttributes(attribute2);
            data.set(parent_pos, attribute1);

        }
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .getFeature(lang, id, attribute_id + "")
                .enqueue(new Callback<AttrDataModel>() {
                    @Override
                    public void onResponse(Call<AttrDataModel> call, Response<AttrDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                if (response.body().getData().getAttributes().size() > 0) {
                                    if (data.size()>0){
                                        childList.clear();
                                        if (type.equals("child")) {
                                            childList.addAll(data);
                                        }

                                        checkDefaultValue(response.body().getData().getAttributes());

                                        childAdapter.notifyDataSetChanged();

                                    }

                                    //childList.addAll(response.body().getData().getAttributes());
                                } else {

                                    if (data.size()>0){
                                        childList.clear();
                                        if (type.equals("child")) {
                                            childList.addAll(data);
                                        }
                                        Log.e("vvvvvvvvvv", childList.size()+"_______");
                                        if (childList.size()>0){
                                            ProductDataModel.Attribute attribute = childList.get(childList.size() - 1);

                                            updatePrice(attribute);
                                        }

                                        childAdapter.notifyDataSetChanged();

                                    }


                                }


                            }
                        } else {
                            dialog.dismiss();

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
                    public void onFailure(Call<AttrDataModel> call, Throwable t) {
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

    private void checkDefaultValue(List<ProductDataModel.Attribute> attributes) {
        List<ProductDataModel.Attribute> childData = new ArrayList<>();

        for (int index = 0; index < attributes.size(); index++) {
            ProductDataModel.Attribute attribute = attributes.get(index);
            boolean hasDefault = false;
            for (ProductDataModel.Attribute attribute1 : attribute.getAttributes()) {
                if (attribute1.getIs_default().equals("yes")) {
                    hasDefault = true;
                }
            }

            if (!hasDefault) {
                List<ProductDataModel.Attribute> attributes1 = attribute.getAttributes();
                ProductDataModel.Attribute attribute1 = attributes1.get(0);
                attribute1.setIs_default("yes");
                attributes1.set(0, attribute1);
                attribute.setAttributes(attributes1);

            }
            childData.add(attribute);

        }
        childList.addAll(childData);
        ProductDataModel.Attribute attribute = childData.get(childData.size() - 1);

        updatePrice(attribute);

    }

    private void updatePrice(ProductDataModel.Attribute attribute) {

        oldPrice = 0.0;
        price = 0.0;
        for (ProductDataModel.Attribute attr : attribute.getAttributes()) {
            if (attr.getIs_default().equals("yes")) {
                oldPrice = attr.getPrice();
                price = oldPrice;
            }
        }

        if (singleProductModel.getHave_offer().equals("yes")) {
            if (singleProductModel.getOffer_type().equals("per")) {
                price = oldPrice - (oldPrice * (singleProductModel.getOffer_value() / 100.0));
            } else if (singleProductModel.getOffer_type().equals("value")) {
                price = oldPrice - singleProductModel.getOffer_value();
            } else {
                price = oldPrice;
            }
        }


        binding.tvOldprice.setText(oldPrice + "");
        binding.tvPrice.setText(price + "");
    }


    private List<ProductDataModel.Attribute> getAttributeList(int parent_level) {
        List<ProductDataModel.Attribute> data = new ArrayList<>();
        for (int index = 0; index < parent_level + 1; index++) {
            data.add(childList.get(index));
        }
        return data;
    }

    public void like_dislike(SingleProductModel productModel, int pos, int i) {
        if (userModel != null) {
            try {

                Api.getService(Tags.base_url)
                        .addFavoriteProduct("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", productModel.getId() + "")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                if (response.isSuccessful() && response.body().getStatus() == 200) {

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
        }
    }

    public void like_dislikeMain(SingleProductModel productModel) {
        if (userModel != null) {
            try {

                Api.getService(Tags.base_url)
                        .addFavoriteProduct("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", productModel.getId() + "")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                if (response.isSuccessful() && response.body().getStatus() == 200) {
                                    getData();
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
        }
    }

    public void showData(String id) {
        Intent intent = new Intent(this, ProductDetialsActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 100);
    }

    private void addItemToCart() {


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
            if (userModel != null) {
                addCartDataModel.setUser_id(userModel.getData().getId());
            }
            double totalPrice = price;
            Log.e("ttt", totalPrice + "__" + price);
            if (userModel != null) {
                addCartDataModel.setTotal_price(totalPrice);
                addCartProductItemModel.setAmount(1);
                addCartProductItemModel.setHave_offer(singleProductModel.getHave_offer());
                addCartProductItemModel.setOffer_bonus(singleProductModel.getOffer_bonus());
                addCartProductItemModel.setOffer_min(singleProductModel.getOffer_min());
                addCartProductItemModel.setOffer_type(singleProductModel.getOffer_type());
                addCartProductItemModel.setOld_price(singleProductModel.getProduct_default_price().getPrice());
                addCartProductItemModel.setPrice(totalPrice);
                addCartProductItemModel.setProduct_id(singleProductModel.getId() + "");
                addCartProductItemModel.setOffer_value(singleProductModel.getOffer_value());
                addCartProductItemModel.setProduct_price_id(singleProductModel.getProduct_default_price().getId() + "");
                addCartProductItemModel.setVendor_id(singleProductModel.getVendor_id() + "");
                addCartProductItemModel.setName(singleProductModel.getProduct_trans_fk().getTitle());
                addCartProductItemModel.setImage(singleProductModel.getMain_image());
                addCartProductItemModel.setRate(singleProductModel.getRate());
                addCartProductItemModel.setDesc(singleProductModel.getProduct_trans_fk().getDescription());
                addCartProductItemModelList.add(addCartProductItemModel);
                addCartDataModel.setCart_products(addCartProductItemModelList);
            }
            else {
           int     pos = -1;
                for (int i = 0; i < addCartProductItemModelList.size(); i++) {
                    if (addCartProductItemModelList.get(i).getProduct_id().equals(singleProductModel.getId() + "")) {
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
                    addCartDataModel.setTotal_price(totalPrice);
                    addCartProductItemModel.setAmount(1);
                    addCartProductItemModel.setHave_offer(singleProductModel.getHave_offer());
                    addCartProductItemModel.setOffer_bonus(singleProductModel.getOffer_bonus());
                    addCartProductItemModel.setOffer_min(singleProductModel.getOffer_min());
                    addCartProductItemModel.setOffer_type(singleProductModel.getOffer_type());
                    addCartProductItemModel.setOld_price(singleProductModel.getProduct_default_price().getPrice());
                    addCartProductItemModel.setPrice(totalPrice);
                    addCartProductItemModel.setProduct_id(singleProductModel.getId() + "");
                    addCartProductItemModel.setOffer_value(singleProductModel.getOffer_value());
                    addCartProductItemModel.setProduct_price_id(singleProductModel.getProduct_default_price().getId() + "");
                    addCartProductItemModel.setVendor_id(singleProductModel.getVendor_id() + "");
                    addCartProductItemModel.setName(singleProductModel.getProduct_trans_fk().getTitle());
                    addCartProductItemModel.setImage(singleProductModel.getMain_image());
                    addCartProductItemModel.setRate(singleProductModel.getRate());
                    addCartProductItemModel.setDesc(singleProductModel.getProduct_trans_fk().getDescription());
                    addCartProductItemModelList.add(addCartProductItemModel);
                    addCartDataModel.setCart_products(addCartProductItemModelList);
                }

            }
            if (userModel != null) {
                addToCart(addCartDataModel);
            } else {
                counter++;

                binding.tvCounter.setText(counter + "");

                preferences.create_update_cart(this, addCartDataModel);

            }



    }


    private void addToCart(AddCartDataModel addCartDataModel) {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        binding.imageIncrease.setClickable(false);
        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        binding.imageIncrease.setClickable(true);
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                counter++;

                                binding.tvCounter.setText(counter + "");
                                isDataChanged = true;
                                double total = price * counter;
                                Log.e("price", price + "__" + total);
                                binding.tvTotal.setText(total + "");
                            }
                        } else {
                            dialog.dismiss();
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
                            dialog.dismiss();
                            binding.imageIncrease.setClickable(true);
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

    public void additemtoCart2(SingleProductModel data, ProductRowBinding binding) {

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
            if (userModel != null) {
                addCartDataModel.setUser_id(userModel.getData().getId());
            }
            double totalprice = 0;
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
            int  pos = -1;
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
            addTocart2(addCartDataModel, binding);
        } else {

            // data.setLoading(false);
            data.setAmount(data.getAmount() + 1);
            binding.tvCounter.setText((Integer.parseInt(binding.tvCounter.getText().toString()) + 1) + "");

            //binding.setCartCount(addCartDataModel.getCart_products().size()+"");
            preferences.create_update_cart(this, addCartDataModel);

        }


    }

    private void addTocart2(AddCartDataModel addCartDataModel, ProductRowBinding binding) {

        binding.imgIncrease.setClickable(false);
        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        binding.imgIncrease.setClickable(true);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                binding.tvCounter.setText((Integer.parseInt(binding.tvCounter.getText().toString()) + 1) + "");

                                isDataChanged = true;

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
        if (requestCode == 100 && resultCode == RESULT_OK) {
            isDataChanged = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }
}