package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.R;
import com.phigital.ai.Shop.CustomerOrderView;
import com.phigital.ai.Model.ModelUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCustomerOrders extends RecyclerView.Adapter<AdapterCustomerOrders.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    public AdapterCustomerOrders(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsername = userList.get(position).getUsername();
        String mVerified = userList.get(position).getVerified();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.itemView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.mName.setText(userName);
        holder.mUsername.setText(userUsername);

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }catch (Exception ignored){

        }

        if (!userImage.isEmpty()){
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }

        if (mVerified.isEmpty()){
            holder.verified.setVisibility(View.GONE);
        }else {
            holder.verified.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CustomerOrderView.class);
                intent.putExtra("hisUid", hisUID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        final ImageView verified;
        final ImageView avatar;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circularImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            verified = itemView.findViewById(R.id.verified);
        }
    }
}
