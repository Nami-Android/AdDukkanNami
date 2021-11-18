package com.addukkanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.databinding.OrderProductRow2Binding;
import com.addukkanapp.databinding.OrderProductRowBinding;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.SingleOrderModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;

import java.util.List;

public class Order2ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleOrderModel.Data.Detials> list;
    private Context context;
    private LayoutInflater inflater;
    private UserModel userModel;
    private Preferences preferences;
    private String currecny;
    private AppLocalSettings settings;
    //private Fragment_Main fragment_main;
    public Order2ProductAdapter(List<SingleOrderModel.Data.Detials> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
      //  this.fragment_main=fragment_main;
        preferences=Preferences.getInstance();

        settings = preferences.isLanguageSelected(context);

        userModel = preferences.getUserData(context);
        if (userModel != null) {
            currecny=userModel.getData().getUser_country().getCountry_setting_trans_fk().getCurrency();
        } else {
            currecny=settings.getCurrency();
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        OrderProductRow2Binding binding = DataBindingUtil.inflate(inflater, R.layout.order_product_row2, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setCurrency(currecny);

        myHolder.binding.setModel(list.get(position));
       // myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);




//Log.e("eeee",list.get(position).getOffer_value()+""+(list.get(position).getAmount()%list.get(position).getOffer_min()));
         // Log.e("ssss",((list.get(position).getHave_offer().equals("yes")?(list.get(position).getOffer_type().equals("per")?(list.get(position).getProduct_default_price().getPrice()-((list.get(position).getProduct_default_price().getPrice()*list.get(position).getOffer_value())/100)):list.get(position).getProduct_default_price().getPrice()-list.get(position).getOffer_value()):list.get(position).getProduct_default_price().getPrice())+""));
        myHolder.itemView.setOnClickListener(view -> {
           // Log.e("sssss",list.get(holder.getLayoutPosition()).getId()+"");

           // fragment_main.setitemData(list.get(holder.getLayoutPosition()).getId()+"");
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public OrderProductRow2Binding binding;

        public MyHolder(@NonNull OrderProductRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
