package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Model.ModelCart;
import com.phigital.ai.Model.ModelCustomerOrder;
import com.phigital.ai.Model.ModelProduct;
import com.phigital.ai.R;
import com.phigital.ai.Shop.InvoiceActivity;

import java.util.List;

public class AdapterProductInvoicelist extends RecyclerView.Adapter<AdapterProductInvoicelist.MyHolder>{

    final Context context;
    final List<ModelCart> userList;

    public AdapterProductInvoicelist(Context context, List<ModelCart> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.product_invoice_list, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String id = userList.get(position).getId();
        final String pId = userList.get(position).getpId();
        final String proname = userList.get(position).getProductname();
        final String price = userList.get(position).getPrice();
        final String quan = userList.get(position).getQuantity();


        holder.productname.setText(proname);
        holder.subtotaltv.setText(price);
        holder.qtytv.setText(quan);
        holder.gsttv.setText("5 %");

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final TextView productname;
        final TextView qtytv;
        final TextView gsttv;
        final TextView subtotaltv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            productname = itemView.findViewById(R.id.productname);
            gsttv = itemView.findViewById(R.id.gsttv);
            qtytv = itemView.findViewById(R.id.qtytv);
            subtotaltv = itemView.findViewById(R.id.subtotaltv);

        }

    }
}
