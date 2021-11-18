package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.SideMenuSubCategoryRowBinding;
import com.addukkanapp.databinding.SubCategoryrowBinding;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.SubCategoryDataModel;
import com.addukkanapp.uis.activity_home.HomeActivity;

import java.util.List;

public class SideMenuSubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SubCategoryDataModel> list;
    private Context context;
    private LayoutInflater inflater;
    private MainCategoryDataModel.Data data;
    //private Fragment_Main fragment_main;
    public SideMenuSubCategoryAdapter(List<SubCategoryDataModel> list, Context context, MainCategoryDataModel.Data data) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data=data;
      //  this.fragment_main=fragment_main;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SideMenuSubCategoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.side_menu_sub_category_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {
           // Log.e("sssss",list.get(holder.getLayoutPosition()).getId()+"");
 if(context instanceof HomeActivity){
     HomeActivity homeActivity=(HomeActivity)context;
                homeActivity.filter(holder.getLayoutPosition(),data);
            }
           // fragment_main.setitemData(list.get(holder.getLayoutPosition()).getId()+"");
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public SideMenuSubCategoryRowBinding binding;

        public MyHolder(@NonNull SideMenuSubCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
