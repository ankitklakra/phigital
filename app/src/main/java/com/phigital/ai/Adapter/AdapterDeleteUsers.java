package com.phigital.ai.Adapter;

import android.app.AlertDialog;
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




@SuppressWarnings("Convert2Lambda")
public class AdapterDeleteUsers extends RecyclerView.Adapter<AdapterDeleteUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    String last_key;
    public AdapterDeleteUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String stat = userList.get(position).getStatus();

        if (userName != null){
            holder.mName.setText(userName);
        }else{
            holder.mName.setText("Noname");
        }

        if (hisUID != null){
            holder.mUsername.setText(hisUID);
        }else{
            holder.mUsername.setText(userList.get(position).getStatus());
        }

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }catch (Exception ignored){
            Picasso.get().load(R.drawable.placeholder).into(holder.avatar);
        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
//                builder.setTitle("Delete");
//                builder.setMessage("Are you sure to delete this user?");
//                builder.setPositiveButton("Delete", (dialog, which) -> {
//                    Query query1 = FirebaseDatabase.getInstance().getReference("Users").orderByChild("status").equalTo(stat);
//                    query1.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                            for (DataSnapshot ds: snapshot.getChildren()){
//                                ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(@NonNull @NotNull Void unused) {
//                                        holder.itemView.setVisibility(View.GONE);
//                                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
//                                        params.height = 0;
//                                        params.width = 0;
//                                        holder.itemView.setLayoutParams(params);
//                                    }
//                                });
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                        }
//                    });
//                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("status").equalTo("online").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        if (stat.equals("online")){
//                            Toast.makeText(context, userList.get(position).getStatus(), Toast.LENGTH_SHORT).show();
////                            snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
////                                @Override
////                                public void onSuccess(@NonNull @NotNull Void unused) {
////                                    holder.itemView.setVisibility(View.GONE);
////                                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
////                                    params.height = 0;
////                                    params.width = 0;
////                                    holder.itemView.setLayoutParams(params);
////                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
////                                }
////                            });
//                        }
                        for(DataSnapshot lastkey : snapshot.getChildren()){
                            last_key=lastkey.getKey();
                            Toast.makeText(context, last_key, Toast.LENGTH_SHORT).show();
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
}
