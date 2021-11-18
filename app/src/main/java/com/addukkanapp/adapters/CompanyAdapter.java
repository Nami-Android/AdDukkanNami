package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.addukkanapp.R;
import com.addukkanapp.databinding.CompanyRowBinding;
import com.addukkanapp.models.CompanyModel;
import com.addukkanapp.uis.activity_filter.FilterActivity;

import java.util.List;


public class CompanyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<CompanyModel> list;

    public CompanyAdapter(Context context, List<CompanyModel> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CompanyRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.company_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof FilterActivity){
                    FilterActivity activity=(FilterActivity)context;
                    activity.addCompanyid(list.get(holder.getLayoutPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private CompanyRowBinding binding;

        public MyHolder(CompanyRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
