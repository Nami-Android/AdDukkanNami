package com.addukkanapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.addukkanapp.R;
import com.addukkanapp.databinding.SpinnerRowBinding;
import com.addukkanapp.models.CountryModel;

import java.util.List;

public class SpinnerCountryAdapter extends BaseAdapter {
    private List<CountryModel> list;
    private Context context;
    private LayoutInflater inflater;

    public SpinnerCountryAdapter(List<CountryModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") SpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row,parent,false);
        String title = list.get(position).getCountry_setting_trans_fk().getTitle();
        binding.setTitle(title);
        return binding.getRoot();
    }
}
