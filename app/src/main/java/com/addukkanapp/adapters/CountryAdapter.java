package com.addukkanapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.CountryRowBinding;
import com.addukkanapp.databinding.DoctorRowBinding;
import com.addukkanapp.models.CountryModel;
import com.addukkanapp.uis.activity_countries.CountryActivity;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CountryModel> list;
    private LayoutInflater inflater;
    private Context context;
    private int i = -1;
   // private int old_pos = 0;


    public CountryAdapter(Context context,List<CountryModel> list) {
        inflater=LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            CountryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.country_row, parent, false);
            return new MyHolder(binding);


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        CountryModel countryModel = list.get(position);
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(countryModel);

        ((MyHolder) holder).binding.rb.setOnClickListener(view -> {


            if(context instanceof CountryActivity){
                CountryActivity countryActivity=(CountryActivity)context;
                countryActivity.setcountry(list.get(position));
            }

        });

if(i==position){
    ((MyHolder) holder).binding.rb.setSelected(true);

}
else {
    ((MyHolder) holder).binding.rb.setSelected(false);
}

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private CountryRowBinding binding;

        public MyHolder(CountryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

}
