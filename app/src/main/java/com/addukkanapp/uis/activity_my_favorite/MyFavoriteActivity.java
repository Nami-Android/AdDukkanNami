package com.addukkanapp.uis.activity_my_favorite;

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
import com.addukkanapp.adapters.FavouriteProductAdapter;
import com.addukkanapp.databinding.ActivityMyFavoriteBinding;
import com.addukkanapp.databinding.FavouriteProductRowBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.AddCartProductItemModel;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.FavouriteProductDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_cart.CartActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoriteActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMyFavoriteBinding binding;
    private String lang;
    private boolean isLoading = false;
    private int current_page = 1;

    private LinearLayoutManager manager;
    private UserModel userModel;
    private Preferences preferences;
    private int selected_pos = -1;
    private boolean isFavoriteChange = false;
    private boolean isItemAdded = false;
    private List<FavouriteProductDataModel.Data> favouriteDataList;
    private FavouriteProductAdapter favouriteProduct_adapter;
  private String country_coude;
    private String currecny;    private AppLocalSettings settings;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_favorite);
        initView();
    }


    private void initView() {
        binding.setCartCount("0");
        favouriteDataList = new ArrayList<>();
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
        favouriteProduct_adapter = new FavouriteProductAdapter(favouriteDataList, this);
        binding.recView.setAdapter(favouriteProduct_adapter);
//        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) {
//                    int total = binding.recView.getAdapter().getItemCount();
//
//                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
//
//
//                    if (total > 6 && (total - lastVisibleItem) == 2 && !isLoading) {
//                        isLoading = true;
//                        int page = current_page + 1;
//                        favouriteDataList.add(null);
//                        adapter.notifyDataSetChanged();
//                        loadMore(page);
//                    }
//                }
//            }
//        });*/
        getData();
        binding.flCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userModel != null) {
                    Intent intent = new Intent(MyFavoriteActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    //  navigateToSignInActivity();
                }
            }
        });
    }


    public void getData() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }

        binding.progBar.setVisibility(View.VISIBLE);
        favouriteDataList.clear();
        favouriteProduct_adapter.notifyDataSetChanged();
        Log.e("sllsks", userModel.getData().getToken());
        Api.getService(Tags.base_url)
                .getFavoutite("Bearer " + userModel.getData().getToken(), lang, user_id, country_coude, "off")
                .enqueue(new Callback<FavouriteProductDataModel>() {
                    @Override
                    public void onResponse(Call<FavouriteProductDataModel> call, Response<FavouriteProductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        //     Log.e("Dldldl",response.message());
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                favouriteDataList.clear();
                                favouriteDataList.addAll(response.body().getData());


                                if (favouriteDataList.size() > 0) {
                                    favouriteProduct_adapter.notifyDataSetChanged();

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
                    public void onFailure(Call<FavouriteProductDataModel> call, Throwable t) {
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

    public int like_dislike(SingleProductModel productModel, int pos, int i) {
        // Log.e("lslsl",productModel.getMain_image());
        if (userModel != null) {
            try {
                // Log.e("llll", userModel.getUser().getToken());

                Api.getService(Tags.base_url)
                        .addFavoriteProduct("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", productModel.getId() + "")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                //  Log.e("dlldl",response.body().getStatus()+"");
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
            return 1;
        } else {
            //navigateToSignInActivity();
            // Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
            return 0;

        }
    }

    public void additemtoCart(FavouriteProductDataModel.Data data, int adapterPosition, int type) {
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
        addCartDataModel.setUser_id(userModel.getData().getId());
        double totalprice = 0;
        if (data.getProduct_data().getHave_offer().equals("yes")) {
            if (data.getProduct_data().getOffer_type().equals("value")) {
                totalprice = data.getProduct_data().getProduct_default_price().getPrice() - data.getProduct_data().getOffer_value();
            } else if (data.getProduct_data().getOffer_type().equals("per")) {
                totalprice = (data.getProduct_data().getProduct_default_price().getPrice()) - ((data.getProduct_data().getProduct_default_price().getPrice() * data.getProduct_data().getOffer_value()) / 100);
            } else {
                totalprice = data.getProduct_data().getProduct_default_price().getPrice();

            }
        } else {
            totalprice = data.getProduct_data().getProduct_default_price().getPrice();
        }
        addCartDataModel.setTotal_price(totalprice);
        addCartProductItemModel.setAmount(1);
        addCartProductItemModel.setHave_offer(data.getProduct_data().getHave_offer());
        addCartProductItemModel.setOffer_bonus(data.getProduct_data().getOffer_bonus());
        addCartProductItemModel.setOffer_min(data.getProduct_data().getOffer_min());
        addCartProductItemModel.setOffer_type(data.getProduct_data().getOffer_type());
        addCartProductItemModel.setOld_price(data.getProduct_data().getProduct_default_price().getPrice());
        addCartProductItemModel.setPrice(totalprice);
        addCartProductItemModel.setOffer_value(data.getProduct_data().getOffer_value());
        addCartProductItemModel.setProduct_id(data.getProduct_id() + "");
        addCartProductItemModel.setProduct_price_id(data.getProduct_data().getProduct_default_price().getId() + "");
        addCartProductItemModel.setVendor_id(data.getProduct_data().getVendor_id() + "");
        addCartProductItemModel.setName(data.getProduct_data().getProduct_trans_fk().getTitle());
        addCartProductItemModel.setImage(data.getProduct_data().getMain_image());
        addCartProductItemModel.setRate(data.getProduct_data().getRate());
        addCartProductItemModel.setDesc(data.getProduct_data().getProduct_trans_fk().getDescription());
        addCartProductItemModelList.add(addCartProductItemModel);
        addCartDataModel.setCart_products(addCartProductItemModelList);
        if (userModel != null) {
            addTocart(addCartDataModel, data, adapterPosition, type);
        } else {

            data.getProduct_data().setLoading(false);
            data.getProduct_data().setAmount(data.getProduct_data().getAmount() + 1);


            preferences.create_update_cart(this, addCartDataModel);
            favouriteDataList.set(adapterPosition, data);
            favouriteProduct_adapter.notifyItemChanged(adapterPosition);
        }
    }

    private void addTocart(AddCartDataModel addCartDataModel, FavouriteProductDataModel.Data data, int adapterPosition, int type) {

        //   Log.e("sllsks", user_id + lang + country_coude);
        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        data.getProduct_data().setLoading(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                data.getProduct_data().setAmount(data.getProduct_data().getAmount() + 1);

                                favouriteDataList.set(adapterPosition, data);
                                favouriteProduct_adapter.notifyItemChanged(adapterPosition);
                                binding.setCartCount(response.body().getData().getDetails().size() + "");
                                //binding.set(response.body().getData().getDetails().size() + "");

                            }
                        } else {
                            favouriteDataList.set(adapterPosition, data);
                            favouriteProduct_adapter.notifyItemChanged(adapterPosition);
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
                            favouriteDataList.set(adapterPosition, data);
                            favouriteProduct_adapter.notifyItemChanged(adapterPosition);

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
