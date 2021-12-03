package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;


@SuppressWarnings("Convert2Lambda")
public class AdapterAdminUsers extends RecyclerView.Adapter<AdapterAdminUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    public AdapterAdminUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }
    private RequestQueue requestQueue;
    private boolean notify = false;
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
        String mVerified = userList.get(position).getVerified();

        holder.mName.setText(userName);
        holder.mUsername.setText(userUsername);


        try {
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }catch (Exception ignored){

        }

//        if (mVerified.isEmpty()){
//            holder.verified.setVisibility(View.GONE);
//        }else {
//            holder.verified.setVisibility(View.VISIBLE);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemView, Gravity.END);
                FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            popupMenu.getMenu().add(Menu.NONE,1,0, "Remove Ban");
                        }else {
                            popupMenu.getMenu().add(Menu.NONE,2,0, "Ban user");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                popupMenu.getMenu().add(Menu.NONE,3,0, "Remove from report");
                popupMenu.getMenu().add(Menu.NONE,4,0, "Send warning to user");
                popupMenu.getMenu().add(Menu.NONE,5,0, "View user profile");
                popupMenu.getMenu().add(Menu.NONE,6,0, "Remove from warning");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == 1 ){
                            FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).removeValue();
                            Snackbar.make(v, "User removed from banned", Snackbar.LENGTH_LONG).show();
                        }
                        else if (id == 2){
                            FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).setValue(true);
                            Snackbar.make(v, "User banned", Snackbar.LENGTH_LONG).show();
                        }
                        else if (id == 3){
                            FirebaseDatabase.getInstance().getReference().child("userReport").child(hisUID).removeValue();
                            Snackbar.make(v, "User removed from report", Snackbar.LENGTH_LONG).show();
                        }
                        else if (id == 4) {
                            Snackbar.make(v, "Warning sent", Snackbar.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference("warn").child("user").child(userList.get(position).getId()).setValue(true);
                            //Notification
                            String timestamp = ""+System.currentTimeMillis();
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("pId", "");
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("pUid", userList.get(position).getId());
                            hashMap.put("notification", "You have got a warning by the Phigital");
                            hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            FirebaseDatabase.getInstance().getReference("Users").child(userList.get(position).getId()).child("Notifications").child(timestamp).setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference("Users").child(userList.get(position).getId()).child("Count").child(timestamp).setValue(true);
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "You have got a warning by the Phigital");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            FirebaseDatabase.getInstance().getReference().child("userReport").child(hisUID).removeValue();
                        }
                        else if (id == 5) {
                            Intent intent = new Intent(context, UserProfile.class);
                            intent.putExtra("hisUid", userList.get(position).getId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else if (id == 6) {
                            FirebaseDatabase.getInstance().getReference("warn").child("user").child(userList.get(position).getId()).getRef().removeValue();
                            Snackbar.make(v, "Removed", Snackbar.LENGTH_LONG).show();
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            holder.itemView.setLayoutParams(params);
                        }
                        return false;
                    }
                });
                popupMenu.show();

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
