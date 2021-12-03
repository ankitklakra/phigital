package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Model.ModelCustomerOrder;
import com.phigital.ai.R;
import com.phigital.ai.Shop.CustomerOrderView;
import com.phigital.ai.Shop.InvoiceActivity;
import com.phigital.ai.Shop.InvoiceOrderView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdapterInvoicelist extends RecyclerView.Adapter<AdapterInvoicelist.MyHolder>{

    final Context context;
    final List<ModelCustomerOrder> userList;

    public AdapterInvoicelist(Context context, List<ModelCustomerOrder> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_bill_list, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String id = userList.get(position).getId();
        final String pId = userList.get(position).getpId();
        final String invoicenumber = userList.get(position).getInvoicedate();
        final String orderdate = userList.get(position).getOrderdate();

//        long milliSeconds= Long.parseLong(orderdate);
//        String date = DateFormat.format("dd-MM-yyyy", milliSeconds).toString();

        holder.date.setText(orderdate);
        holder.invoice.setText(pId);

        FirebaseDatabase.getInstance().getReference().child("Invoice").child(pId).child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.qty.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    holder.itemView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String userId = snapshot.child("id").getValue().toString();
//                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
//                String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
//                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();
//
//                String mVerified = snapshot.child("verified").getValue().toString();
//
//                if (mVerified.isEmpty()){
//                    holder.verified.setVisibility(View.GONE);
//                }else {
//                    holder.verified.setVisibility(View.VISIBLE);
//                }
//
//                holder.mName.setText(name);
//                holder.mUsername.setText(username);
//
//
//                try {
//                    Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.avatar);
//                }catch (Exception ignored){
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InvoiceActivity.class);
            intent.putExtra("hispId", pId);
            intent.putExtra("hisId", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InvoiceOrderView.class);
                intent.putExtra("hispId", pId);
                intent.putExtra("hisId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final TextView date;
        final ImageView imageview;
        final TextView invoice;
        final TextView qty;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            invoice = itemView.findViewById(R.id.invoice);
            qty = itemView.findViewById(R.id.qty);
            imageview = itemView.findViewById(R.id.imageview);

        }

    }
}
