package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.addukkanapp.R;
import com.addukkanapp.databinding.MainCategoryRowBinding;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.uis.activity_home.fragments.FragmentHome;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MainCategoryDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    //private Fragment_Main fragment_main;
    private Fragment fragment;
    public CategoryAdapter(List<MainCategoryDataModel.Data> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
      //  this.fragment_main=fragment_main;
this.fragment=fragment;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        MainCategoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_category_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {
            if(fragment instanceof FragmentHome){
                FragmentHome fragmentHome=(FragmentHome)fragment;
                fragmentHome.showDepart(list.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public MainCategoryRowBinding binding;

        public MyHolder(@NonNull MainCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
