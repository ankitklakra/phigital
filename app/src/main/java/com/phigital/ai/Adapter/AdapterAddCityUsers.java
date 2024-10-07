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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;




@SuppressWarnings("Convert2Lambda")
public class AdapterAddCityUsers extends RecyclerView.Adapter<AdapterAddCityUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    public AdapterAddCityUsers(Context context, List<ModelUser> userList) {
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

        if (userName != null){
            holder.mName.setText(userName);
        }else{
            holder.mName.setText("Noname");
        }

        if (hisUID != null){
            holder.mUsername.setText(hisUID);
        }else{
            holder.mUsername.setText("Noid");
        }

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }catch (Exception ignored){
            Picasso.get().load(R.drawable.placeholder).into(holder.avatar);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Users").child(userList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("city","");
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
}
