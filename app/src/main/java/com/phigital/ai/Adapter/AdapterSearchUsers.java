package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.R;
import com.phigital.ai.Chat.ChatActivity;
import com.phigital.ai.Model.ModelUser;

import com.phigital.ai.Utility.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class AdapterSearchUsers extends RecyclerView.Adapter<AdapterSearchUsers.MyHolder>{
    private FirebaseAuth mAuth;
    String userId;
    final Context context;
    final List<ModelUser> userList;

    public AdapterSearchUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_new, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsernsme = userList.get(position).getUsername();
        String city = userList.get(position).getCity();
        String birthdate = userList.get(position).getBirthdate();
        String job = userList.get(position).getJob();
        String mVerified = userList.get(position).getVerified();

        holder.mName.setText(userName);
        holder.mUsername.setText(userUsernsme);
        holder.citynum.setText(city);
        holder.birthdate.setText(birthdate);
        holder.likesnum.setText(job);


        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams()      ;
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (mVerified.isEmpty()){
            holder.verified.setVisibility(View.GONE);
        }else {
            holder.verified.setVisibility(View.VISIBLE);
        }

        holder.followtv.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                    .child("Following").child(hisUID).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUID)
                    .child("Followers").child(userId).setValue(true);
            holder.followtv.setVisibility(View.GONE);
            holder.followingtv.setVisibility(View.VISIBLE);
        });

        holder.followingtv.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                    .child("Following").child(hisUID).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUID)
                    .child("Followers").child(userId).removeValue();
            holder.followingtv.setVisibility(View.GONE);
            holder.followtv.setVisibility(View.VISIBLE);
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisUID).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.followernum.setText(""+dataSnapshot.getChildrenCount()+" People");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        try{
            Picasso.get()
                    .load(userImage)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.avatar);
        }catch(Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.avatar);
        }
        holder.mssg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", hisUID);
            context.startActivity(intent);
        });

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userId).child("Following");
                 mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(hisUID).exists()){
                    holder.followtv.setVisibility(View.GONE);
                    holder.followingtv.setVisibility(View.VISIBLE);
                }else {
                    holder.followtv.setVisibility(View.VISIBLE);
                    holder.followingtv.setVisibility(View.GONE);
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

        final ImageView avatar;
        final ImageView blockedIV;
        final ImageView mssg;
        final ImageView verified;
        final TextView mName;
        final TextView mUsername;
        final TextView time;
        final TextView followernum;
        final TextView birthdate;
        final TextView citynum;
        final TextView likesnum;
        final TextView followtv;
        final TextView followingtv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circularImageView);
            mName = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            mssg = itemView.findViewById(R.id.mssg);
            mUsername = itemView.findViewById(R.id.username);
            blockedIV = itemView.findViewById(R.id.blockedIV);
            followernum = itemView.findViewById(R.id.followernum);
            birthdate = itemView.findViewById(R.id.birthdate);
            likesnum = itemView.findViewById(R.id.likesnum);
            citynum = itemView.findViewById(R.id.citynum);
            followtv = itemView.findViewById(R.id.followtv);
            followingtv = itemView.findViewById(R.id.followingtv);
            verified = itemView.findViewById(R.id.verified);
        }

    }
}
