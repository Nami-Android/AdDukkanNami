package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.CountryRow2Binding;
import com.addukkanapp.databinding.CountryRowBinding;
import com.addukkanapp.models.CountryModel;
import com.addukkanapp.uis.activity_login.LoginActivity;
import com.addukkanapp.uis.activity_sign_up.SignUpActivity;

import java.util.List;

public class CountryAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CountryModel> list;
    private LayoutInflater inflater;
    private Context context;
    private AppCompatActivity activity;

    public CountryAdapter2(Context context, List<CountryModel> list) {
        inflater=LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        activity = (AppCompatActivity) context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            CountryRow2Binding binding = DataBindingUtil.inflate(inflater, R.layout.country_row2, parent, false);
            return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        CountryModel countryModel = list.get(position);
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(countryModel);

        holder.itemView.setOnClickListener(view -> {
            CountryModel model = list.get(myHolder.getAdapterPosition());
            if (activity instanceof LoginActivity){
                LoginActivity loginActivity = (LoginActivity) activity;
                loginActivity.setItemCountry(model);

            }else {
                SignUpActivity signUpActivity = (SignUpActivity) activity;
                signUpActivity.setItemCountry(model);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private CountryRow2Binding binding;

        public MyHolder(CountryRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

}
