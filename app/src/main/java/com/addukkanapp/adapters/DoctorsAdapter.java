package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.DoctorRowBinding;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.uis.activity_ask_doctor.AskDoctorActivity;

import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<UserModel.Data> list;
    private AskDoctorActivity activity;

    public DoctorsAdapter(Context context,List<UserModel.Data> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        activity= (AskDoctorActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DoctorRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.doctor_row, parent, false);
        return new DoctorHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        DoctorHolder doctorHolder = (DoctorHolder) holder;
        doctorHolder.binding.setModel(list.get(position));
        holder.itemView.setOnClickListener(v -> {
            UserModel.Data model = list.get(doctorHolder.getAdapterPosition());
            activity.setDoctorItemData(model);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DoctorHolder extends RecyclerView.ViewHolder {
        private DoctorRowBinding binding;

        public DoctorHolder(DoctorRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
