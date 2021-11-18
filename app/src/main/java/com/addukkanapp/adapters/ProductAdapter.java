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
import com.addukkanapp.databinding.MainCategoryRowBinding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_home.fragments.FragmentHome;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MainCategoryDataModel.ProductData> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private int parent_pos;
    private UserModel userModel;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;
    public ProductAdapter(List<MainCategoryDataModel.ProductData> list, Context context, Fragment fragment, int parent_pos) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        this.parent_pos = parent_pos;
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
        SingleProductModel model = list.get(position).getProduct_data();

        myHolder.binding.setModel(model);
        myHolder.binding.setCurrency(currecny);

        myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (model.getFavourite() != null) {
            myHolder.binding.checkbox.setChecked(true);
        } else {
            myHolder.binding.checkbox.setChecked(false);

        }
        myHolder.binding.checkbox.setOnClickListener(v -> {
            userModel = preferences.getUserData(context);

            SingleProductModel model2 = list.get(myHolder.getAdapterPosition()).getProduct_data();
            boolean checked = myHolder.binding.checkbox.isChecked();

            if (fragment instanceof FragmentHome) {
                if (userModel == null) {
                    myHolder.binding.checkbox.setChecked(!checked);
                } else {
                    FragmentHome fragment_main = (FragmentHome) fragment;
                    fragment_main.like_dislike(model2, myHolder.getAdapterPosition(), 2);

                }


            }


        });

        myHolder.itemView.setOnClickListener(view -> {
            if (fragment instanceof FragmentHome) {
                SingleProductModel model2 = list.get(myHolder.getAdapterPosition()).getProduct_data();

                FragmentHome fragment_main = (FragmentHome) fragment;


                fragment_main.showData(model2.getId() + "");

            }
        });
        myHolder.binding.imgIncrease.setOnClickListener(v -> {
            SingleProductModel model2 = list.get(myHolder.getAdapterPosition()).getProduct_data();

            if (fragment instanceof FragmentHome) {

                if (!model2.isLoading()) {
                    if (preferences.getUserData(context) != null) {
                        model2.setLoading(true);
                    }
                    notifyItemChanged(myHolder.getAdapterPosition());
                    FragmentHome fragmentHome = (FragmentHome) fragment;
                    fragmentHome.additemtoCart2(model2, myHolder.getAdapterPosition(), parent_pos);

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
