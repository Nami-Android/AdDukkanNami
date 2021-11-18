package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.RoomRowBinding;
import com.addukkanapp.databinding.UserSearchRowBinding;
import com.addukkanapp.models.RoomModel;
import com.addukkanapp.uis.activity_rooms.RoomsActivity;

import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<RoomModel> list;
    private RoomsActivity activity;

    public RoomAdapter(Context context, List<RoomModel> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        activity = (RoomsActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RoomRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.room_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(v -> {
            RoomModel roomModel = list.get(myHolder.getAdapterPosition());
            roomModel.setUnread_messages_count(0);
            list.set(myHolder.getAdapterPosition(),roomModel);
            notifyItemChanged(myHolder.getAdapterPosition());
            activity.setItemData(roomModel);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private RoomRowBinding binding;

        public MyHolder(RoomRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
