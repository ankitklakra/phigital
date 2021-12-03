package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.Model.ModelPost;

import com.google.firebase.auth.FirebaseAuth;
import com.phigital.ai.Post.SponsorDetails;
import com.phigital.ai.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


public class AdapterSearchImages extends RecyclerView.Adapter<AdapterSearchImages.MyHolder> {

    Context context;
    final List<ModelPost> postList;

    private String userId;

    boolean mProcessView = false;
    private final DatabaseReference viewRef;

    public AdapterSearchImages(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        viewRef = FirebaseDatabase.getInstance().getReference().child("SponsorViews");
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridcurvephoto, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String content = postList.get(position).getContent();
        String id = postList.get(position).getId();
        String image = postList.get(position).getImage();
//        String name = postList.get(position).getName();
        String pId = postList.get(position).getpId();
        String pTime = postList.get(position).getpTime();
        String text = postList.get(position).getText();
//        String vine = postList.get(position).getVine();
        String type = postList.get(position).getType();
        String pViews = postList.get(position).getpViews();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
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
        //DP
        if (type.equals("image")){
            try {
                Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.image);
            } catch (Exception ignored) {
            }
        }else{
            holder.itemView.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams()      ;
            params.height = 0;
            params.width = 0;
            holder.itemView.setLayoutParams(params);

        }
        holder.image.setOnClickListener(v -> {
            Intent intent;
            if (content.equals("sponsor")){
                intent = new Intent(context, SponsorDetails.class);
                intent.putExtra("postId", pId);
                intent.putExtra("id", id);
                intent.putExtra("mean", "sponsor");
            }else{
                intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                intent.putExtra("id", id);
                intent.putExtra("mean", "post");
            }
            context.startActivity(intent);
        });

        if (content.equals("sponsor")){
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mProcessView = true;
                    viewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView){
                                if (!dataSnapshot.child(postList.get(position).getpId()).hasChild(userId)) {
                                    viewRef.child(postList.get(position).getpId()).child(userId).setValue(true);
                                }
                                mProcessView = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return false;
                }
            });
            viewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(pId)){
                        viewRef.child(pId).addValueEventListener(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (Integer.parseInt(postList.get(position).getpViews()) <=(int) snapshot.getChildrenCount()){
                                        FirebaseDatabase.getInstance().getReference().child("Sponsorship").child(pId).child("privacy").setValue("private");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
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
        return postList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView image;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gridimage);

        }
    }
}








