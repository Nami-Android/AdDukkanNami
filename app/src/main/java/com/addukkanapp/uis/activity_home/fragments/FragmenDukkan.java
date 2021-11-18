package com.addukkanapp.uis.activity_home.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.DuckanCategoryAdapter;
import com.addukkanapp.adapters.SubCategoryAdapter;
import com.addukkanapp.databinding.FragmentDukkansBinding;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.SubCategoryDataModel;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_home.HomeActivity;
import com.addukkanapp.uis.activity_product_filter.ProductFilterActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmenDukkan extends Fragment {
    private FragmentDukkansBinding binding;
    private HomeActivity activity;
    private String lang = "";
    private List<MainCategoryDataModel.Data> categoryDataModelDataList;
    private DuckanCategoryAdapter duckanCategoryAdapter;
    private SubCategoryAdapter subCategoryAdapter;
    private List<SubCategoryDataModel> subCategoryDataModelList;
    private MainCategoryDataModel.Data sub_departments;
    private int id;

    public static FragmenDukkan newInstance() {
        return new FragmenDukkan();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dukkans, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        categoryDataModelDataList = new ArrayList<>();
        subCategoryDataModelList = new ArrayList<>();

        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        duckanCategoryAdapter = new DuckanCategoryAdapter(categoryDataModelDataList, activity, this,id);
        subCategoryAdapter = new SubCategoryAdapter(subCategoryDataModelList, activity,this, null);
        binding.recViewMainCategory.setLayoutManager(new LinearLayoutManager(activity));
        binding.recViewMainCategory.setAdapter(duckanCategoryAdapter);
        binding.recViewSubCategory.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.recViewSubCategory.setAdapter(subCategoryAdapter);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        getCategory();
    }

    private void getCategory() {


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
                                    duckanCategoryAdapter.notifyDataSetChanged();

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


    public void showSub(MainCategoryDataModel.Data sub_departments) {
        this.sub_departments = sub_departments;
        subCategoryDataModelList.clear();
        subCategoryDataModelList.addAll(sub_departments.getSub_departments());
        subCategoryAdapter.notifyDataSetChanged();
        if (subCategoryDataModelList.size() == 0) {
            binding.tvNoData.setVisibility(View.VISIBLE);
        }
    }

    public void filter(int layoutPosition) {
        Intent intent=new Intent(activity, ProductFilterActivity.class);
        intent.putExtra("pos",layoutPosition);
        intent.putExtra("data",sub_departments);
        startActivity(intent);
    }

    public void setDepartid(int id) {
        this.id=id;
        if(duckanCategoryAdapter!=null){
//            categoryDataModelDataList.clear();
//            duckanCategoryAdapter.notifyDataSetChanged();
            duckanCategoryAdapter.setid(id);
        getCategory();}
    }
}
