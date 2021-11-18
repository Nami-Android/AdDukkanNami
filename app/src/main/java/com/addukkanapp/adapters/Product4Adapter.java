package com.addukkanapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.ProductRow2Binding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_products.ProductsActivity;

import java.util.List;

public class Product4Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ProductsActivity activity;
    private UserModel userModel;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;
    public Product4Adapter(List<SingleProductModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (ProductsActivity) context;
        preferences=Preferences.getInstance();

        settings = preferences.isLanguageSelected(context);

        userModel = preferences.getUserData(context);
        if (userModel != null) {
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            currecny=settings.getCurrency();
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ProductRow2Binding binding = DataBindingUtil.inflate(inflater, R.layout.product_row2, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setCurrency(currecny);

        myHolder.binding.setModel(list.get(position));
        myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (list.get(position).getFavourite() != null) {
            ((MyHolder) holder).binding.checkbox.setChecked(true);
        } else {

            myHolder.binding.checkbox.setChecked(false);

        }
        myHolder.binding.checkbox.setOnClickListener(v -> {
            userModel = preferences.getUserData(context);
            boolean checked = myHolder.binding.checkbox.isChecked();
            if (userModel==null){
                myHolder.binding.checkbox.setChecked(!checked);
            }else {

                activity.like_dislike(list.get(myHolder.getAdapterPosition()), myHolder.getLayoutPosition());

            }

        });

        myHolder.itemView.setOnClickListener(view -> {
            activity.showData(list.get(myHolder.getAdapterPosition()).getId() + "");

        });
        myHolder.binding.imgIncrease.setOnClickListener(v -> activity.addToCart(list.get(holder.getAdapterPosition()), myHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ProductRow2Binding binding;

        public MyHolder(@NonNull ProductRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
