package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.phigital.ai.Groups.GroupProfile;
import com.phigital.ai.Model.ModelGroups;
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

public class AdapterAdminGroups extends RecyclerView.Adapter<AdapterAdminGroups.MyHolder> {

    final Context context;
    final List<ModelGroups> modelGroups;

    public AdapterAdminGroups(Context context, List<ModelGroups> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
    }
    private RequestQueue requestQueue;
    private boolean notify = false;
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_display, parent, false);
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

        holder.itemView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.mName, Gravity.END);
            FirebaseDatabase.getInstance().getReference().child("GroupBan").child(GroupId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        popupMenu.getMenu().add(Menu.NONE,1,0, "Remove Ban");
                    }else {
                        popupMenu.getMenu().add(Menu.NONE,2,0, "Ban Group");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            popupMenu.getMenu().add(Menu.NONE,3,0, "Remove from report");
            popupMenu.getMenu().add(Menu.NONE,4,0, "Send warning to group");
            popupMenu.getMenu().add(Menu.NONE,5,0, "View group profile");
            popupMenu.getMenu().add(Menu.NONE,6,0, "Remove from warning");
            popupMenu.getMenu().add(Menu.NONE,7,0, "Delete group");
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == 1){
                        FirebaseDatabase.getInstance().getReference().child("GroupBan").child(GroupId).removeValue();
                        Snackbar.make(v, "Group removed from banned", Snackbar.LENGTH_LONG).show();
                    }
                    else if (id == 2){
                        FirebaseDatabase.getInstance().getReference().child("GroupBan").child(GroupId).setValue(true);
                        Snackbar.make(v, "Group banned", Snackbar.LENGTH_LONG).show();
                    }
                    else if (id == 3){
                        FirebaseDatabase.getInstance().getReference().child("GroupReport").child(GroupId).removeValue();
                        Snackbar.make(v, "Group removed from report", Snackbar.LENGTH_LONG).show();
                    }
                    else if (id == 4){
                        Snackbar.make(v, "Warning sent", Snackbar.LENGTH_LONG).show();
                        FirebaseDatabase.getInstance().getReference("warn").child("group").child(GroupId).setValue(true);
                        //Notification
                        String timestamp = ""+System.currentTimeMillis();
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("pId", "");
                        hashMap.put("timestamp", timestamp);
                        hashMap.put("pUid", "");
                        hashMap.put("notification", "You have got a warning by the Phigital");
                        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("GroupNotifications").child(timestamp).setValue(hashMap);
                        FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Count").child(timestamp).setValue(true);
                        notify = true;
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ModelUser user = snapshot.getValue(ModelUser.class);
                                if (notify){
                                    FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Participants").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                sendNotification(ds.getKey(), Objects.requireNonNull(user).getName(), "Your group got a warning by the Phigital");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("GroupReport").child(GroupId).removeValue();
                    }
                    else if(id ==5){
                        Intent intent = new Intent(context, GroupProfile.class);
                        intent.putExtra("groupId", GroupId);
                        context.startActivity(intent);
                    }else if (id == 6) {
                        FirebaseDatabase.getInstance().getReference("warn").child("group").child(GroupId).getRef().removeValue();
                        Snackbar.make(v, "Removed", Snackbar.LENGTH_LONG).show();
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        holder.itemView.setLayoutParams(params);
                    } else if (id == 7){
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId).removeValue();
                        Snackbar.make(v, "Group deleted", Snackbar.LENGTH_LONG).show();
                    }
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return modelGroups.size();
    }



    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circularImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);

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
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, "Warning", hisId, "group", R.drawable.logo);
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
