package com.addukkanapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.CartProductRowBinding;
import com.addukkanapp.databinding.FavouriteProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_cart.CartActivity;

import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CartDataModel.Data.Detials> list;
    private Context context;
    private LayoutInflater inflater;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;
    private UserModel userModel;

    //private Fragment_Main fragment_main;
    public CartProductAdapter(List<CartDataModel.Data.Detials> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
      //  this.fragment_main=fragment_main;
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


        CartProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.cart_product_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setCurrency(currecny);

        myHolder.binding.setModel(list.get(position));
        myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        myHolder.binding.imgIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof  CartActivity){
                    CartActivity cartActivity=(CartActivity) context;

                    cartActivity.additemtoCart(list.get(holder.getLayoutPosition()),holder.getAdapterPosition(),"increment");
                }
            }
        });
        myHolder.binding.imgDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof  CartActivity){
                    CartActivity cartActivity=(CartActivity) context;
                    CartDataModel.Data.Detials detials = list.get(holder.getLayoutPosition());
                    if (detials.getAmount()>1){
                        cartActivity.additemtoCart(list.get(holder.getAdapterPosition()),holder.getAdapterPosition(),"decrement");

                    }
                }
            }
        });
        myHolder.binding.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof  CartActivity){
                    CartActivity cartActivity=(CartActivity) context;
                    cartActivity.deleteItemFromcart(list.get(holder.getLayoutPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public CartProductRowBinding binding;

        public MyHolder(@NonNull CartProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
