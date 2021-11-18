package com.addukkanapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.OrderProductRowBinding;
import com.addukkanapp.databinding.OrderRowBinding;
import com.addukkanapp.models.SingleOrderModel;
import com.addukkanapp.uis.activity_order.MyOrderActivity;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleOrderModel.Data> list;
    private Context context;
    private LayoutInflater inflater;

    //private Fragment_Main fragment_main;
    public MyOrderAdapter(List<SingleOrderModel.Data> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        //  this.fragment_main=fragment_main;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        OrderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.order_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.tvDetials.setPaintFlags(myHolder.binding.tvDetials.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        MyOrderProductDetialsAdapter myOrderProductDetialsAdapter = new MyOrderProductDetialsAdapter(list.get(position).getOrder_details(), context);
        myHolder.binding.recView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        myHolder.binding.recView.setAdapter(myOrderProductDetialsAdapter);


//Log.e("eeee",list.get(position).getOffer_value()+""+(list.get(position).getAmount()%list.get(position).getOffer_min()));
        // Log.e("ssss",((list.get(position).getHave_offer().equals("yes")?(list.get(position).getOffer_type().equals("per")?(list.get(position).getProduct_default_price().getPrice()-((list.get(position).getProduct_default_price().getPrice()*list.get(position).getOffer_value())/100)):list.get(position).getProduct_default_price().getPrice()-list.get(position).getOffer_value()):list.get(position).getProduct_default_price().getPrice())+""));
        myHolder.binding.tvDetials.setOnClickListener(view -> {
            // Log.e("sssss",list.get(holder.getLayoutPosition()).getId()+"");
if(context instanceof MyOrderActivity){
    MyOrderActivity myOrderActivity=(MyOrderActivity)context;
    myOrderActivity.showDetials(list.get(holder.getLayoutPosition()).getId()+"");
}
            // fragment_main.setitemData(list.get(holder.getLayoutPosition()).getId()+"");
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public OrderRowBinding binding;

        public MyHolder(@NonNull OrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
