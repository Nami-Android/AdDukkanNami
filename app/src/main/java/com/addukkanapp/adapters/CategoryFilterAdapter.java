package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.CategoryRow3Binding;
import com.addukkanapp.databinding.SubCategoryRow3Binding;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.uis.activity_filter_search.FilterSearchActivity;

import java.util.List;

public class CategoryFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MainCategoryDataModel.Data> list;
    private LayoutInflater inflater;
    private Context context;
    private AppCompatActivity activity;
    private int pos;

    public CategoryFilterAdapter(Context context, List<MainCategoryDataModel.Data> list, int pos) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        activity = (AppCompatActivity) context;
        this.pos = pos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CategoryRow3Binding binding = DataBindingUtil.inflate(inflater, R.layout.category_row3, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        if (position == pos) {
            myHolder.binding.rb.setChecked(true);
        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof FilterSearchActivity){
                    FilterSearchActivity activity=(FilterSearchActivity)context;
                    activity.addDepartid(list.get(holder.getLayoutPosition()));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private CategoryRow3Binding binding;

        public MyHolder(CategoryRow3Binding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

}
