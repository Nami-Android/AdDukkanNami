package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.addukkanapp.R;
import com.addukkanapp.databinding.NotificationRowBinding;
import com.addukkanapp.models.NotificationModel;
import com.addukkanapp.uis.activity_notification.NotificationActivity;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotificationModel> list;
    private Context context;
    private LayoutInflater inflater;
    private NotificationActivity activity;
    public NotificationAdapter(List<NotificationModel> list, Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (NotificationActivity) context;
        this.list =list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.notification_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;

            NotificationModel notificationModel = list.get(position);
            myHolder.binding.setModel(notificationModel);

            myHolder.binding.imageDelete.setOnClickListener(view -> {

                activity.deleteNotification(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());

            });



        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public NotificationRowBinding binding;

        public MyHolder(@NonNull NotificationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
