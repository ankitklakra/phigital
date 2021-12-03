package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data2;
import com.phigital.ai.Notifications.Sender2;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.Post.PostComments;
import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.PostBottomSheet;
import com.phigital.ai.R;
import com.phigital.ai.Utility.UserProfile;
import com.rishabhharit.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

//@SuppressWarnings("ALL")
public class AdapterPostHome extends FirebaseRecyclerAdapter<ModelPost,AdapterPostHome.MyHolder> {

    Context context;

    private String userId;
    private RequestQueue requestQueue;
    private boolean notify = false;

    private final DatabaseReference viewRef;
    private final DatabaseReference likeRef;
    private final DatabaseReference rejoyRef;
    boolean mProcessLike = false;
    boolean mProcessRejoy = false;
    boolean mProcessView = false;

    public AdapterPostHome(@NonNull FirebaseRecyclerOptions<ModelPost> options) {
        super(options);
        viewRef = FirebaseDatabase.getInstance().getReference().child("SponsorViews");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        rejoyRef = FirebaseDatabase.getInstance().getReference().child("ReTweet");
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull @NotNull AdapterPostHome.MyHolder holder, int position, @NonNull @NotNull ModelPost model) {
//        holder.setdata(model);
        String id = model.getId();
        String postId = model.getpId();
        String pText = model.getText();
        String pType = model.getType();
        String pView = model.getpViews();
        String video = model.getVideo();
        String image = model.getImage();
        String privacy = model.getPrivacy();
        String pTime = model.getpTime();
        String link = model.getLink();
        String content = model.getContent();
        String rViews = model.getrViews();
        String location = model.getLocation();
        String reTweet = model.getReTweet();
        String reId = model.getReId();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//        Time
        long lastTime = Long.parseLong(pTime);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
        holder.time.setText(lastSeenTime);
        if (content.equals("normal")){
            if (reTweet.isEmpty()){
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

                //User details
                FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String realId = snapshot.child("id").getValue().toString();
                        String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                        String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                        String mVerified = snapshot.child("verified").getValue().toString();

                        if (mVerified.isEmpty()){
                            holder.verified.setVisibility(View.GONE);
                        }else {
                            holder.verified.setVisibility(View.VISIBLE);
                        }

                        holder.pName.setText(name);

                        if (!dp.isEmpty()) {
                            try{
                                Picasso.get()
                                        .load(dp)
                                        .resize(100, 100)
                                        .centerCrop()
                                        .into(holder.pCircleImageView);
                            }catch(Exception e){
                                Picasso.get().load(R.drawable.placeholder).into(holder.pCircleImageView);
                            }
//                            Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.pCircleImageView);
                        }

                        //ClickToPro
                        holder.pName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, UserProfile.class);
                                intent.putExtra("hisUid", realId);
                                context.startActivity(intent);
                            }
                        });

                        holder.pCircleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, UserProfile.class);
                                intent.putExtra("hisUid", realId);
                                context.startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                if(!location.isEmpty()){
                    holder.location.setVisibility(View.VISIBLE);
                    holder.location.setText(location);
                    holder.dot3.setVisibility(View.VISIBLE);
                }

                switch (pType){
                    case "text": {
                        holder.pTextview.setLinkText(pText);
                        holder.pTextview.setVisibility(View.VISIBLE);
                        holder.pTextview.setOnLinkClickListener((i, s) -> {
                            if (i == 1) {
                                Intent intent = new Intent(context, SearchActivity.class);
                                intent.putExtra("hashTag", s);
                                AdapterPostHome.this.context.startActivity(intent);
                            }
                            else if (i == 2){
                                String username = s.replaceFirst("@","");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                Query query = ref.orderByChild("username").equalTo(username.trim());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                String id12 = ds.child("id").getValue().toString();
                                                Intent intent = new Intent(context, UserProfile.class);
                                                intent.putExtra("hisUid", id12);
                                                context.startActivity(intent);
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
                            }
                            else if (i == 4){
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                context.startActivity(intent);
                            }
                            else if (i == 8){
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, s);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                context.startActivity(intent);
                            }
                        });
                        break;
                    }
                    case"image": {
                        if (!pText.isEmpty()){
                            holder.pTextview.setLinkText(pText);
                            holder.pTextview.setVisibility(View.VISIBLE);
                            holder.pTextview.setOnLinkClickListener((i, s) -> {
                                if (i == 1) {
                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra("hashTag", s);
                                    AdapterPostHome.this.context.startActivity(intent);
                                }
                                else if (i == 2){
                                    String username = s.replaceFirst("@","");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                    Query query = ref.orderByChild("username").equalTo(username.trim());
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot ds : snapshot.getChildren()){
                                                    String id1 = ds.child("id").getValue().toString();
                                                    Intent intent = new Intent(context, UserProfile.class);
                                                    intent.putExtra("hisUid", id1);
                                                    context.startActivity(intent);
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
                                }
                                else if (i == 4){
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                    context.startActivity(intent);
                                }
                                else if (i == 8){
                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setData(Uri.parse("mailto:"));
                                    intent.putExtra(Intent.EXTRA_EMAIL, s);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                    context.startActivity(intent);

                                }
                            });
                        }else{
                            holder.pTextview.setVisibility(View.GONE);
                        }
                        holder.pMeme.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.pMeme);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                //Like
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateLike(id,postId,image);
                    }
                });

                holder.rejoy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessRejoy = true;
                        rejoyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (mProcessRejoy){
                                    if (snapshot.child(postId).hasChild(userId)) {
                                        holder.rejoy.setImageResource(R.drawable.icon_loop);
                                        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String realId = ds.child("id").getValue().toString();
                                                    if (realId.equals(userId)){
                                                        ds.getRef().removeValue();
                                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).child(userId).removeValue();
                                                        Toast.makeText(context, "Repost Removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        holder.rejoy.setImageResource(R.drawable.icon_loop2);
                                        String timeStamp = String.valueOf(System.currentTimeMillis());
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("id", userId);
                                        hashMap.put("pId", timeStamp);
                                        hashMap.put("text", pText);
                                        hashMap.put("pViews", pView);
                                        hashMap.put("rViews", rViews);
                                        hashMap.put("type", pType);
                                        hashMap.put("video", video);
                                        hashMap.put("image", image);
                                        hashMap.put("reTweet", id);
                                        hashMap.put("content", content);
                                        hashMap.put("reId", postId);
                                        hashMap.put("privacy", ""+privacy);
                                        hashMap.put("pTime", pTime);
                                        hashMap.put("location", location);
                                        hashMap.put("link", link);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
                                            if(!id.equals(userId)){
                                                addToHisNotification(id, "Repost on your post", postId,image);
                                                notify = true;
                                                FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                                        if (notify){
                                                            sendNotification(id, Objects.requireNonNull(user).getName(), "Repost on your post",postId);
                                                        }
                                                        notify = false;
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).child(userId).setValue(true);
                                            Toast.makeText(context, "Repost", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    mProcessRejoy = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                checkLike(holder,postId,userId);

                numberLike(holder,postId,userId);

                checkReTweet(holder,postId,userId);

                numberReTweet(holder,postId,userId);

                holder.pMeme.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        updateLike(id,postId,image);
                    }

                    @Override
                    public void onDoubleClick(View view) {
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("id", id);
                        intent.putExtra("mean", "post");
                        context.startActivity(intent);
                    }
                }));

                holder.pMeme.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(context, PostComments.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("id", id);
                        context.startActivity(intent);
                        return false;
                    }
                });

                holder.pTextview.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        updateLike(id,postId,image);
                    }

                    @Override
                    public void onDoubleClick(View view) {
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("id", id);
                        intent.putExtra("mean", "post");
                        context.startActivity(intent);
                    }
                }));

                holder.pTextview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(context, PostComments.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("id",id);
                        context.startActivity(intent);
                        return false;
                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PostComments.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("id", id);
                        context.startActivity(intent);
                    }
                });

                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pType.equals("image")){
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.pMeme.getDrawable();
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            Bundle args = new Bundle();
                            args.putString("id", id);
                            args.putString("postId", postId);
                            args.putByteArray("byteArray",byteArray);
                            PostBottomSheet bottomSheet = new PostBottomSheet();
                            bottomSheet.setArguments(args);
                            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                        }else{
                            Bundle args = new Bundle();
                            args.putString("id", id);
                            args.putString("postId",postId);
                            PostBottomSheet bottomSheet = new PostBottomSheet();
                            bottomSheet.setArguments(args);
                            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                        }
                    }

                });

                FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() == 0){
                            holder.commentNo.setText("0");
                        }else {
                            holder.commentNo.setText(String.valueOf(snapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {

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

                FirebaseDatabase.getInstance().getReference().child("Ban").child(reTweet).addValueEventListener(new ValueEventListener() {
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
                //ReTweetDetails
                FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String realId = snapshot.child("id").getValue().toString();
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                        holder.username.setVisibility(View.VISIBLE);
                        holder.username.setText(username);

                        holder.dot2.setVisibility(View.VISIBLE);

                        holder.username.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, UserProfile.class);
                                intent.putExtra("hisUid", realId);
                                context.startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //User details
                FirebaseDatabase.getInstance().getReference().child("Users").child(reTweet).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String realId = Objects.requireNonNull(snapshot.child("id").getValue()).toString();
                        String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                        String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                        holder.pName.setText(name);

                        String mVerified = snapshot.child("verified").getValue().toString();

                        if (mVerified.isEmpty()){
                            holder.verified.setVisibility(View.GONE);
                        }else {
                            holder.verified.setVisibility(View.VISIBLE);
                        }

                        if (!dp.isEmpty()) {
                            try{
                                Picasso.get()
                                        .load(dp)
                                        .resize(100, 100)
                                        .centerCrop()
                                        .into(holder.pCircleImageView);
                            }catch(Exception e){
                                Picasso.get().load(R.drawable.placeholder).into(holder.pCircleImageView);
                            }
//                            Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.pCircleImageView);
                        }

                        holder.pName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, UserProfile.class);
                                intent.putExtra("hisUid", realId);
                                context.startActivity(intent);
                            }
                        });

                        holder.pCircleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, UserProfile.class);
                                intent.putExtra("hisUid", realId);
                                context.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (!location.isEmpty()){
                    holder.location.setVisibility(View.VISIBLE);
                    holder.location.setText(location);
                    holder.dot3.setVisibility(View.VISIBLE);
                }

                switch (pType){
                    case "text": {
                        holder.pTextview.setLinkText(pText);
                        holder.pTextview.setVisibility(View.VISIBLE);
                        holder.pTextview.setOnLinkClickListener((i, s) -> {
                            if (i == 1) {
                                Intent intent = new Intent(context, SearchActivity.class);
                                intent.putExtra("hashTag", s);
                                AdapterPostHome.this.context.startActivity(intent);
                            }
                            else if (i == 2){
                                String username = s.replaceFirst("@","");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                Query query = ref.orderByChild("username").equalTo(username.trim());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                String id12 = ds.child("id").getValue().toString();
                                                Intent intent = new Intent(context, UserProfile.class);
                                                intent.putExtra("hisUid", id12);
                                                context.startActivity(intent);
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
                            }
                            else if (i == 4){
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                context.startActivity(intent);
                            }
                            else if (i == 8){
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, s);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                context.startActivity(intent);

                            }
                        });
                        break;
                    }
                    case"image": {
                        if (!pText.isEmpty()){
                            holder.pTextview.setLinkText(pText);
                            holder.pTextview.setVisibility(View.VISIBLE);
                            holder.pTextview.setOnLinkClickListener((i, s) -> {
                                if (i == 1) {
                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra("hashTag", s);
                                    AdapterPostHome.this.context.startActivity(intent);
                                }
                                else if (i == 2){
                                    String username = s.replaceFirst("@","");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                    Query query = ref.orderByChild("username").equalTo(username.trim());
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot ds : snapshot.getChildren()){
                                                    String id1 = ds.child("id").getValue().toString();
                                                    Intent intent = new Intent(context, UserProfile.class);
                                                    intent.putExtra("hisUid", id1);
                                                    context.startActivity(intent);
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
                                }
                                else if (i == 4){
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                    context.startActivity(intent);
                                }
                                else if (i == 8){
                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setData(Uri.parse("mailto:"));
                                    intent.putExtra(Intent.EXTRA_EMAIL, s);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                    context.startActivity(intent);

                                }
                            });
                        }else{
                            holder.pTextview.setVisibility(View.GONE);
                        }
                        holder.pMeme.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.pMeme);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                //Like
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateLike(reTweet,reId,image);
                    }
                });

                holder.rejoy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessRejoy = true;
                        rejoyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (mProcessRejoy) {
                                    if (dataSnapshot.child(reId).hasChild(userId)) {
                                        holder.rejoy.setImageResource(R.drawable.icon_loop);
                                        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(reId);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String realId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                                    if (userId.equals(realId)){
                                                        ds.getRef().removeValue();
                                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(reId).child(userId).removeValue();
                                                        Toast.makeText(context, "Repost Removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        holder.rejoy.setImageResource(R.drawable.icon_loop2);
                                        String timeStamp = String.valueOf(System.currentTimeMillis());
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("id", userId);
                                        hashMap.put("pId", timeStamp);
                                        hashMap.put("text", pText);
                                        hashMap.put("pViews", pView);
                                        hashMap.put("rViews", rViews);
                                        hashMap.put("type", pType);
                                        hashMap.put("video", video);
                                        hashMap.put("image", image);
                                        hashMap.put("content", content);
                                        hashMap.put("reTweet", reTweet);
                                        hashMap.put("reId", reId);
                                        hashMap.put("privacy", ""+privacy);
                                        hashMap.put("pTime", pTime);
                                        hashMap.put("link", link);
                                        hashMap.put("location", location);
                                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                        dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NotNull Void aVoid) {
                                                if(!reTweet.equals(userId)){
                                                    addToHisNotification(reTweet, "Repost on your post", postId,image);
                                                    notify = true;
                                                    FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            ModelUser user = snapshot.getValue(ModelUser.class);
                                                            if (notify){
                                                                sendNotification(reTweet, Objects.requireNonNull(user).getName(), "Repost on your post",reId);
                                                            }
                                                            notify = false;
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(reId).child(userId).setValue(true);
                                                Toast.makeText(context, "Repost", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    mProcessRejoy = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                checkLike(holder,reId,userId);

                //LikeNumber
                numberLike(holder,reId,userId);

                //CheckReTweet
                checkReTweet(holder,reId,userId);

                //ReTweetNumber
                numberReTweet(holder,reId,userId);

                holder.pMeme.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        updateLike(reTweet,reId,image);

                    }

                    @Override
                    public void onDoubleClick(View view) {
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", reId);
                        intent.putExtra("id", reTweet);
                        intent.putExtra("mean", "post");
                        context.startActivity(intent);
                    }
                }));

                holder.pMeme.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(context, PostComments.class);
                        intent.putExtra("postId", reId);
                        intent.putExtra("id", reTweet);
                        context.startActivity(intent);
                        return false;
                    }
                });

                holder.pTextview.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        updateLike(reTweet,reId,image);
                    }

                    @Override
                    public void onDoubleClick(View view) {
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", reId);
                        intent.putExtra("id", reTweet);
                        intent.putExtra("mean", "post");
                        context.startActivity(intent);
                    }
                }));

                holder.pTextview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(context, PostComments.class);
                        intent.putExtra("postId", reId);
                        intent.putExtra("id", reTweet);
                        context.startActivity(intent);
                        return false;
                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PostComments.class);
                        intent.putExtra("postId", reId);
                        intent.putExtra("id", reTweet);
                        context.startActivity(intent);
                    }
                });

                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pType.equals("image")){
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.pMeme.getDrawable();
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            Bundle args = new Bundle();
                            args.putString("id", id);
                            args.putString("postId", reId);
                            args.putByteArray("byteArray",byteArray);
                            PostBottomSheet bottomSheet = new PostBottomSheet();
                            bottomSheet.setArguments(args);
                            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                        }else{
                            Bundle args = new Bundle();
                            args.putString("id", id);
                            args.putString("postId", reId);
                            PostBottomSheet bottomSheet = new PostBottomSheet();
                            bottomSheet.setArguments(args);
                            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                        }
                    }
                });

                FirebaseDatabase.getInstance().getReference("Posts").child(reId).child("Comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() == 0){
                            holder.commentNo.setText("0");
                        }else {
                            holder.commentNo.setText(String.valueOf(snapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else{
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

            //User details
            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String realId = snapshot.child("id").getValue().toString();
                    String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                    String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                    String mVerified = snapshot.child("verified").getValue().toString();

                    if (mVerified.isEmpty()){
                        holder.verified.setVisibility(View.GONE);
                    }else {
                        holder.verified.setVisibility(View.VISIBLE);
                    }

                    holder.pName.setText(name);

                    if (!dp.isEmpty()) {
                        Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.pCircleImageView);
                    }

                    //ClickToPro
                    holder.pName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, UserProfile.class);
                            intent.putExtra("hisUid", realId);
                            context.startActivity(intent);
                        }
                    });

                    holder.pCircleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, UserProfile.class);
                            intent.putExtra("hisUid", realId);
                            context.startActivity(intent);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            if (!location.isEmpty()) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(location);
                holder.dot3.setVisibility(View.VISIBLE);
            }

            holder.username.setVisibility(View.VISIBLE);
            holder.username.setText("sponsored");
            holder.dot2.setVisibility(View.VISIBLE);

            switch (pType){
                case "text": {
                    holder.pTextview.setLinkText(pText);
                    holder.pTextview.setVisibility(View.VISIBLE);
                    holder.pTextview.setOnLinkClickListener((i, s) -> {
                        if (i == 1) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra("hashTag", s);
                            AdapterPostHome.this.context.startActivity(intent);
                        }
                        else if (i == 2){
                            String username = s.replaceFirst("@","");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            Query query = ref.orderByChild("username").equalTo(username.trim());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            String id12 = ds.child("id").getValue().toString();
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", id12);
                                            context.startActivity(intent);
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
                        }
                        else if (i == 4){
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                            context.startActivity(intent);
                        }
                        else if (i == 8){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, s);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                            context.startActivity(intent);

                        }
                    });
                    break;
                }
                case"image": {
                    if (!pText.isEmpty()){
                        holder.pTextview.setLinkText(pText);
                        holder.pTextview.setVisibility(View.VISIBLE);
                        holder.pTextview.setOnLinkClickListener((i, s) -> {
                            if (i == 1) {
                                Intent intent = new Intent(context, SearchActivity.class);
                                intent.putExtra("hashTag", s);
                                AdapterPostHome.this.context.startActivity(intent);
                            }
                            else if (i == 2){
                                String username = s.replaceFirst("@","");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                Query query = ref.orderByChild("username").equalTo(username.trim());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                String id1 = ds.child("id").getValue().toString();
                                                Intent intent = new Intent(context, UserProfile.class);
                                                intent.putExtra("hisUid", id1);
                                                context.startActivity(intent);
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
                            }
                            else if (i == 4){
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                context.startActivity(intent);
                            }
                            else if (i == 8){
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, s);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                context.startActivity(intent);

                            }
                        });
                    }else{
                        holder.pTextview.setVisibility(View.GONE);
                    }
                    holder.pMeme.setVisibility(View.VISIBLE);
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.pMeme);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            //Like
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProcessLike = true;
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike){
                                if (dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    likeRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    mProcessLike = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mProcessView = true;
                    viewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView){
                                if (!dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    viewRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                }
                                mProcessView = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            checkLike(holder,postId,userId);

            numberLike(holder,postId,userId);

            holder.pMeme.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mProcessLike = true;
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike){
                                if (dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    likeRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    mProcessLike = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mProcessView = true;
                    viewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView){
                                if (!dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    viewRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                }
                                mProcessView = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onDoubleClick(View view) {
                    if(!link.isEmpty()){
                        Intent intent = new Intent (Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY,link);
                        context.startActivity(intent);
                    }
                }
            }));

            holder.pTextview.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mProcessLike = true;
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike){
                                if (dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    likeRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                    mProcessLike = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mProcessView = true;
                    viewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView){
                                if (!dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    viewRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                }
                                mProcessView = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onDoubleClick(View view) {
                    if(!link.isEmpty()){
                        Intent intent = new Intent (Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY,link);
                        context.startActivity(intent);
                    }
                }
            }));

            holder.rejoylayout.setVisibility(View.GONE);

            holder.commentlayout.setVisibility(View.GONE);

            holder.more.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProcessView = true;
                    viewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView){
                                if (!dataSnapshot.child(postId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    viewRef.child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                }
                                mProcessView = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("SponsorViews").hasChild(postId)){
                        viewRef.child(postId).addValueEventListener(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (Integer.parseInt(pView) <=(int) snapshot.getChildrenCount()){
                                        FirebaseDatabase.getInstance().getReference().child("Sponsorship").child(postId).child("privacy").setValue("private");
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

    private void updateLike(String id,String postId,String image) {
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
                            addToHisNotification(id, "Liked on your post", postId,image);
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        sendNotification(id, Objects.requireNonNull(user).getName(), "Liked on your post",postId);
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

    //CheckReTweet
    private void checkReTweet(AdapterPostHome.MyHolder holder, String postId, String userId) {
        rejoyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)){
                    holder.rejoy.setImageResource(R.drawable.icon_loop2);
                }else {
                    holder.rejoy.setImageResource(R.drawable.icon_loop);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //CheckLike
    private void checkLike(AdapterPostHome.MyHolder holder, String postId, String userId) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)){
                    holder.like.setImageResource(R.drawable.icon_fav2);
                }else {
                    holder.like.setImageResource(R.drawable.icon_fav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //NumberReTweet
    private void numberReTweet(AdapterPostHome.MyHolder holder, String postId, String userId) {
        rejoyRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0){
                    holder.rejoyNo.setText("0");
                }else {
                    holder.rejoyNo.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //NumberLike
    private void numberLike(AdapterPostHome.MyHolder holder, String postId, String userId) {
        likeRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0){
                    holder.likeNo.setText("0");
                }else {
                    holder.likeNo.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView pCircleImageView;
        final RoundedImageView pMeme;
        final ImageView more;
        final ImageView rejoy;
        final ImageView comment;
        final ImageView like;
        final TextView rejoyNo;
        final ImageView like_img;
        final ImageView eye;
        final ImageView dot2;
        final ImageView dot3;
        final ImageView dot4;
        final ImageView verified;
        final TextView pName;

        final TextView username;
        final TextView time;
        final TextView location;
        final TextView updated;
        final SocialTextView pTextview;
        final TextView likeNo;
        final TextView commentNo;
        final TextView views;

        final FrameLayout mfl;

        final RelativeLayout commentlayout;
        final RelativeLayout likeLayout;

        final RelativeLayout view_ly;
        final ConstraintLayout lol;
        final RelativeLayout video_share;
        final RelativeLayout rejoylayout;
        public Context context;
        final ConstraintLayout mainlayout;
        final ImageView pause;
        final ProgressBar load;

        final ConstraintLayout constraintLayout1;
        final ConstraintLayout constraintLayout2;
        final ConstraintLayout constraintLayout3;
        final ConstraintLayout constraintLayout4;
        final ConstraintLayout viewlt;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            pCircleImageView = itemView.findViewById(R.id.circleImageView);
            eye = itemView.findViewById(R.id.eye);
            pMeme = itemView.findViewById(R.id.imageView);
            pName = itemView.findViewById(R.id.name);
            likeNo = itemView.findViewById(R.id.likeNo);
            commentNo = itemView.findViewById(R.id.commentNo);
            load = itemView.findViewById(R.id.load);
            views = itemView.findViewById(R.id.views);
            view_ly = itemView.findViewById(R.id.view_ly);
            more = itemView.findViewById(R.id.more);
//            pVine = itemView.findViewById(R.id.playerview);
            pause = itemView.findViewById(R.id.exomedia_controls_play_pause_btn);
            like_img = itemView.findViewById(R.id.like_img);
            pTextview = itemView.findViewById(R.id.textView);
            viewlt = itemView.findViewById(R.id.viewlt);
            commentlayout = itemView.findViewById(R.id.commentlayout);
            likeLayout = itemView.findViewById(R.id.likeLayout);

            video_share = itemView.findViewById(R.id.vine_share);

            constraintLayout1 = itemView.findViewById(R.id.constraintLayout1);
            constraintLayout2 = itemView.findViewById(R.id.constraintLayout2);
            constraintLayout3 = itemView.findViewById(R.id.constraintLayout3);
            constraintLayout4 = itemView.findViewById(R.id.constraintLayout4);
            mfl = itemView.findViewById(R.id.fl);
            lol = itemView.findViewById(R.id.lol);
            rejoylayout = itemView.findViewById(R.id.rejoylayout);
            verified = itemView.findViewById(R.id.verified);
            rejoy = itemView.findViewById(R.id.rejoy);
            comment = itemView.findViewById(R.id.comment);
            like = itemView.findViewById(R.id.like);

            rejoyNo = itemView.findViewById(R.id.rejoyNo);
            time = itemView.findViewById(R.id.time);
            updated = itemView.findViewById(R.id.updated);
            location = itemView.findViewById(R.id.location);
            username = itemView.findViewById(R.id.username);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
            dot4 = itemView.findViewById(R.id.dot4);
        }

    }

    private void addToHisNotification(String id, String message, String pId,String image){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", id);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", pId);
        hashMap.put("notification", message);
        hashMap.put("sImage", image);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("Notifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("Count").child(timestamp).setValue(true);

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
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Timber.d("onResponse" + response.toString()), error -> Timber.d("onResponse" + error.toString())){
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








