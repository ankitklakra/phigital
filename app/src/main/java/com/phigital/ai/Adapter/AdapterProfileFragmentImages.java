package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.Model.ModelPost;

import com.google.firebase.auth.FirebaseAuth;
import com.phigital.ai.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


public class AdapterProfileFragmentImages extends RecyclerView.Adapter<AdapterProfileFragmentImages.MyHolder> {

    Context context;
    final List<ModelPost> postList;

    private String userId;

    public AdapterProfileFragmentImages(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridsquarephoto, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String content = postList.get(position).getContent();
        String id = postList.get(position).getId();
        String image = postList.get(position).getImage();
        String pId = postList.get(position).getpId();
        String type = postList.get(position).getType();
        FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        holder.close.setVisibility(View.GONE);
        if (type.equals("image")){
            try{
                Picasso.get().load(image).resize(512, 512).centerCrop().into(holder.image);
            }catch(Exception e){
                Picasso.get().load(R.drawable.placeholder).into(holder.image);
            }
        }else{
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
        //DP

        holder.image.setOnClickListener(v -> {
            if (content.equals("sponsor")){
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                intent.putExtra("id", id);
                intent.putExtra("mean", "sponsor");
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                intent.putExtra("id", id);
                intent.putExtra("mean", "post");
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView image;
        final ImageView close;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gridimage);
            close = itemView.findViewById(R.id.close);

        }
    }
}








