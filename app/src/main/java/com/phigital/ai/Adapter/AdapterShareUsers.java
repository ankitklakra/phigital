package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.Activity.ShareActivity;
import com.phigital.ai.Model.ModelUser;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterShareUsers extends RecyclerView.Adapter<AdapterShareUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    private final RequestQueue requestQueue;
    private boolean notify = false;
    private String userId;
    String action;
    String type;
    Intent intent;

    public AdapterShareUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.chatlist, parent, false);
        return new MyHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisId = userList.get(position).getId();
        String dp = userList.get(position).getPhoto();
        final String name = userList.get(position).getName();
        final String username = userList.get(position).getUsername();
        String lastactive = userList.get(position).getStatus();

        holder.mName.setText(name);
        holder.username.setText(username);

        try{
            Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.mDp);
        }catch (Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.mDp);
        }

        if (userList.get(position).getStatus().equals("online")){
            holder.time.setText("online");
        }else {
            long lastTime = Long.parseLong(lastactive);
            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
            holder.time.setText(lastSeenTime);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        intent = ((Activity) context).getIntent();
        action = intent.getAction();
        type = intent.getType();
        holder.itemView.setOnClickListener(v -> imBLockedOrNot(holder,hisId,name));

    }

    
    private void imBLockedOrNot (MyHolder holder,String hisId,String name){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(hisId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(context, "Can't send message, You have been blocked by "+name, Toast.LENGTH_SHORT).show();
                }else{
                    checkBlocked(hisId, holder,name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkBlocked(String hisId, MyHolder holder,String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(hisId)) {
                    Toast.makeText(context, "Can't send message, You have blocked "+name+" Unblock to send messages", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Please wait sending...", Toast.LENGTH_SHORT).show();
                    if (ShareActivity.getContent().equals("post")){
                        switch (ShareActivity.getType()) {
                            case"text":
                            case"meme":
                            case"image": {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("receiver", hisId);
                                hashMap.put("msg",ShareActivity.getPostId());
                                hashMap.put("isSeen", false);
                                hashMap.put("hide", false);
                                hashMap.put("timestamp", ""+System.currentTimeMillis());
                                hashMap.put("type", "post");
                                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(),ShareActivity.getPostId());
                                        }
                                        notify = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }else{
                        switch (ShareActivity.getType()) {
                            case"text":
                            case"image":
                            case"video": {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("receiver", hisId);
                                hashMap.put("msg",ShareActivity.getPostId());
                                hashMap.put("isSeen", false);
                                hashMap.put("hide", false);
                                hashMap.put("timestamp", ""+System.currentTimeMillis());
                                hashMap.put("type", ShareActivity.getType());

                                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(),ShareActivity.getPostId());
                                        }
                                        notify = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String hisUID, String name,String message) {
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userId, name+": "+"Sent you a post", "New Message", hisUID,"", R.drawable.logo);
                    Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                    try {
                        JSONObject senderJsonOnj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonOnj,
                                response -> Log.d("JSON_RESPONSE", "onResponse:" +response.toString() ), error -> Log.d("JSON_RESPONSE", "onResponse:" +error.toString() )){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAA55rtIn4:APA91bHzTbsLtCMfjHcaVnaDC-iXGPVyPOGcAMFfs5vdg9uoCmEv9ifCDF8kCcyZOUudp8TbRLcC5AfQY5xS-wAujnJMB6OZ5xO-erpivhaFcdasN9ecJHtlfhmSYT2vQY19M-GMCVMK");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView mDp;
        final TextView time;
        final TextView mName;
        final TextView username;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mDp = itemView.findViewById(R.id.circleImageView);
            time = itemView.findViewById(R.id.time);
            mName = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
        }

    }
}
