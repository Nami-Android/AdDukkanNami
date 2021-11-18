package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.DoctorRowBinding;
import com.addukkanapp.databinding.SpecializeRowBinding;
import com.addukkanapp.models.SpecialModel;
import com.addukkanapp.uis.activity_ask_doctor.AskDoctorActivity;

import java.util.List;

public class SpecializationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<SpecialModel> list;
    private AskDoctorActivity activity;
    private int oldPos = -1;
    private int selectedPos = -1;

    public SpecializationAdapter(Context context,List<SpecialModel> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        activity = (AskDoctorActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SpecializeRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.specialize_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.rb.setOnClickListener(v -> {
           updateSelection(myHolder.getAdapterPosition());
        });

    }

    public void updateSelection(int adapterPosition) {
        selectedPos = adapterPosition;

        if (oldPos!=-1){
            SpecialModel oldModel = list.get(oldPos);
            oldModel.setSelected(false);
            list.set(oldPos,oldModel);
            //notifyItemChanged(oldPos);

        }
        if (adapterPosition!=-1){
            SpecialModel specialModel = list.get(selectedPos);
            specialModel.setSelected(true);
            list.set(selectedPos,specialModel);

            activity.setSpecializeItemData(specialModel,adapterPosition);
            //notifyItemChanged(selectedPos);

        }else {
            SpecialModel oldModel = list.get(oldPos);
            oldModel.setSelected(false);
            list.set(oldPos,oldModel);
            //notifyItemChanged(oldPos);

        }
        notifyDataSetChanged();
        oldPos = selectedPos;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private SpecializeRowBinding binding;

        public MyHolder(SpecializeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
