package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.phigital.ai.Activity.ShareGroupActivity;
import com.phigital.ai.Model.ModelChatListGroups;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.Activity.ShareActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterChatShareGroups extends RecyclerView.Adapter<AdapterChatShareGroups.MyHolder> {

    final Context context;
    final List<ModelChatListGroups> modelGroups;
    private boolean notify = false;
    private String userId;
    String action;
    String type;
    Intent intent;
    private final RequestQueue requestQueue;
    public AdapterChatShareGroups(Context context, List<ModelChatListGroups> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groupchatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
         String GroupId = modelGroups.get(position).getGroupId();
         String GroupName = modelGroups.get(position).getgName();
         String GroupUsername = modelGroups.get(position).getgUsername();
         String GroupIcon = modelGroups.get(position).getgIcon();

        holder.mName.setText(GroupName);
        holder.mUsername.setText(GroupUsername);

        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.group).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.group).into(holder.avatar);
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        intent = ((Activity) context).getIntent();
        action = intent.getAction();
        type = intent.getType();
        holder.itemView.setOnClickListener(v -> imBLockedOrNot(holder,GroupId,GroupName));
//        holder.itemView.setOnClickListener(v -> {
//            String timeStamp = ""+ System.currentTimeMillis();
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("sender", userId);
//            hashMap.put("msg", ShareGroupActivity.getPostId());
//            hashMap.put("type", "post");
//            hashMap.put("timestamp", timeStamp);
//
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
//            ref.child(GroupId).child("Message").child(timeStamp)
//                    .setValue(hashMap)
//                    .addOnSuccessListener(aVoid -> {
//                        Intent intent = new Intent(context, GroupChat.class);
//                        intent.putExtra("groupId", GroupId);
//                        context.startActivity(intent);
//                    }).addOnFailureListener(e -> {
//
//            });
//
//        });

    }
    private void imBLockedOrNot (MyHolder holder, String hisId, String name){
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

    private void checkBlocked(String hisId,MyHolder holder, String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(hisId)) {
                    Toast.makeText(context, "Can't send message, You have blocked "+name+" Unblock to send messages", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Please wait sending...", Toast.LENGTH_SHORT).show();
                    if (ShareGroupActivity.getContent().equals("post")){
                        switch (ShareGroupActivity.getType()) {
                            case"text":
                            case"meme":
                            case"video":
                            case"image": {

                                String stamp = "" + System.currentTimeMillis();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("msg",ShareGroupActivity.getPostId());
                                hashMap.put("type", "post");
                                hashMap.put("timestamp", stamp);

                                FirebaseDatabase.getInstance().getReference("Groups").child(hisId).child("Message").push()
                                        .setValue(hashMap);

                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(),ShareGroupActivity.getPostId());
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
                        switch (ShareGroupActivity.getType()) {
                            case"text":
                            case"meme":
                            case"image":
                            case"video": {
                                String stamp = "" + System.currentTimeMillis();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("msg",ShareGroupActivity.getPostId());
                                hashMap.put("timestamp", stamp);
                                hashMap.put("type", ShareGroupActivity.getType());

                                FirebaseDatabase.getInstance().getReference("Groups").child(hisId).child("Message").push()
                                        .setValue(hashMap);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(),ShareGroupActivity.getPostId());
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
    @Override
    public int getItemCount() {
        return modelGroups.size();
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

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circleImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);

        }
    }
}
