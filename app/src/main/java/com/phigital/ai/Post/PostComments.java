package com.phigital.ai.Post;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.phigital.ai.Adapter.AdapterPostComments;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelComments;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data2;
import com.phigital.ai.Notifications.Sender2;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityArticleCommentsBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PostComments extends BaseActivity {

    private RequestQueue requestQueue;
    Context context = PostComments.this;
    private boolean notify = false;
    SharedPref sharedPref;
    ActivityArticleCommentsBinding binding;
    String cId,replyusername,replyusername2,cId2,postId1,postId2;
    private String userId, myName, myDp,id;
    String hisId,hispId,hisText,hisViews,hisrViews,hisType,hisVideo,hisreTweet,hisreId,hisTime,hisPrivacy,hisLocation,hisContent,hisLink;
    List<ModelComments> commentsList;
    AdapterPostComments adapterComments;
    DatabaseReference commentRef;
    private static String postId,hisImage;
    public static String getmeme() {
        return hisImage;
    }
    public static String getPostId() {
        return postId;
    }
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_comments);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        postId = intent.getStringExtra("postId");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("comment"));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        commentRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        loadComments();
        loadPostInfo();
        loadUserInfo();
        setCommentno();
        binding.imageView10.setOnClickListener(v -> postreplyComment());
        binding.imageView3.setOnClickListener(v -> {
            LocalBroadcastManager.getInstance(PostComments.this).unregisterReceiver(mMessageReceiver);
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
                binding.textBox.setSelection(binding.textBox.getText().length());
            }
             binding.imageView10.setOnClickListener(v ->   postreplyComment());
             binding.imageView11.setOnClickListener(v ->   replycomment(postId1,cId));
             binding.imageView12.setOnClickListener(v ->   replycomment2(postId2,cId2));
             if (cId == null){
                binding.imageView10.setVisibility(View.VISIBLE);
                binding.imageView11.setVisibility(View.GONE);
                binding.imageView12.setVisibility(View.GONE);
            }
             if (cId != null){
                binding.imageView10.setVisibility(View.GONE);
                binding.imageView11.setVisibility(View.VISIBLE);
                binding.imageView12.setVisibility(View.GONE);
            }
             if (cId2 != null){
                binding.imageView10.setVisibility(View.GONE);
                binding.imageView11.setVisibility(View.GONE);
                binding.imageView12.setVisibility(View.VISIBLE);
            }
        }
    };

    private void replycomment(String postId, String cId) {
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
                            if(!id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(id, "Comment on your Feel", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(id, Objects.requireNonNull(user).getName(), "Comment on your feel",postId);
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
                                addToHisNotification(hisreTweet, "Comment on your Feel", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Comment on your feel",hisreId);
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
                            if(!id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(id, "Comment on your Feel", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(id, Objects.requireNonNull(user).getName(), "Comment on your feel",postId);
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
                                addToHisNotification(hisreTweet, "Comment on your Feel", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Comment on your feel",hisreId);
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
        }
        else {
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
                            if(!id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                addToHisNotification(id, "Comment on your Feel", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(id, Objects.requireNonNull(user).getName(), "Comment on your feel",postId);
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
                                addToHisNotification(hisreTweet, "Comment on your Feel", postId,hisImage);
                                notify = true;
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelUser user = snapshot.getValue(ModelUser.class);
                                        if (notify){
                                            sendNotification(hisreTweet, Objects.requireNonNull(user).getName(), "Comment on your feel",hisreId);
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

    private void setCommentno() {
        commentRef.child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                    binding.textView10.setText("0");
                } else {
                    binding.textView10.setText(String.valueOf(snapshot.getChildrenCount()));
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
                    adapterComments = new AdapterPostComments(PostComments.this, commentsList);
                    binding.recyclerView.setAdapter(adapterComments);
                    adapterComments.notifyDataSetChanged();
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
                    Data2 data = new Data2(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Comment", hisId, R.drawable.logo,postId);
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
        LocalBroadcastManager.getInstance(PostComments.this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}