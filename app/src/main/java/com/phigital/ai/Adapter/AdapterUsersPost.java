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

public class AdapterUsersPost extends RecyclerView.Adapter<AdapterUsersPost.MyHolder>{
//
//    private AdapterCallback mAdapterCallback;
    final Context context;
    final List<ModelUser> userList;

    private static  int aCount = 0;
    private static final int NUM_ONE = 1;
    private static final int NUM_TWO = 2;
    private static final int NUM_THREE = 3;
    private static final int NUM_FOUR = 4;
    private static final int NUM_FIVE = 5;

    public AdapterUsersPost(Context context, List<ModelUser> userList) {
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
//        if (position ==0){
//            aCount++;
//            Intent intent = new Intent(context, PostActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("hisUsername", userUsernsme);
//            intent.putExtras(bundle);
////            mAdapterCallback.onMethodCallback();
////            intent.putExtra("hisUsername", userUsernsme);
//
////            context.startActivity(intent);
////            ((Activity)context).startActivityForResult(intent,NUM_ONE);
//        }
        Intent intent = new Intent("usertag");
        intent.putExtra("username", userUsernsme);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//        if (position ==1){
//            aCount++;
//            Intent intent2 = new Intent(context, PostActivity.class);
//            intent2.putExtra("hisUsername2", userUsernsme);
////            context.startActivity(intent2);
//            ((Activity)context).startActivityForResult(intent2,NUM_TWO);
//        }
//        if (position ==2){
//            aCount++;
//            Intent intent3 = new Intent(context, PostActivity.class);
//            intent3.putExtra("hisUsername3", userUsernsme);
////            context.startActivity(intent3);
//            ((Activity)context).startActivityForResult(intent3,NUM_THREE);
//        }
//        if (position ==3){
//            aCount++;
//            Intent intent4 = new Intent(context, PostActivity.class);
//            intent4.putExtra("hisUsername4", userUsernsme);
////            context.startActivity(intent4);
//            ((Activity)context).startActivityForResult(intent4,NUM_FOUR);
//        }
//        if (position ==4){
//            aCount++;
//            Intent intent5 = new Intent(context, PostActivity.class);
//            intent5.putExtra("hisUsername5", userUsernsme);
////            context.startActivity(intent5);
//            ((Activity)context).startActivityForResult(intent5,NUM_FIVE);
//        }

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
