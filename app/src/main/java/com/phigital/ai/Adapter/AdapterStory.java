package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.phigital.ai.Model.ModelStory;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.Upload.AddStoryActivity;
import com.phigital.ai.Utility.StoryViewActivity;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;


import java.util.List;
import java.util.Objects;

public class AdapterStory extends RecyclerView.Adapter<AdapterStory.ViewHolder> {

    private final Context context;
    private final List<ModelStory> storyList;

    public AdapterStory(Context context, List<ModelStory> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        if (i == 0){
            view = LayoutInflater.from(context).inflate(R.layout.square_add_story, parent, false);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.square_story_item, parent, false);
        }
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ModelStory story = storyList.get(position);
        if (story != null){
            if (viewHolder.getAdapterPosition() != 0){
                seenStory(viewHolder, story.getUserid());
            }
            else if (viewHolder.getAdapterPosition() == 0){
                myStory(false);
            }
            viewHolder.itemView.setOnClickListener(v -> {
                if (viewHolder.getAdapterPosition() == 0){
                    myStory(true);
                }else {
                    Intent intent = new Intent(context, StoryViewActivity.class);
                    intent.putExtra("userid", story.getUserid());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            viewHolder.itemView.setOnLongClickListener(v -> {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("hisUid", story.getUserid());
                context.startActivity(intent);
                return false;
            });
            FirebaseDatabase.getInstance().getReference().child("Ban").child(story.getUserid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        viewHolder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams()      ;
                        params.height = 0;
                        params.width = 0;
                        viewHolder.itemView.setLayoutParams(params);
                    }else{
                        userInfo(viewHolder, story.getUserid(), position);
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
        return storyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final RoundedImageView story_photo;
        public final RoundedImageView imageView;
        public final RoundedImageView imageView2;
        public final RoundedImageView story_photo_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            imageView = itemView.findViewById(R.id.imageView);
            imageView2 = itemView.findViewById(R.id.imageView2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        return 1;
    }

    private void userInfo (ViewHolder viewHolder, String userId, int pos){
        FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                if (modelUser != null){
                    try{
                        Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).resize(150, 150).centerCrop().into(viewHolder.story_photo);
                    }catch(Exception e){
                        Picasso.get().load(R.drawable.placeholder).into(viewHolder.story_photo);
                    }
                    if (pos != 0){
                        try{
                            Picasso.get().load(Objects.requireNonNull(modelUser).getPhoto()).resize(150, 150).centerCrop().into(viewHolder.story_photo_seen);
                        }catch(Exception e){
                            Picasso.get().load(R.drawable.placeholder).into(viewHolder.story_photo_seen);
                        }
                    }
                }
//                Glide.with(context).load(Objects.requireNonNull(modelUser).getPhoto()).centerCrop().into(viewHolder.story_photo);
//                if (pos != 0){
//                    Glide.with(context).load(modelUser.getPhoto()).centerCrop().into(viewHolder.story_photo_seen);
////                    viewHolder.story_username.setText(modelUser.getName());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory(boolean click){
        FirebaseDatabase.getInstance().getReference("Story").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelStory story = snapshot1.getValue(ModelStory.class);
                    if (timecurrent > Objects.requireNonNull(story).getTimestart() && timecurrent < story.getTimeend()){
                        count++;
                    }

                }
                if (click){
                    Intent intent;
                    if (count > 0){
                        intent = new Intent(context, StoryViewActivity.class);
                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }else {
                        intent = new Intent(context, AddStoryActivity.class);
                    }
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(ViewHolder viewHolder, String userId){
        FirebaseDatabase.getInstance().getReference("Story").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
           if (!snapshot1.child("views").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists() && System.currentTimeMillis() < Objects.requireNonNull(snapshot1.getValue(ModelStory.class)).getTimeend()){
               i++;
           }
                }
                if (i > 0){
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                    viewHolder.imageView2.setVisibility(View.GONE);
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.imageView.setVisibility(View.GONE);
                    viewHolder.imageView2.setVisibility(View.VISIBLE);
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
