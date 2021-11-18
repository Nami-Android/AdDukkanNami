package com.addukkanapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.FavouriteProductRowBinding;
import com.addukkanapp.databinding.OfferProductRowBinding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.FavouriteProductDataModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_my_favorite.MyFavoriteActivity;

import java.util.List;

public class FavouriteProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FavouriteProductDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;
    private UserModel userModel;
    //private Fragment_Main fragment_main;
    public FavouriteProductAdapter(List<FavouriteProductDataModel.Data> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        preferences=Preferences.getInstance();

        //  this.fragment_main=fragment_main;
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


        FavouriteProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.favourite_product_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setCurrency(currecny);
        myHolder.binding.setModel(list.get(position).getProduct_data());
        myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ((MyHolder) holder).binding.checkbox.setChecked(true);
        myHolder.binding.checkbox.setOnClickListener(v -> {

            if (context instanceof MyFavoriteActivity) {

                MyFavoriteActivity fragment_main = (MyFavoriteActivity) context;

                fragment_main.like_dislike(list.get(myHolder.getLayoutPosition()).getProduct_data(), myHolder.getLayoutPosition(), 0);

            }




        });

//Log.e("eeee",list.get(position).getHave_offer());
         // Log.e("ssss",((list.get(position).getHave_offer().equals("yes")?(list.get(position).getOffer_type().equals("per")?(list.get(position).getProduct_default_price().getPrice()-((list.get(position).getProduct_default_price().getPrice()*list.get(position).getOffer_value())/100)):list.get(position).getProduct_default_price().getPrice()-list.get(position).getOffer_value()):list.get(position).getProduct_default_price().getPrice())+""));
        myHolder.itemView.setOnClickListener(view -> {
           // Log.e("sssss",list.get(holder.getLayoutPosition()).getId()+"");

           // fragment_main.setitemData(list.get(holder.getLayoutPosition()).getId()+"");
        });
        myHolder.binding.imgDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myHolder.binding.imgIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof  MyFavoriteActivity){
                    MyFavoriteActivity myFavoriteActivity=(MyFavoriteActivity)context;
                    myFavoriteActivity.additemtoCart(list.get(position), myHolder.getAdapterPosition(), 0);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public FavouriteProductRowBinding binding;

        public MyHolder(@NonNull FavouriteProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
