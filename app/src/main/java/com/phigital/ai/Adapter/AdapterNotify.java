package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.phigital.ai.Article.ArticleCommentsActivity;
import com.phigital.ai.Article.ArticleViewActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelNotification;
import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.Post.SponsorDetails;
import com.phigital.ai.R;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterNotify extends RecyclerView.Adapter<AdapterNotify.Holder>  {

    private final Context context;
    private final ArrayList<ModelNotification> notifications;
    private String userId;

    public AdapterNotify(Context context, ArrayList<ModelNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);

        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ModelNotification modelNotification = notifications.get(position);
        String mName = modelNotification.getsName();
        String type = modelNotification.getType();
        String notification = modelNotification.getNotification();
        String image = modelNotification.getsImage();
        String timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getsUid();
        String postId = modelNotification.getpUid();
        String pId = modelNotification.getpId();

        Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.imv);
        GetTimeAgo getNotificationTime = new GetTimeAgo();
        long lastTime = Long.parseLong(timestamp);
        String lastSeenTime = GetTimeAgo.getNotificationTime(lastTime);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("id").equalTo(senderUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String mName = ""+ds.child("name").getValue();
                            String uName = ""+ds.child("username").getValue();
                            String image2 = ""+ds.child("photo").getValue();
                            modelNotification.setsName(uName);
                            modelNotification.setsImage(image);
                            holder.name.setText(uName);

                            try {
                                Picasso.get().load(image2).placeholder(R.drawable.placeholder).into(holder.circleImageView);
                            }catch (Exception e){
                                Picasso.get().load(image2).placeholder(R.drawable.placeholder).into(holder.circleImageView);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.time.setText(lastSeenTime);
        holder.username.setText(notification);

        holder.itemView.setOnClickListener(v -> {
            String sender = modelNotification.getsUid();
            if (notification.toLowerCase().equals("liked on your article")){
                Intent intent = new Intent(context, ArticleViewActivity.class);
                intent.putExtra("articleId", postId);
                intent.putExtra("id", sender);
                context.startActivity(intent);
            }else if (notification.toLowerCase().equals("comment on your article")){
                Intent intent = new Intent(context, ArticleCommentsActivity.class);
                intent.putExtra("articleId", postId);
                intent.putExtra("id", sender);
                intent.putExtra("image", image);
                context.startActivity(intent);
            }else if (notification.toLowerCase().equals("liked on your post")){
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", postId);
                intent.putExtra("id",sender);
                intent.putExtra("mean", "post");
                context.startActivity(intent);
            } else if (notification.toLowerCase().equals("liked on your sponsor")){
                Intent intent = new Intent(context, SponsorDetails.class);
                intent.putExtra("postId", postId);
                intent.putExtra("id",sender);
                intent.putExtra("mean", "sponsor");
                context.startActivity(intent);
            }else if (notification.toLowerCase().equals("repost on your post" +
                    "")) {
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", postId);
                intent.putExtra("id",sender);
                intent.putExtra("mean", "post");
                context.startActivity(intent);
            }else if (notification.toLowerCase().equals("liked on your comment")) {
                 Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", postId);
                intent.putExtra("id",sender);
                intent.putExtra("mean", "post");
                context.startActivity(intent);
            }else if (notification.toLowerCase().equals("comment on your post")) {
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", postId);
                intent.putExtra("id",sender);
                intent.putExtra("mean", "post");
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("hisUid", senderUid);
                context.startActivity(intent);
            }

        });

        holder.itemView.setOnLongClickListener(v -> {
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setTitle("Delete");
         builder.setMessage("Are you sure to delete this notification?");
         builder.setPositiveButton("Delete", (dialog, which) -> {
             DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
             ref.child(userId).child("Notifications").child(timestamp).removeValue().addOnSuccessListener(aVoid -> {
             }).addOnFailureListener(e -> {
             });
         }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
         builder.create().show();
         return false;
     });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        final CircleImageView circleImageView;
        final TextView username;
        final TextView name;
        final TextView time;
        final ImageView imv;

        public Holder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView3);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            imv = itemView.findViewById(R.id.imv);
        }
    }
}
