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
import com.addukkanapp.databinding.OfferProductRowBinding;
import com.addukkanapp.databinding.ProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.SingleProductModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.uis.activity_home.fragments.FragmentOffer;
import com.addukkanapp.uis.activity_product_filter.ProductFilterActivity;
import com.addukkanapp.uis.activity_search.SearchActivity;

import java.util.List;

public class ProductOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
private UserModel userModel;
private Preferences preferences;
private String currecny;
    private AppLocalSettings settings;


    public ProductOfferAdapter(List<SingleProductModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
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


        OfferProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_product_row, parent, false);
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
        }
        myHolder.itemView.setOnClickListener(view -> {
            if (fragment instanceof FragmentOffer) {

                FragmentOffer fragmentOffer = (FragmentOffer) fragment;

                fragmentOffer.setItemData(list.get(myHolder.getAdapterPosition()).getId()+"");

            }else if(context instanceof ProductFilterActivity){
                ProductFilterActivity activity=(ProductFilterActivity) context;
                activity.showData(list.get(holder.getLayoutPosition()).getId()+"");
            }
            else if(context instanceof SearchActivity){
                SearchActivity activity=(SearchActivity) context;
                activity.showData(list.get(holder.getLayoutPosition()).getId()+"");

            }

        });
        myHolder.binding.checkbox.setOnClickListener(v -> {

            if (fragment instanceof FragmentOffer) {

                FragmentOffer fragment_main = (FragmentOffer) fragment;

                fragment_main.like_dislike(list.get(myHolder.getLayoutPosition()), myHolder.getLayoutPosition(), 0);

            } else if (context instanceof ProductFilterActivity) {
                ProductFilterActivity productFilterActivity = (ProductFilterActivity) context;

                productFilterActivity.like_dislike(list.get(myHolder.getLayoutPosition()), myHolder.getLayoutPosition(), 0);

            }
            else if (context instanceof SearchActivity) {
                SearchActivity productFilterActivity = (SearchActivity) context;

                productFilterActivity.like_dislike(list.get(myHolder.getLayoutPosition()), myHolder.getLayoutPosition(), 0);

            }


        });
        myHolder.binding.imgIncrease.setOnClickListener(v -> {
            SingleProductModel model = list.get(myHolder.getAdapterPosition());
            if (!model.isLoading()) {
                if(Preferences.getInstance().getUserData(context)!=null){
                    model.setLoading(true);}
                notifyItemChanged(myHolder.getAdapterPosition());}

            if (fragment instanceof FragmentOffer) {

                FragmentOffer fragment_main = (FragmentOffer) fragment;

                fragment_main.additemtoCart(model, myHolder.getAdapterPosition(), 0);

            } else if (context instanceof ProductFilterActivity) {
                ProductFilterActivity productFilterActivity = (ProductFilterActivity) context;
                productFilterActivity.additemtoCart(model, myHolder.getAdapterPosition(), 0);

            } else if (context instanceof SearchActivity) {
                SearchActivity productFilterActivity = (SearchActivity) context;
                productFilterActivity.additemtoCart(model, myHolder.getAdapterPosition(), 0);

            }


        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public OfferProductRowBinding binding;

        public MyHolder(@NonNull OfferProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
