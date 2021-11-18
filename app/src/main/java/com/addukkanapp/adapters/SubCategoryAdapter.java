package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.SubCategoryrowBinding;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.SubCategoryDataModel;
import com.addukkanapp.uis.activity_home.fragments.FragmenDukkan;
import com.addukkanapp.uis.activity_home.fragments.FragmentHome;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SubCategoryDataModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private MainCategoryDataModel.Data data;
    //private Fragment_Main fragment_main;
    public SubCategoryAdapter(List<SubCategoryDataModel> list, Context context, Fragment fragment, MainCategoryDataModel.Data data) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment=fragment;
        this.data=data;
      //  this.fragment_main=fragment_main;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SubCategoryrowBinding binding = DataBindingUtil.inflate(inflater, R.layout.sub_categoryrow, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {
           // Log.e("sssss",list.get(holder.getLayoutPosition()).getId()+"");

           // fragment_main.setitemData(list.get(holder.getLayoutPosition()).getId()+"");
            if(fragment instanceof FragmenDukkan){
                FragmenDukkan fragmenDukkan=(FragmenDukkan)fragment;
                fragmenDukkan.filter(holder.getLayoutPosition());
            }
            else if(fragment instanceof FragmentHome){
                    FragmentHome fragmentHome=(FragmentHome)fragment;
                    fragmentHome.filter(holder.getLayoutPosition(),data);
                }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public SubCategoryrowBinding binding;

        public MyHolder(@NonNull SubCategoryrowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
