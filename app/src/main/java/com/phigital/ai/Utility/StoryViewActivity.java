package com.phigital.ai.Utility;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelStory;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.Upload.AddStoryActivity;
import com.phigital.ai.databinding.ActivityStoryViewBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryViewActivity extends BaseActivity implements StoriesProgressView.StoriesListener,View.OnClickListener {

    int counter = 0;
    long pressTime = 0L;
    final long limit = 500L;

    ActivityStoryViewBinding binding;
    StoriesProgressView storiesProgressView;
    BottomSheetDialog bottomDialog;
    List<String> images;
    List<String> storyids;
    String userid;

    ConstraintLayout deleteCL,storyCl,shareCL,reportCL;
    private RequestQueue requestQueue;

    private boolean notify = false;

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener(){
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_story_view);
        userid = getIntent().getStringExtra("userid");

        getStories(userid);
        userInfo(userid);

        View reverse =  findViewById(R.id.reverse);
        View skip =  findViewById(R.id.skip);
        storiesProgressView =  findViewById(R.id.stories);

        binding.rSeen.setVisibility(View.VISIBLE);
        binding.message.setVisibility(View.GONE);

        binding.rSeen.setOnClickListener(v -> {
            Intent intent = new Intent(StoryViewActivity.this, ViewedActivity.class);
            intent.putExtra("id",userid);
            intent.putExtra("storyid",storyids.get(counter));
            startActivity(intent);
        });

//        binding.storyDelete.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
//            builder.setTitle("Delete");
//            builder.setMessage("Are you sure to delete this Story?");
//            builder.setPositiveButton("Delete", (dialog, which) -> {
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Story").child(userid);
//                ref.child(storyids.get(counter)).removeValue()
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(this, "Story deleted", Toast.LENGTH_SHORT).show();
//                        }).addOnFailureListener(e -> {
//                    Toast.makeText(this, "Story Failed to deleted", Toast.LENGTH_SHORT).show();
//                });
//            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//            builder.create().show();
//        });

        binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.show();
                storiesProgressView.pause();
            }
        });

        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

