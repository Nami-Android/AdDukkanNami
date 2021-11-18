package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.CommentRowBinding;
import com.addukkanapp.databinding.RateRowBinding;
import com.addukkanapp.models.ProductDataModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductDataModel.Comment> list;
    private Context context;
    private LayoutInflater inflater;

    public CommentAdapter(List<ProductDataModel.Comment> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        CommentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.comment_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public CommentRowBinding binding;

        public MyHolder(@NonNull CommentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
