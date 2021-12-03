package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelCalllist;
import com.phigital.ai.R;
import com.phigital.ai.Chat.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCallList extends RecyclerView.Adapter<AdapterCallList.MyHolder> {

    final Context context;
    final List<ModelCalllist> userList;
    private final HashMap<String, String> lastMessageMap;
    private final DatabaseReference likeRef;

    public AdapterCallList(Context context, List<ModelCalllist> userList) {
        this.context = context;
        this.userList = userList;
     lastMessageMap = new HashMap<>();
        likeRef = FirebaseDatabase.getInstance().getReference().child("CallList");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.calllist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUid = userList.get(position).getId();
        String dp = userList.get(position).getPhoto();
        String name = userList.get(position).getName();
        String phone = userList.get(position).getPhone();
        String lastactive = userList.get(position).getpId();
        String user = userList.get(position).getUserid();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String myUid = currentUser.getUid();

        holder.mName.setText(name);

        try {
            Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.mDp);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.placeholder).into(holder.mDp);
        }
        try {
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            long lastTime = Long.parseLong(lastactive);
            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
            holder.message.setText(lastSeenTime);
        } catch (Exception ignored) {

        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("hisUid", user);
            context.startActivity(intent);
        });

        holder.call.setOnClickListener(v -> {
            String postId = userList.get(position).getpId();
            String phone2 = userList.get(position).getPhone();
            String timeStamp = String.valueOf(System.currentTimeMillis());
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("id", myUid);
            hashMap.put("name", name);
            hashMap.put("photo", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("phone", phone);
            hashMap.put("userid", user);
            DatabaseReference dRef2 = FirebaseDatabase.getInstance().getReference("CallList");
            dRef2.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                    });
            Uri call = Uri.parse("tel:" + phone);
            Intent surf = new Intent(Intent.ACTION_CALL, call);
            context.startActivity(surf);
        });
    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView mDp;
        final TextView time;
        final TextView mName;
        final TextView message;
        final ImageView call;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.circleImageView);
            time = itemView.findViewById(R.id.time);
            mName = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.username);
            call = itemView.findViewById(R.id.call);
        }
    }

}
