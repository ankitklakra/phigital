package com.phigital.ai.Adapter;

import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;


@SuppressWarnings("Convert2Lambda")
public class AdapterOnlineUserCheck extends RecyclerView.Adapter<AdapterOnlineUserCheck.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    String last_key;
    public AdapterOnlineUserCheck(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }
    private RequestQueue requestQueue;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        requestQueue = Volley.newRequestQueue(context);
        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsername = userList.get(position).getUsername();
        String stat = userList.get(position).getStatus();

        if (userName != null){
            holder.mName.setText(userName);
        }else{
            holder.mName.setText("Noname");
        }

//        Query getLastKey= FirebaseDatabase.getInstance().getReference()
//                .child("Users")
//                .orderByChild("status").equalTo(stat);
//
//        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                for(DataSnapshot lastkey : snapshot.getChildren()){
//                    last_key=lastkey.getKey();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//
//            }
//        });
//        if (last_key.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
//            holder.itemView.setVisibility(View.GONE);
//            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
//            params.height = 0;
//            params.width = 0;
//            holder.itemView.setLayoutParams(params);
//        }
        if (hisUID != null){
            holder.mUsername.setText(hisUID);
        }else{
            holder.mUsername.setText(userList.get(position).getStatus());
        }

        try {
            Picasso.get().load(userImage).resize(200, 200).centerCrop().into(holder.avatar);

        }catch (Exception ignored){
            Picasso.get().load(R.drawable.placeholder).into(holder.avatar);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("status").equalTo(stat).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (stat.equals("online")){
                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("status",timeStamp);
                                FirebaseDatabase.getInstance().getReference("Users").child(userList.get(position).getId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull @NotNull Void unused) {
                                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                        holder.itemView.setVisibility(View.GONE);
                                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                        params.height = 0;
                                        params.width = 0;
                                        holder.itemView.setLayoutParams(params);
                                    }
                                });
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final ImageView verified;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circleImageView2);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            verified = itemView.findViewById(R.id.verified);
        }
    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, "Warning", hisId, "profile", R.drawable.logo);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Timber.d("onResponse%s", response.toString()), error -> Timber.d("onResponse%s", error.toString())){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAA55rtIn4:APA91bHzTbsLtCMfjHcaVnaDC-iXGPVyPOGcAMFfs5vdg9uoCmEv9ifCDF8kCcyZOUudp8TbRLcC5AfQY5xS-wAujnJMB6OZ5xO-erpivhaFcdasN9ecJHtlfhmSYT2vQY19M-GMCVMK");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
