package com.addukkanapp.uis.activity_home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.addukkanapp.R;
import com.addukkanapp.adapters.SideMenuCategoryAdapter;
import com.addukkanapp.databinding.ActivityHomeBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_cart.CartActivity;
import com.addukkanapp.uis.activity_home.fragments.FragmenDukkan;
import com.addukkanapp.uis.activity_home.fragments.FragmentHome;
import com.addukkanapp.uis.activity_home.fragments.FragmentOffer;
import com.addukkanapp.uis.activity_home.fragments.FragmentProfile;
import com.addukkanapp.uis.activity_login.LoginActivity;
import com.addukkanapp.uis.activity_product_filter.ProductFilterActivity;
import com.addukkanapp.uis.activity_search.SearchActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    public ActivityHomeBinding binding;
    private String lang = "";
    private Preferences preferences;
    private UserModel userModel;
    private FragmentManager fragmentManager;
    private FragmentHome fragmentHome;
    private FragmenDukkan fragmenDukkan;
    private FragmentOffer fragmentOffer;
    private FragmentProfile fragmentProfile;
    private ActionBarDrawerToggle toggle;
    private List<MainCategoryDataModel.Data> categoryDataModelDataList;
    private SideMenuCategoryAdapter sideMenuSubCategoryAdapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        categoryDataModelDataList = new ArrayList<>();
        sideMenuSubCategoryAdapter = new SideMenuCategoryAdapter(categoryDataModelDataList, this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(sideMenuSubCategoryAdapter);
        fragmentManager = getSupportFragmentManager();
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolBar, R.string.open, R.string.close);
        toggle.syncState();
        binding.toolBar.setNavigationIcon(R.drawable.ic_toolbar_nav_icon);
        updateCartCount(0);

        displayFragmentHome();
        binding.llHome.setOnClickListener(v -> displayFragmentHome());
        binding.llDukkan.setOnClickListener(v -> displayFragmentDukkan(0));
        binding.llOffer.setOnClickListener(v -> displayFragmentOffers());
        binding.llProfile.setOnClickListener(v -> displayFragmentProfile());

        if (userModel != null) {
            updateTokenFireBase();

        }
        getSideMenu();
        binding.flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        binding.flCart.setOnClickListener(v -> {
            //if (userModel != null) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivityForResult(intent,200);
//            } else {
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivityForResult(intent,100);
//            }
        });
    }

    private void updateCartCount(int count) {
        binding.setCartCount(String.valueOf(count));
    }


    public void displayFragmentHome() {
        if (fragmentHome == null) {
            fragmentHome = FragmentHome.newInstance();

        }


        if (fragmenDukkan != null && fragmenDukkan.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmenDukkan).commit();
        }

        if (fragmentOffer != null && fragmentOffer.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentOffer).commit();
        }
        if (fragmentProfile != null && fragmentProfile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentProfile).commit();
        }

        if (fragmentHome.isAdded()) {
            fragmentManager.beginTransaction().show(fragmentHome).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragmentHome, "fragmentHome").commit();
        }

        updateNavUiHome();
    }

    public void displayFragmentDukkan(int id) {
        if (fragmenDukkan == null) {
            fragmenDukkan = FragmenDukkan.newInstance();

        }

        fragmenDukkan.setDepartid(id);
        if (fragmentHome != null && fragmentHome.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentHome).commit();
        }

        if (fragmentOffer != null && fragmentOffer.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentOffer).commit();
        }
        if (fragmentProfile != null && fragmentProfile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentProfile).commit();
        }

        if (fragmenDukkan.isAdded()) {
            fragmentManager.beginTransaction().show(fragmenDukkan).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragmenDukkan, "fragmenDukkan").commit();
        }

        updateNavUiDukkan();
    }

    private void displayFragmentOffers() {

        if (fragmentOffer == null) {
            fragmentOffer = FragmentOffer.newInstance();

        }


        if (fragmentHome != null && fragmentHome.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentHome).commit();
        }

        if (fragmenDukkan != null && fragmenDukkan.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmenDukkan).commit();
        }
        if (fragmentProfile != null && fragmentProfile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentProfile).commit();
        }

        if (fragmentOffer.isAdded()) {
            fragmentManager.beginTransaction().show(fragmentOffer).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragmentOffer, "fragmentOffer").commit();
        }

        updateNavUiOffer();
    }

    private void displayFragmentProfile() {
        if (fragmentProfile == null) {
            fragmentProfile = FragmentProfile.newInstance();

        }


        if (fragmentHome != null && fragmentHome.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentHome).commit();
        }

        if (fragmenDukkan != null && fragmenDukkan.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmenDukkan).commit();
        }
        if (fragmentOffer != null && fragmentOffer.isAdded()) {
            fragmentManager.beginTransaction().hide(fragmentOffer).commit();
        }

        if (fragmentProfile.isAdded()) {
            fragmentManager.beginTransaction().show(fragmentProfile).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragmentProfile, "fragmentProfile").commit();
        }
        updateNavUiProfile();
    }

    public void updateFragmentHome(){
        if (fragmentHome!=null&&fragmentHome.isAdded()){
            fragmentHome.getMostSell();
            fragmentHome.getRecentArrived();
            fragmentHome.getMainCategorySubCategoryProduct();

        }
    }
    private void updateNavUiHome() {
        binding.iconHome.setSaturation(1.0f);
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.iconStore.setSaturation(0.0f);
        binding.tvStore.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconOffer.setSaturation(0.0f);
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconProfile.setSaturation(0.0f);
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray9));

    }



    private void updateNavUiDukkan() {
        binding.iconStore.setSaturation(1.0f);
        binding.tvStore.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.iconHome.setSaturation(0.0f);
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconOffer.setSaturation(0.0f);
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconProfile.setSaturation(0.0f);
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray9));

    }


    private void updateNavUiOffer() {
        binding.iconOffer.setSaturation(1.0f);
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.iconHome.setSaturation(0.0f);
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconStore.setSaturation(0.0f);
        binding.tvStore.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconProfile.setSaturation(0.0f);
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.gray9));

    }

    private void updateNavUiProfile() {
        binding.iconProfile.setSaturation(1.0f);
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.iconHome.setSaturation(0.0f);
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconStore.setSaturation(0.0f);
        binding.tvStore.setTextColor(ContextCompat.getColor(this, R.color.gray9));

        binding.iconOffer.setSaturation(0.0f);
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.gray9));

    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 500);


    }

    private void updateTokenFireBase() {
        if (userModel != null) {
            try {
                FirebaseInstanceId.getInstance()
                        .getInstanceId()
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()){
                                String token = task.getResult().getToken();
                                Api.getService(Tags.base_url)
                                        .updateFirebaseToken("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), token, "android")
                                        .enqueue(new Callback<ResponseModel>() {
                                            @Override
                                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                                if (response.isSuccessful() && response.body() != null ) {
                                                    userModel.getData().setFirebase_token(token);
                                                    preferences.create_update_userdata(HomeActivity.this, userModel);
                                                    Log.e("data", "success");

                                                } else {
                                                    try {

                                                        Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                                try {

                                                    if (t.getMessage() != null) {
                                                        Log.e("errorToken2", t.getMessage());
                                                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                            //Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                } catch (Exception e) {
                                                }
                                            }
                                        });

                            }
                        });

            } catch (Exception e) {


            }
        }
    }

    public void logout() {
        if (userModel == null) {
            return;
        }

        Log.e("ddd", userModel.getData().getFirebase_token());
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .logout("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), userModel.getData().getFirebase_token(), "android")
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    userModel = null;
                                    preferences.clear(HomeActivity.this);
                                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }


                        } else {

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
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
                                Log.e("errorToken2", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });


    }


    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    private void getSideMenu() {

        Api.getService(Tags.base_url)
                .getSideMenu(lang)
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                categoryDataModelDataList.clear();
                                categoryDataModelDataList.addAll(response.body().getData());

                                if (categoryDataModelDataList.size() > 0) {
                                    sideMenuSubCategoryAdapter.notifyDataSetChanged();

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
                    public void onFailure(Call<MainCategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    // Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
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
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode==100&&resultCode==RESULT_OK){
            userModel = preferences.getUserData(this);
            if (fragmentProfile!=null&&fragmentProfile.isAdded()){
                fragmentProfile.updateUserData();
            }


        }else if (requestCode==200&&resultCode==RESULT_OK){
            updateFragmentHome();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onBackPressed() {
        if (fragmentHome != null && fragmentHome.isAdded() && fragmentHome.isVisible()) {

            finish();
        } else {
            displayFragmentHome();
        }
    }

    public void filter(int layoutPosition, MainCategoryDataModel.Data sub_departments) {
        Intent intent = new Intent(HomeActivity.this, ProductFilterActivity.class);
        intent.putExtra("pos", layoutPosition);
        intent.putExtra("data", sub_departments);
        startActivity(intent);
    }

    public void getData() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }


        Api.getService(Tags.base_url)
                .getMyCart("Bearer " + userModel.getData().getToken(), lang, user_id)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        //binding.progBar.setVisibility(View.GONE);
                        //     Log.e("Dldldl",response.message());
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                if (response.body().getData() != null && response.body().getData().getDetails() != null) {
                                    updateCartCount(response.body().getData().getDetails().size());
                                }else {
                                    updateCartCount(0);

                                }


                            }
                        } else {
                            //binding.progBar.setVisibility(View.GONE);

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

    @Override
    protected void onResume() {
        super.onResume();
        if (userModel != null) {
            getData();
        }
        else {
            if(preferences.getCartData(this)!=null){
                binding.setCartCount(preferences.getCartData(this).getCart_products().size()+"");
            }
        }
    }
}