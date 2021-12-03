package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterTagUser extends RecyclerView.Adapter<AdapterTagUser.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    public AdapterTagUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsernsme = userList.get(position).getUsername();

        holder.mName.setText(userName);
        holder.mUsername.setText(userUsernsme);

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }catch (Exception ignored){
        }

        holder.blockedIV.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            thiswasclicked(position,userUsernsme);

        });

    }

    private void thiswasclicked(int position,String userUsernsme) {

        Intent intent = new Intent("usertag");
        intent.putExtra("username2", userUsernsme);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

     static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final ImageView blockedIV;
        final TextView mName;
        final TextView mUsername;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circularImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            blockedIV = itemView.findViewById(R.id.blockedIV);
        }

    }

    public static interface AdAdapterCallback {
        void onMethodCallback(String data);
    }
}
