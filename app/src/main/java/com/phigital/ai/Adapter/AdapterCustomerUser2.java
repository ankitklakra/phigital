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
import com.phigital.ai.Shop.CustomerOrderView2;
import com.phigital.ai.Model.ModelCustomerOrder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class AdapterCustomerUser2 extends RecyclerView.Adapter<AdapterCustomerUser2.MyHolder>{

    final Context context;
    final List<ModelCustomerOrder> userList;

    public AdapterCustomerUser2(Context context, List<ModelCustomerOrder> userList) {
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

        final String id = userList.get(position).getId();
        final String pId = userList.get(position).getpId();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.child("id").getValue().toString();
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                String mVerified = snapshot.child("verified").getValue().toString();

                if (mVerified.isEmpty()){
                    holder.verified.setVisibility(View.GONE);
                }else {
                    holder.verified.setVisibility(View.VISIBLE);
                }

                holder.mName.setText(name);
                holder.mUsername.setText(username);


                try {
                    Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.avatar);
                }catch (Exception ignored){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerOrderView2.class);
            intent.putExtra("hispId", pId);
            intent.putExtra("hisId", id);
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
