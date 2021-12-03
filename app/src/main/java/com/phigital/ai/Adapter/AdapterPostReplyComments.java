package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelCommentsReply;
import com.phigital.ai.Model.ModelUser;

import com.phigital.ai.Notifications.Data2;
import com.phigital.ai.Notifications.Sender2;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.Post.PostComments;
import com.phigital.ai.R;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Utility.UserProfile;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterPostReplyComments extends RecyclerView.Adapter<AdapterPostReplyComments.MyHolder> {

    Context context ;
    final List<ModelCommentsReply> commentsList;
    private DatabaseReference likeRef;
    boolean mProcessLike = false;
    private boolean notify = false;
    private String userId;
    private RequestQueue requestQueue;

    public AdapterPostReplyComments(Context context, List<ModelCommentsReply> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment2, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String id = commentsList.get(position).getId();
        String cId = commentsList.get(position).getcId();
        String comment = commentsList.get(position).getComment();
        String time = commentsList.get(position).getTimestamp();
        String pId = commentsList.get(position).getpId();
        String type = commentsList.get(position).getType();
        String maincId = commentsList.get(position).getMaincId();

        switch (type) {
            case "text":
                holder.mComment.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.mComment.setText(comment);
                holder.play.setVisibility(View.GONE);
                break;
            case "image":
                holder.mComment.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                Glide.with(context).asBitmap().centerCrop().load(comment).into(holder.rec_img);
                break;
            case "video":
                holder.mComment.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                holder.rec_img.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(comment).into(holder.rec_vid);
                break;
        }

        likeRef = FirebaseDatabase.getInstance().getReference().child("cLikes");
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
        holder.mName.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.mDp.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.mComment.setLinkText(comment);
        holder.mComment.setOnLinkClickListener((i, s) -> {
            if (i == 1){
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("hashTag", s);
                context.startActivity(intent);
            }else
            if (i == 2){
                String username = s.replaceFirst("@","");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                Query query = ref.orderByChild("username").equalTo(username.trim());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                String id1 = ds.child("id").getValue().toString();
                                if (id1.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Intent intent = new Intent(context, UserProfile.class);
                                    intent.putExtra("hisUid", id1);
                                    context.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context, UserProfile.class);
                                    intent.putExtra("hisUid", id1);
                                    context.startActivity(intent);
                                }
                            }
                        }else {

                            Toast.makeText(context, "Invalid username, can't find user with this username", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else if (i == 16){
                String url = s;
                if (!url.startsWith("https://") && !url.startsWith("http://")){
                    url = "http://" + url;
                }
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(openUrlIntent);
            }else if (i == 4){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                context.startActivity(intent);
            }else if (i == 8){
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, s);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                context.startActivity(intent);

            }
        });

        long lastTime = Long.parseLong(time);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
        holder.time.setText(lastSeenTime);

        getUserInfo(holder, id);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        holder.mComment.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                updateLike(id,cId,pId);
            }
        }));


        holder.reply.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    String username = (Objects.requireNonNull(user).getUsername());
                    Intent intent = new Intent("comment");
                    intent.putExtra("fromComment2", username);
                    intent.putExtra("postId2", pId);
                    intent.putExtra("fromCid2", maincId);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
            if (id.equals(userId)){
                builder.setTitle("Delete");
            builder.setMessage("Are you sure to delete this comment?");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
                ref.child("Comments").child(maincId).child("replies").child(cId).removeValue()
                        .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {
                });
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            }else {
                builder.setTitle("Report");
            builder.setMessage("Are you sure to report this comment?");
            builder.setPositiveButton("Report", (dialog, which) -> {

                Toast.makeText(context, "Comment Reported", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            }
            builder.create().show();
            return false;

        });

        holder.image.setOnClickListener(v -> {
            updateLike(id,cId,pId);
        });

        FirebaseDatabase.getInstance().getReference().child("cLikes").child(commentsList.get(position).getcId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0){
                    holder.liketv.setText("Like");
                    holder.liketv.setVisibility(View.VISIBLE);
                }else {
                    holder.liketv.setVisibility(View.VISIBLE);
                    holder.liketv.setText(String.valueOf(snapshot.getChildrenCount()));
                }
                if (snapshot.hasChild(userId)){
                    holder.image.setImageResource(R.drawable.icon_fav2);
                }else {
                    holder.image.setImageResource(R.drawable.icon_fav);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void updateLike(String id,String postId,String pId) {
        mProcessLike = true;
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                if (mProcessLike){
                    if (dataSnapshot.child(postId).hasChild(userID)) {
                        likeRef.child(postId).child(userID).removeValue();
                        mProcessLike = false;
                    } else {
                        likeRef.child(postId).child(userID).setValue(true);
                        mProcessLike = false;
                        if(!id.equals(userID)){
                            addToHisNotification(id, "Liked on your Comment", pId);
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        sendNotification(id, Objects.requireNonNull(user).getName(), "Liked on your Comment",pId);
                                    }
                                    notify = false;
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String hisId, final String name,final String message,final String postId){
        requestQueue = Volley.newRequestQueue(context);
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data2 data = new Data2(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Notification", hisId, R.drawable.logo,postId);
                    assert token != null;
                    Sender2 sender = new Sender2(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Log.d("JSON_RESPONSE", "onResponse" + response.toString()), error -> Log.d("JSON_RESPONSE", "onResponse" + error.toString())){
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

    private void getUserInfo(MyHolder holder, String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                holder.mName.setText(Objects.requireNonNull(user).getName());
                try {
                    Picasso.get().load(user.getPhoto()).placeholder(R.drawable.placeholder).into(holder.mDp);
                }catch (Exception ignored){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToHisNotification(String id, String message, String pId){
        String moId = PostComments.getmeme();
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", id);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", pId);
        hashMap.put("notification", message);
        hashMap.put("sImage", moId);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("Notifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("Count").child(timestamp).setValue(true);

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        final CircleImageView mDp;
        final TextView mName;
        final SocialTextView mComment;
        final TextView time;
        final TextView reply;
        final TextView liketv;
        final ImageView rec_img;
        final ImageView rec_vid;
        final ImageView play;
        final ImageView image;
        final ImageView delete;
        final ConstraintLayout com;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.dp);
            mName = itemView.findViewById(R.id.name);
            liketv = itemView.findViewById(R.id.liketv);
            reply = itemView.findViewById(R.id.reply);

            rec_img = itemView.findViewById(R.id.rec_img);
            rec_vid = itemView.findViewById(R.id.rec_vid);
            play = itemView.findViewById(R.id.play);

            mComment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_medium);
            Typeface typeface2 = ResourcesCompat.getFont(context, R.font.roboto_regular);
            mName.setTypeface(typeface);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            mComment.setTypeface(typeface2);
            reply.setTypeface(typeface);
            liketv.setTypeface(typeface);
            time.setTypeface(typeface);
            com = itemView.findViewById(R.id.messageLayout);
        }
    }

}
