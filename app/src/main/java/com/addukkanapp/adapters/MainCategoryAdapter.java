package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.MainCategorySubCategoryProductRowBinding;
import com.addukkanapp.databinding.SubCategoryrowBinding;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.uis.activity_home.fragments.FragmentHome;

import java.util.List;

import io.paperdb.Paper;

public class MainCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String lang;
    private List<MainCategoryDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    //private Fragment_Main fragment_main;
    public MainCategoryAdapter(List<MainCategoryDataModel.Data> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        Paper.init(context);
        lang = Paper.book().read("lang", "ar");
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        //  this.fragment_main=fragment_main;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        MainCategorySubCategoryProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_category_sub_category_product_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.setLang(lang);
        SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(list.get(position).getSub_departments(), context, fragment,list.get(position));
        myHolder.binding.recViewSubCategory.setLayoutManager(new GridLayoutManager(context, 4));
        myHolder.binding.recViewSubCategory.setAdapter(subCategoryAdapter);
        ProductAdapter productAdapter = new ProductAdapter(list.get(position).getProduct_list(), context, fragment,position);
        myHolder.binding.recView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        myHolder.binding.recView.setAdapter(productAdapter);
        myHolder.binding.imbanner.setOnClickListener(view -> {
        if(fragment instanceof FragmentHome){
            FragmentHome fragmentHome=(FragmentHome)fragment;
            fragmentHome.showDepart(list.get(position).getId());
        }
        });

        myHolder.binding.tvShowAll.setOnClickListener(v -> {
            if(fragment instanceof FragmentHome){
                FragmentHome fragmentHome=(FragmentHome)fragment;
                fragmentHome.displayFragmentDepartment(list.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public MainCategorySubCategoryProductRowBinding binding;

        public MyHolder(@NonNull MainCategorySubCategoryProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
