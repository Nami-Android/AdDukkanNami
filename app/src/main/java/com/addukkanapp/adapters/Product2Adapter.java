package com.addukkanapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_home.fragments.FragmentHome;

import java.util.List;

public class Product2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private int i;
    private UserModel userModel;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;

    //private Fragment_Main fragment_main;
    public Product2Adapter(List<SingleProductModel> list, Context context, Fragment fragment, int i) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        this.i = i;
        preferences=Preferences.getInstance();

        settings = preferences.isLanguageSelected(context);

        userModel = preferences.getUserData(context);
        if (userModel != null) {
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            currecny=settings.getCurrency();
        }

        //  this.fragment_main=fragment_main;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_row, parent, false);
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
            ((MyHolder) holder).binding.checkbox.setChecked(false);

        }
        myHolder.binding.checkbox.setOnClickListener(v -> {
            userModel = preferences.getUserData(context);
            boolean checked = myHolder.binding.checkbox.isChecked();

            if (fragment instanceof FragmentHome) {
                if (userModel==null){
                    myHolder.binding.checkbox.setChecked(!checked);
                }else {
                    FragmentHome fragment_main = (FragmentHome) fragment;

                    fragment_main.like_dislike(list.get(myHolder.getLayoutPosition()), myHolder.getLayoutPosition(), i);

                }

            }


        });
        myHolder.itemView.setOnClickListener(view -> {
            if (fragment instanceof FragmentHome) {

                FragmentHome fragment_main = (FragmentHome) fragment;


                fragment_main.showData(list.get(myHolder.getLayoutPosition()).getProduct_trans_fk().getProduct_id() + "");

            }
        });

        myHolder.binding.imgIncrease.setOnClickListener(v -> {
            SingleProductModel model = list.get(myHolder.getAdapterPosition());

            if (fragment instanceof FragmentHome) {

                if (!model.isLoading()) {
                    if(preferences.getUserData(context)!=null){
                    model.setLoading(true);}
                    notifyItemChanged(myHolder.getAdapterPosition());
                    FragmentHome fragmentHome = (FragmentHome) fragment;
                    fragmentHome.additemtoCart(model, myHolder.getAdapterPosition(), i);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ProductRowBinding binding;

        public MyHolder(@NonNull ProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
