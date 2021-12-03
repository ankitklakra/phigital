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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class AdapterUserSuggestion2 extends RecyclerView.Adapter<AdapterUserSuggestion2.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    String userId;
    public AdapterUserSuggestion2(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_suggestion, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelUser user = userList.get(position);
        if (user != null){
            final String hisUID = userList.get(position).getId();
            String userImage = userList.get(position).getPhoto();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            try{
                Picasso.get().load(userImage).placeholder(R.drawable.placeholder).resize(512, 512).centerCrop().into(holder.avatar);
            }catch(Exception e){
                Picasso.get().load(R.drawable.placeholder).into(holder.avatar);
            }

            holder.followtv.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Following").child(hisUID).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUID).child("Followers").child(userId).setValue(true);
                holder.followtv.setVisibility(View.GONE);
                holder.followingtv.setVisibility(View.VISIBLE);
            });

            holder.followingtv.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Following").child(hisUID).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUID).child("Followers").child(userId).removeValue();
                holder.followingtv.setVisibility(View.GONE);
                holder.followtv.setVisibility(View.VISIBLE);
            });

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Following");
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(hisUID).exists()){
                        holder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        holder.itemView.setLayoutParams(params);
                    }else {
                        holder.followtv.setVisibility(View.VISIBLE);
                        holder.followingtv.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);
            });

            FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        holder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        holder.itemView.setLayoutParams(params);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final TextView followtv;
        final TextView followingtv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circularImageView);
            followtv = itemView.findViewById(R.id.followtv);
            followingtv = itemView.findViewById(R.id.followingtv);
        }

    }
}
