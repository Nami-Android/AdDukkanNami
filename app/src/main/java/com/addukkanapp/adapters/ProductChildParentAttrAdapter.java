package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.AttrRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.ProductDataModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;

import java.util.List;


public class ProductChildParentAttrAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<ProductDataModel.Attribute> list;
    private UserModel userModel;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;
    public ProductChildParentAttrAdapter(Context context, List<ProductDataModel.Attribute> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
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

        AttrRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.attr_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setCurrency(currecny);

        myHolder.binding.setTitle(list.get(position).getAttribute_trans_fk().getTitle());

        if (list.get(position).getAttributes()!=null){
            myHolder.binding.recView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            ChildAdapter adapter = new ChildAdapter(context,list.get(position).getAttributes(),"child",position);
            myHolder.binding.recView.setAdapter(adapter);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private AttrRowBinding binding;

        public MyHolder(AttrRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
