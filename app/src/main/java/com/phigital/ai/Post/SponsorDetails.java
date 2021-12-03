package com.phigital.ai.Post;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Adapter.AdapterPostComments;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelComments;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data2;
import com.phigital.ai.Notifications.Sender2;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.PostBottomSheet;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.Utility.UserProfile;
import com.phigital.ai.databinding.ActivityPostDetailsBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SponsorDetails extends BaseActivity {

    private RequestQueue requestQueue;
    Context context = SponsorDetails.this;
    SharedPref sharedPref;
    DatabaseReference rejoyRef;
    boolean mProcessRejoy = false;
    private boolean notify = false;

    String cId,replyusername,replyusername2,cId2,postId1,postId2;
    private String userId, myName, myDp;


    String hisId,hispId,hisText,hisViews,hisrViews,hisType,hisVideo,hisreTweet,hisreId,hisTime,hisPrivacy,hisLocation,hisContent,hisLink;
    boolean mProcessView = false;
    private DatabaseReference viewRef;
    boolean mProcessCLike = false;

    List<ModelComments> commentsList;

    AdapterPostComments adapterPostComments;

    ActivityPostDetailsBinding binding;

    private String postId,hisImage;

    public  String getmeme() {
        return hisImage;
    }

    public String getPostId() {
        return postId;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_details);
        rejoyRef = FirebaseDatabase.getInstance().getReference().child("ReTweet");

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        loadUserInfo();
        setLikes();
        setLikenumber();
        binding.relativeLayout.setOnClickListener(v -> likePost());

        viewRef = FirebaseDatabase.getInstance().getReference().child("SponsorViews");
        loadPostInfo2();
        binding.relativeLayout2.setVisibility(View.GONE);
        binding.relativeLayout6.setVisibility(View.GONE);
        binding.postcomment.setVisibility(View.GONE);
        binding.textBox.setText("Sponsored");
        binding.textBox.setTextColor(getResources().getColor(R.color.commenthint));
        binding.textBox.setEnabled(false);
        mProcessView = true;
        viewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessView){
                    if (!dataSnapshot.child(postId).hasChild(userId)) {
                        viewRef.child(postId).child(userId).setValue(true);
                    }
                    mProcessView = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "comment1"
        ));


        binding.recyclerView.smoothScrollToPosition(0);

        binding.recyclerView.setFocusable(false);

        binding.imageView3.setOnClickListener(v -> {
            LocalBroadcastManager.getInstance(SponsorDetails.this).unregisterReceiver(mMessageReceiver);
            onBackPressed();
        });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
             replyusername = intent.getStringExtra("fromComment");
             postId1 = intent.getStringExtra("postId");
             cId = intent.getStringExtra("fromCid");
             replyusername2 = intent.getStringExtra("fromComment2");
             postId2 = intent.getStringExtra("postId2");
             cId2 = intent.getStringExtra("fromCid2");
             binding.textBox.setText( "@"+replyusername);
             if (replyusername2 != null) {
                binding.textBox.setText("@"+replyusername2);
            }
             binding.postcomment1.setOnClickListener(v ->   replycomment(postId1,cId));
             binding.postcomment2.setOnClickListener(v ->   replycomment2(postId2,cId2));
             if (cId == null){
                binding.postcomment.setVisibility(View.VISIBLE);
                binding.postcomment1.setVisibility(View.GONE);
                binding.postcomment2.setVisibility(View.GONE);
            }
             if (cId!= null){
                binding.postcomment.setVisibility(View.GONE);
                binding.postcomment1.setVisibility(View.VISIBLE);
                binding.postcomment2.setVisibility(View.GONE);
            }
             if (cId2!= null){
                binding.postcomment.setVisibility(View.GONE);
                binding.postcomment1.setVisibility(View.GONE);
                binding.postcomment2.setVisibility(View.VISIBLE);
            }
        }
    };

    private void replycomment( String postId, String cId) {
        String comment = binding.textBox.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(context, "Please comment something..", Toast.LENGTH_SHORT).show();
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").child(cId).child("replies");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("maincId", cId);
            hashMap.put("comment", comment);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("id", userId);
            hashMap.put("pLikes", "0");
            hashMap.put("type", "text");
            hashMap.put("pId", postId);
            hashMap.put("dp", myDp);
            hashMap.put("mane", myName);
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        binding.textBox.setText("");
                        if(hisreTweet.isEmpty()){
                            if(!hisId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(hisId, "Comment on your post", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Comment on your post",postId);
                                        }
                                        notify = false;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }else{
                            if(!hisreTweet.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(hisreTweet, "Comment on your post", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Comment on your post",hisreId);
                                        }
                                        notify = false;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                        makenull();
                    }).addOnFailureListener(e -> {

            });
        }
    }

    private void replycomment2( String postId2, String cId2) {
        String comment = binding.textBox.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(context, "Please comment something..", Toast.LENGTH_SHORT).show();
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId2).child("Comments").child(cId2).child("replies");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("maincId", cId2);
            hashMap.put("comment", comment);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("id", userId);
            hashMap.put("pLikes", "0");
            hashMap.put("type", "text");
            hashMap.put("pId", postId2);
            hashMap.put("dp", myDp);
            hashMap.put("mane", myName);
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        binding.textBox.setText("");
                        if(hisreTweet.isEmpty()){
                            if(!hisId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(hisId, "Comment on your post", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Comment on your post",postId);
                                        }
                                        notify = false;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }else{
                            if(!hisreTweet.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(hisreTweet, "Comment on your post", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Comment on your post",hisreId);
                                        }
                                        notify = false;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                        makenull();
                    }).addOnFailureListener(e -> {

            });
        }
    }

    private void postreplyComment() {
        String comment = binding.textBox.getText().toString().trim();
        if (TextUtils.isEmpty(comment)){
            Toast.makeText(context, "Please comment something..", Toast.LENGTH_SHORT).show();
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("comment", comment);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("id", userId);
            hashMap.put("pLikes", "0");
            hashMap.put("type", "text");
            hashMap.put("pId", postId);
            hashMap.put("dp", myDp);
            hashMap.put("mane", myName);
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        binding.textBox.setText("");
                        if(hisreTweet.isEmpty()){
                            if(!hisId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(hisId, "Comment on your post", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Comment on your post",postId);
                                        }
                                        notify = false;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }else{
                            if(!hisreTweet.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(hisreTweet, "Comment on your post", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Comment on your post",hisreId);
                                        }
                                        notify = false;
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                        makenull();
                    }).addOnFailureListener(e -> {

            });
        }
    }

    private void makenull() {
        cId = null;
        cId2 = null;
        replyusername = null;
        replyusername2 = null;
    }

    private void setRejoy() {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("ReTweet").hasChild(postId)){
                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userId)){
                                binding.rejoyImg.setImageResource(R.drawable.icon_loop2);
                            }else {
                                binding.rejoyImg.setImageResource(R.drawable.icon_loop);
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

    private void setRejoynumber() {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    binding.rejoyNo.setText("0");
                                }else {
                                    binding.rejoyNo.setText(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCommentnumber() {
        FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0){
                    binding.commentNo.setText("0");
                }else {
                    binding.commentNo.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        commentsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComments modelComments = ds.getValue(ModelComments.class);
                    commentsList.add(modelComments);
                    adapterPostComments = new AdapterPostComments(getApplicationContext(), commentsList);
                    binding.recyclerView.setAdapter(adapterPostComments);
                    adapterPostComments.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikenumber() {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                   binding.likeNo.setText("0");
                } else {
                  binding.likeNo.setText(String.valueOf(snapshot.getChildrenCount()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes() {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)){
                    binding.likeImg.setImageResource(R.drawable.icon_fav2);
                }else {
                    binding.likeImg.setImageResource(R.drawable.icon_fav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        mProcessCLike = true;
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessCLike) {
                    if (dataSnapshot.child(postId).hasChild(userId)) {
                        likeRef.child(postId).child(userId).removeValue();
                        mProcessCLike = false;
                    } else {
                        likeRef.child(postId).child(userId).setValue("Liked");
                        mProcessCLike = false;
                        if(!hisId.equals(userId)){
                            addToHisNotification(hisId, "Liked on your Sponsor", postId,hisImage);
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        sendNotification(hisId, Objects.requireNonNull(user).getName(), "Liked on your Sponsor",postId);
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

    private void loadUserInfo() {
        Query query = FirebaseDatabase.getInstance().getReference("Users");
        query.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    myName = ""+ds.child("name").getValue();
                    myDp = ""+ds.child("photo").getValue();
                    try {
                        Picasso.get().load(myDp).into(binding.dp);
                    } catch (Exception e ){
                        Picasso.get().load(R.drawable.placeholder).into(binding.dp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    hisId = ""+ds.child("id").getValue();
                    hispId = ""+ds.child("pId").getValue();
                    hisText = ""+ds.child("text").getValue();
                    hisViews = ""+ds.child("pViews").getValue();
                    hisrViews = ""+ds.child("rViews").getValue();
                    hisType = ""+ds.child("type").getValue();
                    hisImage = ""+ds.child("image").getValue();
                    hisVideo = ""+ds.child("video").getValue();
                    hisreTweet = ""+ds.child("reTweet").getValue();
                    hisreId = ""+ds.child("reId").getValue();
                    hisTime = ""+ds.child("pTime").getValue();
                    hisPrivacy = ""+ds.child("privacy").getValue();
                    hisLocation = ""+ds.child("location").getValue();
                    hisContent = ""+ds.child("content").getValue();
                    hisLink = ""+ds.child("link").getValue();

                    switch (hisType){
                        case "text": {
                            binding.textView.setLinkText(hisText);
                            binding.textView.setVisibility(View.VISIBLE);
                            binding.textView.setOnLinkClickListener((i, s) -> {
                                if (i == 1) {
                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra("hashTag", s);
                                    context.startActivity(intent);
                                }
                                else if (i == 2){
                                    String username = s.replaceFirst("@","");
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
                                    Query query1 = ref1.orderByChild("username").equalTo(username.trim());
                                    query1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot ds1 : snapshot.getChildren()){
                                                    String id = ds1.child("id").getValue().toString();
                                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                        Intent intent = new Intent(context, UserProfile.class);
                                                        intent.putExtra("hisUid", id);
                                                        context.startActivity(intent);
                                                    }else {
                                                        Intent intent = new Intent(context, UserProfile.class);
                                                        intent.putExtra("hisUid", id);
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
                            if (!hisText.isEmpty()){
                                binding.textView.setLinkText(hisText);
                                binding.textView.setVisibility(View.VISIBLE);
                                binding.textView.setOnLinkClickListener((i, s) -> {
                                    if (i == 1) {
                                        Intent intent = new Intent(context, SearchActivity.class);
                                        intent.putExtra("hashTag", s);
                                        context.startActivity(intent);
                                    }
                                    else if (i == 2){
                                        String username = s.replaceFirst("@","");
                                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
                                        Query query1 = ref1.orderByChild("username").equalTo(username.trim());
                                        query1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot ds1 : snapshot.getChildren()){
                                                        String id = ds1.child("id").getValue().toString();
                                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                            Intent intent = new Intent(context, UserProfile.class);
                                                            intent.putExtra("hisUid", id);
                                                            context.startActivity(intent);
                                                        }else {
                                                            Intent intent = new Intent(context, UserProfile.class);
                                                            intent.putExtra("hisUid", id);
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
                                binding.textView.setVisibility(View.GONE);
                            }
                            binding.imageView.setVisibility(View.VISIBLE);
                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.imageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case"meme":{
                            binding.textView.setVisibility(View.VISIBLE);
                            binding.textView.setLinkText(hisText);
                            binding.imageView.setVisibility(View.VISIBLE);
                            binding.textView.setOnLinkClickListener((i, s) -> {
                                if (i == 1) {
                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra("hashTag", s);
                                    context.startActivity(intent);
                                }else if (i == 2){
                                    String username = s.replaceFirst("@","");
                                    DatabaseReference ref12 = FirebaseDatabase.getInstance().getReference("Users");
                                    Query query12 = ref12.orderByChild("username").equalTo(username.trim());
                                    query12.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot ds12 : snapshot.getChildren()){
                                                    String id = ds12.child("id").getValue().toString();
                                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                        Intent intent = new Intent(context, UserProfile.class);
                                                        intent.putExtra("hisUid", id);
                                                        context.startActivity(intent);
                                                    }else {
                                                        Intent intent = new Intent(context, UserProfile.class);
                                                        intent.putExtra("hisUid", id);
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
                                }else if (i == 16){
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
                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.imageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    long lastTime = Long.parseLong(hisTime);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
                    binding.time.setText(lastSeenTime);

                    if (hisId != null){
                        //User details
                        FirebaseDatabase.getInstance().getReference().child("Users").child(hisId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userId = snapshot.child("id").getValue().toString();
                                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                                String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                                String mVerified = snapshot.child("verified").getValue().toString();

                                if (mVerified.isEmpty()){
                                    binding.verified.setVisibility(View.GONE);
                                }else {
                                    binding.verified.setVisibility(View.VISIBLE);
                                }

                                binding.name.setText(name);
                                binding.username.setVisibility(View.VISIBLE);
                                binding.username.setText(username);
                                binding.dot2.setVisibility(View.VISIBLE);

                                if (!dp.isEmpty()) {
                                    Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(binding.circleImageView3);
                                }

                                //ClickToPro
                                binding.name.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", userId);
                                            context.startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", userId);
                                            context.startActivity(intent);
                                        }
                                    }
                                });

                                binding.circleImageView3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", userId);
                                            context.startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", userId);
                                            context.startActivity(intent);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }


                    if (hisLocation.isEmpty()){
                        binding.dot3.setVisibility(View.GONE);
                    }else{
                        binding.location.setVisibility(View.VISIBLE);
                        binding.location.setText(hisLocation);
                        binding.dot3.setVisibility(View.VISIBLE);
                    }

                    if (hisreTweet.isEmpty()){
                        binding.rejoy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mProcessRejoy = true;
                                rejoyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (mProcessRejoy){
                                            if (snapshot.child(postId).hasChild(userId)) {
                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop);
                                                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(hispId);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            String userId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                                            if (userId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                                ds.getRef().removeValue();
                                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hispId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                                                Toast.makeText(context, "Repost Removed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            } else {
                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop2);
                                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                                HashMap<Object, String> hashMap = new HashMap<>();
                                                hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                                hashMap.put("pId", timeStamp);
                                                hashMap.put("text", hisText);
                                                hashMap.put("pViews", hisViews);
                                                hashMap.put("rViews", hisrViews);
                                                hashMap.put("type", hisType);
                                                hashMap.put("video", hisVideo);
                                                hashMap.put("image", hisImage);
                                                hashMap.put("reTweet", hisId);
                                                hashMap.put("content", hisContent);
                                                hashMap.put("reId", hispId);
                                                hashMap.put("privacy", ""+hisPrivacy);
                                                hashMap.put("pTime", hisTime);
                                                hashMap.put("location", hisLocation);
                                                hashMap.put("link", hisLink);

                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                                dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NotNull Void aVoid) {
                                                        if(!hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                            addToHisNotification(hisId, "Repost on your post", postId,hisImage);
                                                            notify = true;
                                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                                    if (notify){
                                                                        sendNotification(hisId, Objects.requireNonNull(user).getName(), "Repost on your post",postId);
                                                                    }
                                                                    notify = false;
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                }
                                                            });
                                                        }
                                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hispId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                        Toast.makeText(context, "Repost", Toast.LENGTH_SHORT).show();
                                                    }
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
                    }else{
                        binding.rejoy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mProcessRejoy = true;
                                rejoyRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (mProcessRejoy) {
                                            if (dataSnapshot.child(hisreId).hasChild(userId)) {
                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop);
                                                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(hisreId);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            String userId = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                                            if (userId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                                ds.getRef().removeValue();
                                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hisreId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                                                Toast.makeText(context, "Repost Removed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            } else {
                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop2);
                                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                                HashMap<Object, String> hashMap = new HashMap<>();
                                                hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                                hashMap.put("pId", timeStamp);
                                                hashMap.put("text", hisText);
                                                hashMap.put("pViews", hisViews);
                                                hashMap.put("rViews", hisrViews);
                                                hashMap.put("type", hisType);
                                                hashMap.put("video", hisVideo);
                                                hashMap.put("image", hisImage);
                                                hashMap.put("reTweet", hisreTweet);
                                                hashMap.put("content", hisContent);
                                                hashMap.put("reId", hisreId);
                                                hashMap.put("privacy", ""+hisPrivacy);
                                                hashMap.put("pTime", hisTime);
                                                hashMap.put("location", hisLocation);
                                                hashMap.put("link", hisLink);

                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                                dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NotNull Void aVoid) {
                                                        if(!hisreTweet.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                            addToHisNotification(hisreTweet, "Repost on your post", postId,hisImage);
                                                            notify = true;
                                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                                    if (notify){
                                                                        sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Repost on your post",hisreId);
                                                                    }
                                                                    notify = false;
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                }
                                                            });
                                                        }
                                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hispId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

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
                    }

                    binding.more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (hisreTweet.isEmpty()){
                                if (hisType.equals("image")){
                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.imageView.getDrawable();
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    Bundle args = new Bundle();
                                    args.putString("id", hisId);
                                    args.putString("postId", hispId);
                                    args.putByteArray("byteArray",byteArray);
                                    PostBottomSheet bottomSheet = new PostBottomSheet();
                                    bottomSheet.setArguments(args);
                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                                }else{
                                    Bundle args = new Bundle();
                                    args.putString("id", hisId);
                                    args.putString("postId", hispId);
                                    PostBottomSheet bottomSheet = new PostBottomSheet();
                                    bottomSheet.setArguments(args);
                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                                }
                            }else{
                                if (hisType.equals("image")){
                                    BitmapDrawable bitmapDrawable = (BitmapDrawable)binding.imageView.getDrawable();
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    Bundle args = new Bundle();
                                    args.putString("id", hisId);
                                    args.putString("postId", hisreId);
                                    args.putByteArray("byteArray",byteArray);
                                    PostBottomSheet bottomSheet = new PostBottomSheet();
                                    bottomSheet.setArguments(args);
                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                                }else{
                                    Bundle args = new Bundle();
                                    args.putString("id", hisId);
                                    args.putString("postId", hisreId);
                                    PostBottomSheet bottomSheet = new PostBottomSheet();
                                    bottomSheet.setArguments(args);
                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                                }
                            }
                        }
                    });

                    FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                onBackPressed();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo2() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sponsorship");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    hisId = ""+ds.child("id").getValue();
                    hispId = ""+ds.child("pId").getValue();
                    hisText = ""+ds.child("text").getValue();
                    hisViews = ""+ds.child("pViews").getValue();
                    hisrViews = ""+ds.child("rViews").getValue();
                    hisType = ""+ds.child("type").getValue();
                    hisImage = ""+ds.child("image").getValue();
                    hisVideo = ""+ds.child("video").getValue();
                    hisreTweet = ""+ds.child("reTweet").getValue();
                    hisreId = ""+ds.child("reId").getValue();
                    hisTime = ""+ds.child("pTime").getValue();
                    hisPrivacy = ""+ds.child("privacy").getValue();
                    hisLocation = ""+ds.child("location").getValue();
                    hisContent = ""+ds.child("content").getValue();
                    hisLink = ""+ds.child("link").getValue();

                    switch (hisType){
                        case "text": {
                            binding.textView.setLinkText(hisText);
                            binding.textView.setVisibility(View.VISIBLE);
                            binding.textView.setOnLinkClickListener((i, s) -> {
                                if (i == 1) {
                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra("hashTag", s);
                                    context.startActivity(intent);
                                }
                                else if (i == 2){
                                    String username = s.replaceFirst("@","");
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
                                    Query query1 = ref1.orderByChild("username").equalTo(username.trim());
                                    query1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                String id1 = null;
                                                for (DataSnapshot ds : snapshot.getChildren()){
                                                    id1 = ds.child("id").getValue().toString();
                                                }
                                                if (id1 != null){
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
                            break;
                        }
                        case"image": {
                            if (!hisText.isEmpty()){
                                binding.textView.setLinkText(hisText);
                                binding.textView.setVisibility(View.VISIBLE);
                                binding.textView.setOnLinkClickListener((i, s) -> {
                                    if (i == 1) {
                                        Intent intent = new Intent(context, SearchActivity.class);
                                        intent.putExtra("hashTag", s);
                                        context.startActivity(intent);
                                    }
                                    else if (i == 2){
                                        String username = s.replaceFirst("@","");
                                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
                                        Query query1 = ref1.orderByChild("username").equalTo(username.trim());
                                        query1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    String id1 = null;
                                                    for (DataSnapshot ds : snapshot.getChildren()){
                                                        id1 = ds.child("id").getValue().toString();
                                                    }
                                                    if (id1 != null){
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
                                binding.textView.setVisibility(View.GONE);
                            }
                            binding.imageView.setVisibility(View.VISIBLE);
                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.imageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    long lastTime = Long.parseLong(hisTime);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
                    binding.time.setText(lastSeenTime);

                    //User details
                    FirebaseDatabase.getInstance().getReference().child("Users").child(hisId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userId = snapshot.child("id").getValue().toString();
                            String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                            String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                            String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                            String mVerified = snapshot.child("verified").getValue().toString();

                            if (mVerified.isEmpty()){
                                binding.verified.setVisibility(View.GONE);
                            }else {
                                binding.verified.setVisibility(View.VISIBLE);
                            }

                            binding.name.setText(name);
                            binding.username.setVisibility(View.VISIBLE);
                            binding.username.setText(username);
                            binding.dot2.setVisibility(View.VISIBLE);

                            if (!dp.isEmpty()) {
                                Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(binding.circleImageView3);
                            }

                            //ClickToPro
                            binding.name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });

                            binding.circleImageView3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    if (hisLocation.isEmpty()){
                        binding.dot3.setVisibility(View.GONE);
                    }else{
                        binding.location.setVisibility(View.VISIBLE);
                        binding.location.setText(hisLocation);
                        binding.dot3.setVisibility(View.VISIBLE);
                    }

                    binding.more.setVisibility(View.GONE);
//                    if (hisreTweet.isEmpty()){
//                        binding.rejoy.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mProcessRejoy = true;
//                                rejoyRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (mProcessRejoy){
//                                            if (snapshot.child(postId).hasChild(userId)) {
//                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop);
//                                                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(hispId);
//                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                                                            String userId = Objects.requireNonNull(ds.child("id").getValue()).toString();
//                                                            if (userId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
//                                                                ds.getRef().removeValue();
//                                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hispId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
//                                                                Toast.makeText(context, "Rejoyed Removed", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                                mProcessRejoy = false;
//                                            } else {
//                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop2);
//                                                String timeStamp = String.valueOf(System.currentTimeMillis());
//                                                HashMap<Object, String> hashMap = new HashMap<>();
//                                                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                                hashMap.put("pId", timeStamp);
//                                                hashMap.put("text", hisText);
//                                                hashMap.put("pViews", hisViews);
//                                                hashMap.put("rViews", hisrViews);
//                                                hashMap.put("type", hisType);
//                                                hashMap.put("video", hisVideo);
//                                                hashMap.put("image", hisImage);
//                                                hashMap.put("reTweet", hisId);
//                                                hashMap.put("content", hisContent);
//                                                hashMap.put("reId", hispId);
//                                                hashMap.put("privacy", ""+hisPrivacy);
//                                                hashMap.put("pTime", hisTime);
//                                                hashMap.put("location", hisLocation);
//                                                hashMap.put("link", hisLink);
//
//                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
//                                                dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
////                                                      addToHisNotification("Rejoyed your post",id);
//                                                        notify = true;
//                                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                                        dataRef.addValueEventListener(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                                ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                                                if (notify){
////                                                                    sendNotification(id, user.getName(), name + " Rejoyed your post");
//                                                                    if (hisType.equals("text")){
//                                                                        addToHisNotification2(""+hisId,""+postId,"Rejoyed your post");
//                                                                    }else{
//                                                                        addToHisNotification(""+hisId,""+postId,"Rejoyed your Post",hisImage);
//                                                                    }
//                                                                }
//                                                                notify = false;
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//
//                                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hispId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//
//                                                        Toast.makeText(context, "Rejoyed", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                                mProcessRejoy = false;
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                            }
//                        });
//                    }else{
//                        binding.rejoy.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mProcessRejoy = true;
//                                rejoyRef.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if (mProcessRejoy) {
//                                            if (dataSnapshot.child(hisreId).hasChild(userId)) {
//                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop);
//                                                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(hisreId);
//                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                                                            String userId = ds.child("id").getValue().toString();
//                                                            if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                                                ds.getRef().removeValue();
//                                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hisreId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
//                                                                Toast.makeText(context, "ReTweet Removed", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                                mProcessRejoy = false;
//                                            } else {
//                                                binding.rejoyImg.setImageResource(R.drawable.icon_loop2);
//                                                String timeStamp = String.valueOf(System.currentTimeMillis());
//                                                HashMap<Object, String> hashMap = new HashMap<>();
//                                                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                                hashMap.put("pId", timeStamp);
//                                                hashMap.put("text", hisText);
//                                                hashMap.put("pViews", hisViews);
//                                                hashMap.put("rViews", hisrViews);
//                                                hashMap.put("type", hisType);
//                                                hashMap.put("video", hisVideo);
//                                                hashMap.put("image", hisImage);
//                                                hashMap.put("reTweet", hisreTweet);
//                                                hashMap.put("content", hisContent);
//                                                hashMap.put("reId", hisreId);
//                                                hashMap.put("privacy", ""+hisPrivacy);
//                                                hashMap.put("pTime", hisTime);
//                                                hashMap.put("location", hisLocation);
//                                                hashMap.put("link", hisLink);
//
//                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
//                                                dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        notify = true;
//                                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                                                        dataRef.addValueEventListener(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                                ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                                                if (notify){
////                                                                    sendNotification(postList.get(position).getRejoy(), user.getName(), name + " Retweeted your post");
//                                                                    if (hisType.equals("text")){
//                                                                        addToHisNotification2(""+hisId,""+postId,"Rejoyed your post");
//                                                                    }else{
//                                                                        addToHisNotification(""+hisId,""+postId,"Rejoyed your Post",hisImage);
//                                                                    }
//                                                                }
//                                                                notify = false;
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//
//                                                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(hisreId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//                                                        Toast.makeText(context, "ReTweeted", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                                mProcessRejoy = false;
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        });
//                    }

//                    binding.more.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (hisreTweet.isEmpty()){
//                                if (hisType.equals("image")){
//                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.imageView.getDrawable();
//                                    Bitmap bitmap = bitmapDrawable.getBitmap();
//                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                                    byte[] byteArray = stream.toByteArray();
//                                    Bundle args = new Bundle();
//                                    args.putString("id", hisId);
//                                    args.putString("postId", hispId);
//                                    args.putByteArray("byteArray",byteArray);
//                                    PostBottomSheet bottomSheet = new PostBottomSheet();
//                                    bottomSheet.setArguments(args);
//                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//                                }else{
//                                    Bundle args = new Bundle();
//                                    args.putString("id", hisId);
//                                    args.putString("postId", hispId);
//                                    PostBottomSheet bottomSheet = new PostBottomSheet();
//                                    bottomSheet.setArguments(args);
//                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//                                }
//                            }else{
//                                if (hisType.equals("image")){
//                                    BitmapDrawable bitmapDrawable = (BitmapDrawable)binding.imageView.getDrawable();
//                                    Bitmap bitmap = bitmapDrawable.getBitmap();
//                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                                    byte[] byteArray = stream.toByteArray();
//                                    Bundle args = new Bundle();
//                                    args.putString("id", hisId);
//                                    args.putString("postId", hisreId);
//                                    args.putByteArray("byteArray",byteArray);
//                                    PostBottomSheet bottomSheet = new PostBottomSheet();
//                                    bottomSheet.setArguments(args);
//                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//                                }else{
//                                    Bundle args = new Bundle();
//                                    args.putString("id", hisId);
//                                    args.putString("postId", hisreId);
//                                    PostBottomSheet bottomSheet = new PostBottomSheet();
//                                    bottomSheet.setArguments(args);
//                                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//                                }
//                            }
//                        }
//                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(SponsorDetails.this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}