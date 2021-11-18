package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.BrandRowBinding;
import com.addukkanapp.databinding.ChildRowBinding;
import com.addukkanapp.models.ProductDataModel;
import com.addukkanapp.uis.activity_product_detials.ProductDetialsActivity;

import java.util.List;


public class ChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<ProductDataModel.Attribute> list;
    private ProductDetialsActivity activity;
    private String type;
    private int parent_pos;
    private int current_pos = -1;
    private int old_pos = -1;
    private int child_old_pos = -1;

    public ChildAdapter(Context context, List<ProductDataModel.Attribute> list,String type,int parent_pos) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        activity = (ProductDetialsActivity) context;
        this.type = type;
        this.parent_pos = parent_pos;
        if (type.equals("parent")){
            old_pos = getDefaultPos();
            current_pos = old_pos;
        }else {
            child_old_pos = getDefaultPos();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChildRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.child_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        ProductDataModel.Attribute attribute = list.get(position);
        if (type.equals("parent")){
            if (current_pos == position){
                attribute.setIs_default("yes");
            }else {
                attribute.setIs_default("no");

            }
        }


        myHolder.binding.setModel(attribute);



        myHolder.itemView.setOnClickListener(v -> {
            current_pos = myHolder.getAdapterPosition();
            ProductDataModel.Attribute model = list.get(current_pos);
            if (type.equals("parent")){
                model.setIs_default("yes");
                list.set(current_pos, model);
                notifyItemChanged(current_pos);
                if (old_pos!=-1){
                    ProductDataModel.Attribute model2 = list.get(old_pos);
                    model.setIs_default("no");
                    list.set(old_pos, model2);
                    notifyItemChanged(old_pos);

                }

                old_pos =current_pos;

            }
            activity.getFeatures(list.get(current_pos).getId(),parent_pos,current_pos,child_old_pos,type);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private ChildRowBinding binding;

        public MyHolder(ChildRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    private int getDefaultPos(){
        int pos = -1;
        for (int index=0;index<list.size();index++){
            if (list.get(index).getIs_default().equals("yes")){
                pos = index;
                return pos;
            }
        }
        return pos;
    }
}