//        sendMessage = findViewById(R.id.sendMessage);
//        imageView2 = findViewById(R.id.imageView2);

        binding.imageView2.setOnClickListener(v -> {
            String msg = binding.sendMessage.getText().toString();
            if (msg.isEmpty()){
                Toast.makeText(getApplicationContext(), "Type something", Toast.LENGTH_SHORT).show();
            }else {
                notify = true;
                String timeStamp = ""+ System.currentTimeMillis();
//                Uri uri =Uri.parse(images.get(counter));
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                hashMap.put("receiver", userid);
                hashMap.put("msg", msg);
                hashMap.put("isSeen", false);
                hashMap.put("timeStamp", timeStamp);
                hashMap.put("type", "story");
                hashMap.put("pId", timeStamp);
                hashMap.put("hide", "false");
                hashMap.put("seen", "false");
                databaseReference1.child("Chats").child(timeStamp).setValue(hashMap);

                final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userid);
                chatRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatRef1.child("id").setValue(userid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                });

                final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(userid)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                chatRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatRef2.child("id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Category can't be changed", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(userid, Objects.requireNonNull(user).getName(), "Sent a message");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        binding.sendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                storiesProgressView.pause();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        createBottomDialog();

    }

    private void createBottomDialog(){
        if ( bottomDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.story_bottom_sheet, null);
            storyCl = view.findViewById(R.id.storyCl);
            shareCL = view.findViewById(R.id.shareCL);
            deleteCL = view.findViewById(R.id.deleteCL);
            reportCL = view.findViewById(R.id.reportCL);

            reportCL.setOnClickListener(this);
            storyCl.setOnClickListener(this);
            shareCL.setOnClickListener(this);
            deleteCL.setOnClickListener(this);
            if (userid.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
               deleteCL.setVisibility(View.VISIBLE);
               reportCL.setVisibility(View.GONE);
            }
            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);
        }

        bottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                storiesProgressView.resume();
            }
        });
    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++counter)).into(binding.image);
        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(binding.image);
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onComplete() {
       finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid){
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Story").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelStory modelStory = snapshot1.getValue(ModelStory.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                        images.add(modelStory.imageUri);
                        storyids.add(modelStory.storyid);
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryViewActivity.this);
//                if (counter == 0){
//                    Intent intent = new Intent(StoryViewActivity.this, AboutMe.class);
//                    startActivity(intent);
//                    finish();
//                }else{
                storiesProgressView.startStories(counter);
                Glide.with(getApplicationContext()).load(images.get(counter)).into(binding.image);
                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenNumber(String storyid) {
        FirebaseDatabase.getInstance().getReference("Story").child(userid).child(storyid).child("views").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.seenNumber.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                if (Objects.requireNonNull(modelUser).getPhoto().isEmpty()){
                    Glide.with(getApplicationContext()).load(R.drawable.placeholder).into(binding.pic);
                }else {
                    Glide.with(getApplicationContext()).load(Objects.requireNonNull(modelUser).getPhoto()).into(binding.pic);
                }

               binding.username.setText(modelUser.getUsername());
                binding.username.setOnClickListener(v -> {
                    Intent intent = new Intent(StoryViewActivity.this, UserProfile.class);
                    intent.putExtra("hisUid", modelUser.getId());
                    startActivity(intent);
                });
              binding. pic.setOnClickListener(v -> {
                    Intent intent = new Intent(StoryViewActivity.this, UserProfile.class);
                    intent.putExtra("hisUid", modelUser.getId());
                    startActivity(intent);
                });

                if (modelUser.getVerified().isEmpty()){
                  binding.verify.setVisibility(View.GONE);
                }else {
                    binding.verify.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addView(String storyid){
        FirebaseDatabase.getInstance().getReference("Story").child(userid).child(storyid).child("views").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " : " + message, "New Message", hisId,"", R.drawable.logo);
                    Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Log.d("JSON_RESPONSE", "onResponse" + response.toString()), error -> Log.d("JSON_RESPONSE", "onResponse" + error.toString())){
                            @SuppressWarnings("RedundantThrows")
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
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
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.storyCl:
                bottomDialog.cancel();
                Intent intent = new Intent(StoryViewActivity.this, AddStoryActivity.class);
                startActivity(intent);
                storiesProgressView.resume();
                break;
            case R.id.shareCL:
                bottomDialog.cancel();

                binding.image.invalidate();;
                BitmapDrawable drawable =(BitmapDrawable)binding.image.getDrawable();
                Bitmap picBitmap =drawable.getBitmap();

                //setup intent:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                //setup image extra, if exists:
                if (picBitmap != null) {
                    String url = MediaStore.Images.Media.insertImage(StoryViewActivity.this.getContentResolver(), picBitmap, "", "");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                    sharingIntent.setType("*/*");
                } else {
                    //if no picture, just text set - this MIME
                    sharingIntent.setType("text/plain");
                }
                if (sharingIntent.resolveActivity(StoryViewActivity.this.getPackageManager()) != null) {
                    startActivity(Intent.createChooser(sharingIntent,"Share"));
                } else {
                    Toast.makeText(StoryViewActivity.this, "Sharing failed please try again later", Toast.LENGTH_SHORT).show();
                }

                storiesProgressView.resume();
                break;
            case R.id.deleteCL:
                bottomDialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this Story?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Story").child(userid);
                    ref.child(storyids.get(counter)).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Story deleted", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
                storiesProgressView.resume();
                break;
            case R.id.reportCL:
                bottomDialog.cancel();
                Toast.makeText(this, "Story Reported", Toast.LENGTH_SHORT).show();
                storiesProgressView.resume();
                break;
        }
    }
}