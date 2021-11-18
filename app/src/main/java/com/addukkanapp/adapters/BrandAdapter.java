package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.BrandRowBinding;
import com.addukkanapp.databinding.CompanyRowBinding;
import com.addukkanapp.models.BrandDataModel;
import com.addukkanapp.uis.activity_filter.FilterActivity;

import java.util.List;


public class BrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<BrandDataModel.Data> list;

    public BrandAdapter(Context context, List<BrandDataModel.Data> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BrandRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.brand_row, parent, false);
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
                    activity.addBrandid(list.get(holder.getLayoutPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private BrandRowBinding binding;

        public MyHolder(BrandRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
