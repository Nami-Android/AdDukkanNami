package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.MainCategorySubCategoryProductRowBinding;
import com.addukkanapp.databinding.SideMenuCategoryRowBinding;
import com.addukkanapp.models.MainCategoryDataModel;

import java.util.List;

import io.paperdb.Paper;

public class SideMenuCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String lang;
    private List<MainCategoryDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private int currentPos = -1;

    public SideMenuCategoryAdapter(List<MainCategoryDataModel.Data> list, Context context) {
        this.list = list;
        this.context = context;
        Paper.init(context);
        lang = Paper.book().read("lang", "ar");
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SideMenuCategoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.side_menu_category_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.setLang(lang);
        SideMenuSubCategoryAdapter subCategoryAdapter = new SideMenuSubCategoryAdapter(list.get(position).getSub_departments(), context, list.get(position));
        myHolder.binding.recViewSubCategory.setLayoutManager(new GridLayoutManager(context, 1));
        myHolder.binding.recViewSubCategory.setAdapter(subCategoryAdapter);

        if (currentPos == position) {
            myHolder.binding.expandLayout.expand(true);
        } else {
            myHolder.binding.expandLayout.collapse(true);
        }

        myHolder.itemView.setOnClickListener(v -> {
            currentPos = myHolder.getAdapterPosition();
            if (myHolder.binding.expandLayout.isExpanded()) {
                myHolder.binding.expandLayout.collapse(true);

            } else {
                myHolder.binding.expandLayout.expand(true);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public SideMenuCategoryRowBinding binding;

        public MyHolder(@NonNull SideMenuCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
