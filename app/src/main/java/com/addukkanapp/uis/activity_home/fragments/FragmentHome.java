package com.addukkanapp.uis.activity_home.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.adapters.CategoryAdapter;
import com.addukkanapp.adapters.MainCategoryAdapter;
import com.addukkanapp.adapters.Product2Adapter;
import com.addukkanapp.adapters.SliderAdapter;
import com.addukkanapp.databinding.FragmentHomeBinding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.models.ALLProductDataModel;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.AddCartProductItemModel;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.SliderDataModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.share.Common;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_ask_doctor.AskDoctorActivity;
import com.addukkanapp.uis.activity_home.HomeActivity;
import com.addukkanapp.uis.activity_product_detials.ProductDetialsActivity;
import com.addukkanapp.uis.activity_product_filter.ProductFilterActivity;
import com.addukkanapp.uis.activity_products.ProductsActivity;
import com.addukkanapp.uis.activity_qr_code.QrCodeActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {
    private FragmentHomeBinding binding;
    private HomeActivity activity;
    private SliderAdapter sliderAdapter;
    private Timer timer;
    private TimerTask timerTask;
    private Preferences preferences;
    private UserModel userModel;
    private List<MainCategoryDataModel.Data> categoryDataModelDataList, getCategoryDataModelDataList;
    private List<SingleProductModel> recentArriveList, mostSellerList;
    private Product2Adapter recentArriveAdapter, mostSellerAdapter;
    private CategoryAdapter categoryAdapter;
    private AppLocalSettings settings;
    private String lang = "";
    private MainCategoryAdapter mainCategoryAdapter;
    private String country_coude;
    private String currecny;
    private Handler handler;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2;
    private Uri uri = null;

    public static FragmentHome newInstance() {
        return new FragmentHome();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        categoryDataModelDataList = new ArrayList<>();
        getCategoryDataModelDataList = new ArrayList<>();
        recentArriveList = new ArrayList<>();
        mostSellerList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        settings = preferences.isLanguageSelected(activity);

        userModel = preferences.getUserData(activity);

        if (userModel != null) {
            country_coude = userModel.getData().getCountry_code();
            if (preferences.getCartData(activity) != null && preferences.getCartData(activity).getCart_products().size() > 0) {
                AddCartDataModel addCartDataModel = preferences.getCartData(activity);
                addCartDataModel.setUser_id(userModel.getData().getId());
                addTocart(addCartDataModel);
            }
        } else {
            country_coude = settings.getCountry_code();
        }
        binding.cardaskdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AskDoctorActivity.class);
                startActivity(intent);
            }
        });
        binding.cardroshta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.flroshata.setVisibility(View.VISIBLE);
            }
        });
        binding.flimage.setOnClickListener(v -> openSheet());
        binding.progBarCategory.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        binding.progBarrecent.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        binding.progBarmostsell.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        binding.tab.setupWithViewPager(binding.pager);

        categoryAdapter = new CategoryAdapter(categoryDataModelDataList, activity, this);
        mainCategoryAdapter = new MainCategoryAdapter(getCategoryDataModelDataList, activity, this);
        recentArriveAdapter = new Product2Adapter(recentArriveList, activity, this, 1);
        mostSellerAdapter = new Product2Adapter(mostSellerList, activity, this, 0);

        binding.recViewCategory.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        binding.recViewCategory.setAdapter(categoryAdapter);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recView.setAdapter(mainCategoryAdapter);
        binding.recViewrecent.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        binding.recViewrecent.setAdapter(recentArriveAdapter);
        binding.recViewmostsell.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        binding.recViewmostsell.setAdapter(mostSellerAdapter);
        get_slider();
        getMainCategory();
        getMainCategorySubCategoryProduct();
        getRecentArrived();
        getMostSell();
        binding.flGallery.setOnClickListener(view -> {
            closeSheet();
            checkReadPermission();
        });

        binding.flCamera.setOnClickListener(view -> {
            closeSheet();
            checkCameraPermission();
        });
        binding.btnCancel.setOnClickListener(view -> closeSheet());

        binding.btnSend.setOnClickListener(v -> {
            if (userModel != null) {
                if (uri != null) {
                    addRosheta();
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.ch_image), Toast.LENGTH_LONG).show();

                }
            } else {
                activity.navigateToSignInActivity();
            }
        });
        binding.imQr.setOnClickListener(v -> {
            binding.flroshata.setVisibility(View.GONE);
            if (userModel != null) {
                Intent intent = new Intent(activity, QrCodeActivity.class);
                startActivity(intent);
            } else {
                activity.navigateToSignInActivity();
            }
        });

        binding.flclose.setOnClickListener(v -> binding.flroshata.setVisibility(View.GONE));


        binding.tvMostSeller.setOnClickListener(v -> {
            navigateToProductActivity("mostSeller", 100);
        });

        binding.tvRecentArrive.setOnClickListener(v -> {
            navigateToProductActivity("recentArrive", 100);
        });
    }

    private void navigateToProductActivity(String type, int req) {
        Intent intent = new Intent(activity, ProductsActivity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, req);

    }


    public void getMainCategory() {
        Api.getService(Tags.base_url)
                .get_category(lang)
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBarCategory.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                categoryDataModelDataList.clear();
                                categoryDataModelDataList.addAll(response.body().getData());

                                if (categoryDataModelDataList.size() > 0) {
                                    categoryAdapter.notifyDataSetChanged();
//                                binding.tvNoDatadepart.setVisibility(View.GONE);
                                    // Log.e(",dkdfkfkkfk", categoryDataModelDataList.get(0).getTitle());
                                } else {
//                                binding.tvNoDatadepart.setVisibility(View.VISIBLE);

                                }

                            }
                        } else {
                            binding.progBarCategory.setVisibility(View.GONE);

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
                            binding.progBarCategory.setVisibility(View.GONE);

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

    public void getMainCategorySubCategoryProduct() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }
        Api.getService(Tags.base_url)
                .get_categorySubProduct(lang, user_id, country_coude)
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                getCategoryDataModelDataList.clear();
                                getCategoryDataModelDataList.addAll(response.body().getData());

                                if (getCategoryDataModelDataList.size() > 0) {
                                    mainCategoryAdapter.notifyDataSetChanged();

//                                binding.tvNoDatadepart.setVisibility(View.GONE);
                                    //Log.e(",dkdfkfkkfk", categoryDataModelDataList.get(0).getTitle());
                                } else {
//                                binding.tvNoDatadepart.setVisibility(View.VISIBLE);

                                }

                            }
                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode2", response.code() + "__" + response.errorBody().string());
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
                                    //   Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
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

    public void getRecentArrived() {

        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }
        //   Log.e("sllsks", user_id + lang + country_coude);
        Api.getService(Tags.base_url)
                .getRecentlyArrived(lang, user_id, country_coude, "off")
                .enqueue(new Callback<ALLProductDataModel>() {
                    @Override
                    public void onResponse(Call<ALLProductDataModel> call, Response<ALLProductDataModel> response) {
                        binding.progBarrecent.setVisibility(View.GONE);
                        Log.e("Slslls", response.message());
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                recentArriveList.clear();
                                recentArriveList.addAll(response.body().getData());
                                recentArriveAdapter.notifyDataSetChanged();
                                Log.e("Ddd", "ttt");


                            }
                        } else {
                            binding.progBarrecent.setVisibility(View.GONE);

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
                            binding.progBarrecent.setVisibility(View.GONE);

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
        String user_id = null;
        if (userModel != null) {
            user_id = userModel.getData().getId() + "";
        }
        Api.getService(Tags.base_url)
                .getMostSell(lang, user_id, country_coude, "off")
                .enqueue(new Callback<ALLProductDataModel>() {
                    @Override
                    public void onResponse(Call<ALLProductDataModel> call, Response<ALLProductDataModel> response) {
                        binding.progBarmostsell.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {

                                mostSellerList.clear();
                                mostSellerList.addAll(response.body().getData());

                                if (mostSellerList.size() > 0) {
                                    mostSellerAdapter.notifyDataSetChanged();

//                                binding.tvNoDatadepart.setVisibility(View.GONE);
                                    //Log.e(",dkdfkfkkfk", categoryDataModelDataList.get(0).getTitle());
                                } else {
//                                binding.tvNoDatadepart.setVisibility(View.VISIBLE);

                                }

                            }
                        } else {
                            binding.progBarmostsell.setVisibility(View.GONE);

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
                            binding.progBarmostsell.setVisibility(View.GONE);

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

    private void get_slider() {

        binding.progBarSlider.setVisibility(View.VISIBLE);
        binding.pager.setVisibility(View.GONE);
        Api.getService(Tags.base_url).get_slider(lang, "public", country_coude).enqueue(new Callback<SliderDataModel>() {
            @Override
            public void onResponse(Call<SliderDataModel> call, Response<SliderDataModel> response) {
                binding.progBarSlider.setVisibility(View.GONE);
                binding.pager.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    if (response.body() != null && response.body().getStatus() == 200 && response.body().getData() != null) {
                        // binding.flslider.setVisibility(View.VISIBLE);
                        sliderAdapter = new SliderAdapter(response.body().getData(), activity);
                        binding.pager.setAdapter(sliderAdapter);
                        if (response.body().getData().size() > 1) {
                            Log.e("ldkdkdkjk", "lkjjdjjd");
                            timer = new Timer();
                            timerTask = new MyTask();
                            timer.scheduleAtFixedRate(timerTask, 6000, 6000);
                        } else {
                            //  binding.flslider.setVisibility(View.GONE);
                        }


                    } else {

                        binding.pager.setVisibility(View.GONE);
                    }
                } else if (response.code() == 404) {
                    binding.pager.setVisibility(View.GONE);
                } else {
                    binding.pager.setVisibility(View.GONE);
                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<SliderDataModel> call, Throwable t) {
                try {
                    binding.progBarSlider.setVisibility(View.GONE);
                    binding.pager.setVisibility(View.GONE);

                    Log.e("Error", t.getMessage());

                } catch (Exception e) {
                    Log.e("Error", e.toString());

                }

            }
        });

    }

    public void showDepart(int id) {
        activity.displayFragmentDukkan(id);
    }

    public void displayFragmentDepartment(int id) {
        activity.displayFragmentDukkan(id);

    }

    public void showData(String s) {
        Intent intent = new Intent(activity, ProductDetialsActivity.class);
        intent.putExtra("id", s);
        startActivityForResult(intent, 100);
    }

    public void like_dislike(SingleProductModel productModel, int pos, int i) {
        if (userModel != null) {
            try {

                Api.getService(Tags.base_url)
                        .addFavoriteProduct("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", productModel.getId() + "")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                //  Log.e("dlldl",response.body().getStatus()+"");
                                if (response.isSuccessful() && response.body().getStatus() == 200) {
                                    update(i);

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
            activity.navigateToSignInActivity();
            // Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));

        }
    }

    public class MyTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(() -> {
                int current_page = binding.pager.getCurrentItem();
                if (current_page < sliderAdapter.getCount() - 1) {
                    binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
                } else {
                    binding.pager.setCurrentItem(0);

                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }

    }


    private void update(int i) {

        Log.e("fff", "fff");
        getMainCategorySubCategoryProduct();
        getRecentArrived();
        getMostSell();


    }

    public void filter(int layoutPosition, MainCategoryDataModel.Data sub_departments) {
        Intent intent = new Intent(activity, ProductFilterActivity.class);
        intent.putExtra("pos", layoutPosition);
        intent.putExtra("data", sub_departments);
        startActivity(intent);
    }

    public void openSheet() {
        binding.expandLayout.setExpanded(true, true);
    }

    public void closeSheet() {
        binding.expandLayout.collapse(true);

    }

    public void checkReadPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, READ_REQ);
        } else {
            SelectImage(READ_REQ);
        }
    }

    public void checkCameraPermission() {

        closeSheet();

        if (ContextCompat.checkSelfPermission(activity, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage(CAMERA_REQ);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{camera_permission, write_permission}, CAMERA_REQ);
        }
    }

    private void SelectImage(int req) {

        Intent intent = new Intent();

        if (req == READ_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, req);

        } else if (req == CAMERA_REQ) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, req);
            } catch (SecurityException e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                SelectImage(requestCode);
            } else {
                Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQ && resultCode == Activity.RESULT_OK && data != null) {

            uri = data.getData();
            File file = new File(Common.getImagePath(activity, uri));
            Picasso.get().load(file).fit().into(binding.image);
            binding.icon.setVisibility(View.GONE);

        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                String path = Common.getImagePath(activity, uri);
                binding.icon.setVisibility(View.GONE);

                if (path != null) {
                    Picasso.get().load(new File(path)).fit().into(binding.image);

                } else {
                    Picasso.get().load(uri).fit().into(binding.image);

                }
            }


        } else if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            getMostSell();
            getRecentArrived();
            getMainCategorySubCategoryProduct();

        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "", ""));
    }

    private void addRosheta() {

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody user_part = Common.getRequestBodyText(userModel.getData().getId() + "");

        MultipartBody.Part image = Common.getMultiPartImage(activity, uri, "image");


        Api.getService(Tags.base_url)
                .addRosheta("Bearer " + userModel.getData().getToken(), user_part, image)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                binding.flroshata.setVisibility(View.GONE);
                                Toast.makeText(activity, activity.getResources().getString(R.string.suc_and), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                // Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    // Toast.makeText(SignUpActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(SignUpActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
            addCartDataModel = preferences.getCartData(activity);
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
        } else {
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
            //   data.setLoading(false);
            data.setAmount(data.getAmount() + 1);
            activity.binding.setCartCount(addCartDataModel.getCart_products().size() + "");
            preferences.create_update_cart(activity, addCartDataModel);
            if (type == 0) {
                mostSellerList.set(adapterPosition, data);
                mostSellerAdapter.notifyItemChanged(adapterPosition);
            } else {
                recentArriveList.set(adapterPosition, data);
                recentArriveAdapter.notifyItemChanged(adapterPosition);
            }
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
                                if (type == 0) {
                                    mostSellerList.set(adapterPosition, data);
                                    mostSellerAdapter.notifyItemChanged(adapterPosition);
                                } else {
                                    recentArriveList.set(adapterPosition, data);
                                    recentArriveAdapter.notifyItemChanged(adapterPosition);
                                }
                                activity.binding.setCartCount(response.body().getData().getDetails().size() + "");

                            }
                        } else {
                            if (type == 0) {
                                mostSellerList.set(adapterPosition, data);
                                mostSellerAdapter.notifyItemChanged(adapterPosition);
                            } else {
                                recentArriveList.set(adapterPosition, data);
                                recentArriveAdapter.notifyItemChanged(adapterPosition);
                            }
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
                            if (type == 0) {
                                mostSellerList.set(adapterPosition, data);
                                mostSellerAdapter.notifyItemChanged(adapterPosition);
                            } else {
                                recentArriveList.set(adapterPosition, data);
                                recentArriveAdapter.notifyItemChanged(adapterPosition);
                            }


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

    private void addTocart(AddCartDataModel addCartDataModel) {

        //   Log.e("sllsks", user_id + lang + country_coude);
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.upload_cart));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        //   data.setLoading(false);
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
//                                data.setAmount(data.getAmount() + 1);
//                                if (type == 0) {
//                                    mostSellerList.set(adapterPosition, data);
//                                    mostSellerAdapter.notifyItemChanged(adapterPosition);
//                                } else {
//                                    recentArriveList.set(adapterPosition, data);
//                                    recentArriveAdapter.notifyItemChanged(adapterPosition);
//                                }
                                preferences.clearCart(activity);
                                activity.binding.setCartCount(response.body().getData().getDetails().size() + "");

                            }
                        } else {
//                            if (type == 0) {
//                                mostSellerList.set(adapterPosition, data);
//                                mostSellerAdapter.notifyItemChanged(adapterPosition);
//                            } else {
//                                recentArriveList.set(adapterPosition, data);
//                                recentArriveAdapter.notifyItemChanged(adapterPosition);
//                            }
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
                            //                            if (type == 0) {

//                            if (type == 0) {
//                                mostSellerList.set(adapterPosition, data);
//                                mostSellerAdapter.notifyItemChanged(adapterPosition);
//                            } else {
//                                recentArriveList.set(adapterPosition, data);
//                                recentArriveAdapter.notifyItemChanged(adapterPosition);
//                            }


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

    public void additemtoCart2(SingleProductModel data, int child_pos, int parent_pos) {


        AddCartDataModel addCartDataModel;

        if (userModel != null) {
            addCartDataModel = new AddCartDataModel();
        } else {
            addCartDataModel = preferences.getCartData(activity);
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
        } else {
            int pos = -1;
            for (int i = 0; i < addCartProductItemModelList.size(); i++) {
                if (addCartProductItemModelList.get(i).getProduct_id().equals(data.getId() + "")) {
                    addCartProductItemModel = addCartProductItemModelList.get(i);
                    pos = i;
                    break;
                }
            }
            // Log.e("psosoo",pos+"");
            if (pos > -1) {
                addCartProductItemModel.setAmount(addCartProductItemModel.getAmount() + 1);
                addCartProductItemModelList.set(pos, addCartProductItemModel);
                addCartDataModel.setCart_products(addCartProductItemModelList);
                //Log.e("dlld",addCartProductItemModelList.size()+"");
            } else {
                Log.e("psosoo", pos + "");

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
            addTocart2(addCartDataModel, data, child_pos, parent_pos);
        } else {

            //  data.setLoading(false);
            data.setAmount(data.getAmount() + 1);
            preferences.create_update_cart(activity, addCartDataModel);
            MainCategoryDataModel.Data data1 = getCategoryDataModelDataList.get(parent_pos);
            List<MainCategoryDataModel.ProductData> product_list = data1.getProduct_list();
            MainCategoryDataModel.ProductData productData = product_list.get(child_pos);
            productData.setProduct_data(data);
            product_list.set(child_pos, productData);
            data1.setProduct_list(product_list);
            activity.binding.setCartCount(addCartDataModel.getCart_products().size() + "");
            mainCategoryAdapter.notifyItemChanged(parent_pos);
        }


    }

    private void addTocart2(AddCartDataModel addCartDataModel, SingleProductModel model, int child_pos, int parent_pos) {

        Api.getService(Tags.base_url)
                .createCart("Bearer " + userModel.getData().getToken(), addCartDataModel)
                .enqueue(new Callback<CartDataModel>() {
                    @Override
                    public void onResponse(Call<CartDataModel> call, Response<CartDataModel> response) {
                        model.setLoading(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                model.setAmount(model.getAmount() + 1);
                                MainCategoryDataModel.Data data = getCategoryDataModelDataList.get(parent_pos);
                                List<MainCategoryDataModel.ProductData> product_list = data.getProduct_list();
                                MainCategoryDataModel.ProductData productData = product_list.get(child_pos);
                                productData.setProduct_data(model);
                                product_list.set(child_pos, productData);
                                data.setProduct_list(product_list);
                                mainCategoryAdapter.notifyItemChanged(parent_pos);


                                activity.binding.setCartCount(response.body().getData().getDetails().size() + "");


                            }
                        } else {
                            MainCategoryDataModel.Data data = getCategoryDataModelDataList.get(parent_pos);
                            List<MainCategoryDataModel.ProductData> product_list = data.getProduct_list();
                            MainCategoryDataModel.ProductData productData = product_list.get(child_pos);
                            productData.setProduct_data(model);
                            product_list.set(child_pos, productData);
                            data.setProduct_list(product_list);
                            mainCategoryAdapter.notifyItemChanged(parent_pos);
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
                            MainCategoryDataModel.Data data = getCategoryDataModelDataList.get(parent_pos);
                            List<MainCategoryDataModel.ProductData> product_list = data.getProduct_list();
                            MainCategoryDataModel.ProductData productData = product_list.get(child_pos);
                            productData.setProduct_data(model);
                            product_list.set(child_pos, productData);
                            data.setProduct_list(product_list);
                            mainCategoryAdapter.notifyItemChanged(parent_pos);
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
