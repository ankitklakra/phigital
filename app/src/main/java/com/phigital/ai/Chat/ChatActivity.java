package com.phigital.ai.Chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.phigital.ai.Adapter.AdapterChat;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelChat;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.Utility.UserProfile;
import com.phigital.ai.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    ActivityChatBinding binding;

    //String
    String hisId,mName;
    boolean isShown = false;
    public static final String fileName = "recorded.3gp";
    final String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;

    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrenPage = 1;

    //Bottom
    BottomSheetDialog post_more,bottomDialog,bottomSheetDialog;
    LinearLayout image,video,audio,watch_party,camera,document,location,recorder,meeting,stickers;
    TextView blocked;
    //Permission
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int VIDEO_PICK_CODE = 1002;
    private static final int AUDIO_PICK_CODE = 1003;
    private static final int DOC_PICK_CODE = 1004;
    private static final int PERMISSION_CODE = 1001;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PERMISSION_REQ_CODE = 1 << 3;
    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //ID
    private AdapterChat adapterChat ;
    private ArrayList<ModelChat> nChat ;

    private RequestQueue requestQueue;
    private boolean notify = false;
    SharedPref sharedPref;
    String userId;
//    Context context = ChatActivity.this;
//    private static final int PICK_VIDEO_REQUEST = 1;
//    private static final int IMAGE_PICK_CODE = 1000;
//    private static final int PERMISSION_CODE = 1001;
//    private boolean notify = false;
    boolean isBlocked = false;
//
//    TextView blocked;
//    String mchatid,msgtimestamp,pId,msg,type, myUid,hisUid;
//    ImageView imageView21;
//
//    ConstraintLayout block,info,clearchat,attachphoto,attachvideo;
//    ConstraintLayout star,copy,delete,forward;
//    BottomSheetDialog bottomDialog,chatbottomsheet,bottomSheetDialog;
//
//    AdapterChat adapterChat;
//    List<ModelChat> nChat;
//

//    ModelUser user;
//
//    private Uri image_uri, video_uri;
//    ValueEventListener valueEventListener;
//    DatabaseReference chatRef,userRef,hisRef,databaseReference,likeRef;
//
//    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        requestQueue = Volley.newRequestQueue(ChatActivity.this);

        //GetUSERID
        hisId = getIntent().getStringExtra("hisUid");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        //Back
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("type")){
                    Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    onBackPressed();
                }
            }
        });

        if (isShown){
            check();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = ref.orderByChild("id").equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String photo = ""+ds.child("photo").getValue();
                    String name = ""+ds.child("name").getValue();
                    String status = ""+ds.child("status").getValue();

                    if (status.equals("online")) {
                        binding.time.setText("Online");
                    }else {
                        long lastTime = Long.parseLong(status);
                        binding.time.setText(GetTimeAgo.getTimeAgo(lastTime));
                    }

                    binding.name.setText(name);

                    Picasso.get().load(photo).into(binding.circleImageView3);

                    binding.circleImageView3.setOnClickListener(v -> {
                        Intent intent = new Intent(ChatActivity.this, UserProfile.class);
                        intent.putExtra("hisUid", hisId);
                        startActivity(intent);
                    });

                    binding.name.setOnClickListener(v -> {
                        Intent intent = new Intent(ChatActivity.this, UserProfile.class);
                        intent.putExtra("hisUid", hisId);
                        startActivity(intent);
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        binding.textBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                HashMap<String, Object> hashMap = new HashMap<>();
//                if (count == 0){
//                    hashMap.put("typingTo", "noOne");
//                }else {
//                    hashMap.put("typingTo", hisId);
//                }
//                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        binding.send.setOnClickListener(v -> {
            if (binding.textBox.getText().toString().isEmpty()){
                Snackbar.make(v,"Type a message", Snackbar.LENGTH_LONG).show();
            }else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg", binding.textBox.getText().toString());
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", ""+System.currentTimeMillis());
                hashMap.put("type", "text");
                hashMap.put("hide", false);
                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                notify = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        if (notify){
                            String msg = binding.textBox.getText().toString();
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), msg);
                            binding.textBox.setText("");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        //Bottom
        addAttachment();
        readMessage();
        seenMessage();
        chatList();
        binding.attach.setOnClickListener(v -> bottomSheetDialog.show());
        binding.more.setOnClickListener(v -> bottomDialog.show());

//        Intent intent = getIntent();
//        hisUid = intent.getStringExtra("hisUid");
//
//        //Firebase
//        myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//
//        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//        userRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
//        hisRef = FirebaseDatabase.getInstance().getReference("Users").child(hisUid);
//
//        Query query = databaseReference.orderByChild("id").equalTo(hisUid);
//        query.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    user = ds.getValue(ModelUser.class);
////                     name = ""+ ds.child("name").getValue();
////                     photo = ""+ ds.child("photo").getValue();
////                     status = ""+ ds.child("status").getValue();
////                     typingStatus = ""+ ds.child("typingTo").getValue();
////                     phone = ""+ ds.child("phone").getValue();
//
//
////                    call.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            String timeStamp = String.valueOf(System.currentTimeMillis());
////                            HashMap<Object, String> hashMap = new HashMap<>();
////                            hashMap.put("id", myUid);
////                            hashMap.put("name", name);
////                            hashMap.put("photo", photo);
////                            hashMap.put("pId", timeStamp);
////                            hashMap.put("phone", phone);
////                            hashMap.put("userid", hisUid);
////                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("CallList");
////                            dRef.child(timeStamp).setValue(hashMap)
////                                    .addOnSuccessListener(aVoid -> {
////                                    })
////                                    .addOnFailureListener(e -> {
////                                    });
////                            Uri call = Uri.parse("tel:" + phone);
////                            Intent surf = new Intent (Intent.ACTION_CALL,call);
////                            startActivity(surf);
////
////                        }
////                    });
//                }
//                if (Objects.requireNonNull(user).getTypingTo().equals(myUid)){
//                    binding.relativeLayout15.setVisibility(View.VISIBLE);
//                    binding.updated.setVisibility(View.VISIBLE);
//                }else {
//                    binding.relativeLayout15.setVisibility(View.GONE);
//                    binding.updated.setVisibility(View.GONE);
//                }
//
//                if (user.getStatus().equals("online")) {
//                    binding.username.setText(user.getStatus());
//                }else {
//                    long lastTime = Long.parseLong(user.getStatus());
//                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
//                    binding.username.setText(lastSeenTime);
//                }
//
//                binding.name.setText(user.getName());
//
//                try {
//                    Picasso.get().load(user.getPhoto()).placeholder(R.drawable.placeholder).into(binding.circleImageView3);
//                }catch (Exception e){
//                    Picasso.get().load(R.drawable.placeholder).into(binding.circleImageView3);
//                }
//                readMessage();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        binding.attach.setOnClickListener(this);
//        binding.more.setOnClickListener(this);
//        binding.send.setOnClickListener(this);
//        binding.name.setOnClickListener(this);
//        binding.circleImageView3.setOnClickListener(this);
//
//        binding.textBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().trim().length() == 0){
//                    checkTypingStatus("noOne");
//                }else {
//                    checkTypingStatus(hisUid);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        seenMessage();
//        createBottomDialog();
//
//        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myUid).child(hisUid);
//        chatRef1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()){
//                    chatRef1.child("id").setValue(hisUid);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(hisUid).child(myUid);
//        chatRef2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()){
//                    chatRef2.child("id").setValue(myUid);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


//        String action = intent.getAction();
//        String type = intent.getType();
//        if (Intent.ACTION_SEND.equals(action) && type!=null){
//            if ("text/plain".equals(type)){
//                sendText(intent);
//            }
//            else if (type.startsWith("image")){
//                sendChatImage(intent);
//            }else if (type.startsWith("video")) {
//                sendChatVideo(intent);
//            }
//        }
//
        checkBlocked();
        imBLockedOrNot();

        binding.chat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setStackFromEnd(true);
        binding.chat.setLayoutManager(layoutManager);
//        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(ChatActivity.this) {
//            @Override protected int getVerticalSnapPreference() {
//                return LinearSmoothScroller.SNAP_TO_END;
//            }
//        };
//        layoutManager.startSmoothScroll(smoothScroller);
//        smoothScroller.setTargetPosition(position);
//        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if(scrollX == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
////                mCurrenPage++;
//            }
//        });

    }

    private void chatList() {
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisId);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisId)
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

            }
        });
    }

    private void addAttachment() {
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.add_bottom_sheet, null);
            ConstraintLayout  attachphoto = view.findViewById(R.id.attachphoto);
            ConstraintLayout  attachvideo = view.findViewById(R.id.attachvideo);

            attachphoto.setOnClickListener(this);
            attachvideo.setOnClickListener(this);

            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
        if (post_more == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.chat_more, null);
            image = view.findViewById(R.id.image);
            image.setOnClickListener(this);
            video = view.findViewById(R.id.video);
            video.setOnClickListener(this);
            audio = view.findViewById(R.id.audio);
            audio.setOnClickListener(this);
            audio.setVisibility(View.GONE);
            document = view.findViewById(R.id.document);
            document.setOnClickListener(this);
            document.setVisibility(View.GONE);
            location = view.findViewById(R.id.location);
            location.setOnClickListener(this);
            location.setVisibility(View.GONE);
            watch_party = view.findViewById(R.id.watch_party);
            watch_party.setOnClickListener(this);
            watch_party.setVisibility(View.GONE);
            camera = view.findViewById(R.id.camera);
            camera.setOnClickListener(this);
            camera.setVisibility(View.GONE);
            recorder = view.findViewById(R.id.recorder);
            recorder.setOnClickListener(this);
            recorder.setVisibility(View.GONE);
            meeting = view.findViewById(R.id.meeting);
            meeting.setOnClickListener(this);
            meeting.setVisibility(View.GONE);
            stickers = view.findViewById(R.id.stickers);
            stickers.setOnClickListener(this);
            stickers.setVisibility(View.GONE);
            post_more = new BottomSheetDialog(this);
            post_more.setContentView(view);
        }
        if (bottomDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.more_bottom_sheet, null);
            ConstraintLayout block = view.findViewById(R.id.block);
            ConstraintLayout clearchat = view.findViewById(R.id.clearchat);
            ConstraintLayout userinfo = view.findViewById(R.id.userinfo);
            blocked = view.findViewById(R.id.blocked);

            block.setOnClickListener(this);
            clearchat.setOnClickListener(this);
            userinfo.setOnClickListener(this);
            bottomDialog = new BottomSheetDialog(ChatActivity.this);
            bottomDialog.setContentView(view);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(binding.main, "Storage permission allowed", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(binding.main, "Storage permission is required", Snackbar.LENGTH_LONG).show();
            }
            if (requestCode == PERMISSION_REQ_CODE) {
                boolean granted = true;
                for (int result : grantResults) {
                    granted = (result == PackageManager.PERMISSION_GRANTED);
                    if (!granted) break;
                }

                if (granted) {
                } else {
                    Snackbar.make(binding.main, "Permission is required", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image:

                post_more.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImage();
                    }
                }
                else {
                    pickImage();
                }

                break;
            case R.id.video:

                post_more.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickVideo();
                    }
                }
                else {
                    pickVideo();
                }

                break;
            case R.id.audio:

                post_more.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickAudio();
                    }
                }
                else {
                    pickAudio();
                }
                break;
            case  R.id.location:

                post_more.cancel();

//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(ChatActivity.this), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }

                break;

            case  R.id.document:

                post_more.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickDoc();
                    }
                }
                else {
                    pickDoc();
                }

                break;

            case  R.id.stickers:

                post_more.cancel();

//                Intent s = new Intent(ChatActivity.this, Stickers.class);
//                s.putExtra("type", "user");
//                s.putExtra("id", hisId);
//                startActivity(s);

                break;

            case  R.id.recorder:

                post_more.cancel();

                check();

//                if (isShown){
//                    findViewById(R.id.mediaRecord).setVisibility(View.GONE);
//                    isShown = false;
//                }else {
//                    findViewById(R.id.mediaRecord).setVisibility(View.VISIBLE);
//                    isShown = true;
//                }

                break;
            case R.id.meeting:
                post_more.cancel();
//                startActivity(new Intent(ChatActivity.this, MeetingActivity.class));
                break;

            case R.id.watch_party:
                post_more.cancel();

//                Query q = FirebaseDatabase.getInstance().getReference().child("Party").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                q.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()){
//                            if (ds.child("from").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                ds.getRef().removeValue();
//                                startActivity(new Intent(ChatActivity.this, StartWatchPartyActivity.class));
//                            }else {
//                                startActivity(new Intent(ChatActivity.this, StartWatchPartyActivity.class));
//                            }
//                        }
//                        startActivity(new Intent(ChatActivity.this, StartWatchPartyActivity.class));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                break;

            case R.id.camera:
                post_more.cancel();
//                startActivity(new Intent(ChatActivity.this, FaceFilters.class));
                break;
            case R.id.clearchat:
                bottomDialog.cancel();
                clearchats();
                break;
            case R.id.userinfo:
                bottomDialog.cancel();
                Intent intent = new Intent(ChatActivity.this, UserProfile.class);
                intent.putExtra("hisUid", hisId);
                startActivity(intent);
                break;
            case R.id.block:
                bottomDialog.cancel();
                if (isBlocked){
                    unBlockUser();
                }else {
                    BlockUser();
                }
                break;
            case R.id.attachphoto:
                bottomSheetDialog.cancel();
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImage();
                    }
                }
                else {
                    pickImage();
                }
                break;
            case R.id.attachvideo:
                bottomSheetDialog.cancel();
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickVideo();
                    }
                }
                else {
                    pickVideo();
                }
                break;
        }
    }

    private void clearchats() {
        FirebaseDatabase.getInstance().getReference("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelChat chat = snapshot.getValue(ModelChat.class);

                    if (Objects.requireNonNull(chat).getReceiver().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) && chat.getSender().equals(hisId)){
                            nChat.add(chat);
                            if(!nChat.isEmpty()){
                                Map<String, Object> hashMap = new HashMap<>();
                                hashMap.put("hide", true);
                                snapshot.getRef().updateChildren(hashMap);
                                Snackbar.make(binding.main, "Message cleared for me... ", Snackbar.LENGTH_LONG).show();
                            }
                    }
                    if (chat.getReceiver().equals(hisId) && chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            snapshot.getRef().removeValue();
                            Snackbar.make(binding.main, "Message cleared", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void check() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }

        if (granted) {

        } else {
            requestPermissions();
        }
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
    }

    private void pickDoc() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        startActivityForResult(intent, DOC_PICK_CODE);
    }

    private void pickAudio() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("audio/*");
        startActivityForResult(intent, AUDIO_PICK_CODE);
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK_CODE);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            Uri dp_uri = Objects.requireNonNull(data).getData();
            sendImage(dp_uri);
            binding.progressBar.setVisibility(View.VISIBLE);
            Snackbar.make(binding.main, "Please wait, Sending...", Snackbar.LENGTH_LONG).show();
        }
        if(resultCode == RESULT_OK && requestCode == VIDEO_PICK_CODE && data != null){
            Uri video_uri = Objects.requireNonNull(data).getData();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getApplicationContext(), video_uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMilli = Long.parseLong(time);
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (timeInMilli > 50000){
                Snackbar.make(binding.main, "Video must be of 5 minutes or less", Snackbar.LENGTH_LONG).show();
            }else {
                binding.progressBar.setVisibility(View.VISIBLE);
                Snackbar.make(binding.main, "Please wait, Sending...", Snackbar.LENGTH_LONG).show();
                sendVideo(video_uri);
//                new CompressVideo().execute("false",video_uri.toString(),file.getPath());
            }
        }
        if (resultCode == RESULT_OK && requestCode == AUDIO_PICK_CODE && data != null){
            Uri audio_uri = Objects.requireNonNull(data).getData();
            sendAudio(audio_uri);
            binding.progressBar.setVisibility(View.VISIBLE);
            Snackbar.make(binding.main, "Please wait, Sending...", Snackbar.LENGTH_LONG).show();
        }
        if (resultCode == RESULT_OK && requestCode == DOC_PICK_CODE && data != null){
            Uri doc_uri = Objects.requireNonNull(data).getData();
            sendDoc(doc_uri);
            binding.progressBar.setVisibility(View.VISIBLE);
            Snackbar.make(binding.main, "Please wait, Sending...", Snackbar.LENGTH_LONG).show();
        }
        if (resultCode == RESULT_OK && requestCode == PLACE_PICKER_REQUEST && data != null){
//            Place place = PlacePicker.getPlace(data, this);
//            String latitude = String.valueOf(place.getLatLng().latitude);
//            String longitude = String.valueOf(place.getLatLng().longitude);
            String time = ""+System.currentTimeMillis();
            //Message
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("receiver", hisId);
            hashMap.put("msg", time);
            hashMap.put("isSeen", false);
            hashMap.put("timestamp", time);
            hashMap.put("type", "location");
            FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);

            //Location
            HashMap<String, Object> hashMap2 = new HashMap<>();
//            hashMap2.put("latitude", latitude);
//            hashMap2.put("longitude", longitude);
            hashMap2.put("id", time);
            FirebaseDatabase.getInstance().getReference().child("Location").child(time).setValue(hashMap2);

            notify = true;
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    if (notify){
                        sendNotification(hisId, Objects.requireNonNull(user).getName(), "has sent location");
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


            Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void LoadFfmpegLibrary(){
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_hh-mm", Locale.CANADA);
//        Date now = new Date();
//        String videoFileName = "Video-" + formatter.format(now) + ".mp4";
//        if(fFmpeg == null){
//            fFmpeg = FFmpeg.getInstance(ChatActivity.this);
//            try {
//                fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                    @Override
//                    public void onStart() {
//                    }
//
//                    @Override
//                    public void onFailure() {
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        Compress(videoFileName);
//                    }
//                });
//            } catch (FFmpegNotSupportedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Compress(videoFileName);
//        }
//    }
//
//    private void Compress(String fileName){
//        String outputPath = getExternalFilesDir("/").getAbsolutePath() + "/My Folder/" + fileName;
//        String[] commandArray;
//        commandArray = new String[]{"-y", "-i", String.valueOf(video_uri), "-s", "720x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "300k", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputPath};
//        final ProgressDialog dialog = new ProgressDialog(ChatActivity.this);
//        try {
//            fFmpeg.execute(commandArray, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onStart() {
//                    Toast.makeText(ChatActivity.this, "please wait", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onProgress(String message) {
//                    Toast.makeText(ChatActivity.this, "progress", Toast.LENGTH_SHORT).show();
//                    Log.e("FFmpeg onProgress? ", message);
//                }
//                @Override
//                public void onFailure(String message) {
//                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
//                    Log.e("FFmpeg onFailure? ", message);
//                }
//                @Override
//                public void onSuccess(String message) {
//                    Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
//                    Log.e("FFmpeg onSuccess? ", message);
//
//                }
//                @Override
//                public void onFinish() {
//                    Uri compressedOutputUri = Uri.fromFile(new File(outputPath));
//                    sendVideo(compressedOutputUri);
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//        }
//    }

    private void sendDoc(Uri doc_uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_doc/" + ""+System.currentTimeMillis());
        storageReference.putFile(doc_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg", downloadUri.toString());
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", ""+System.currentTimeMillis());
                hashMap.put("type", "doc");
                hashMap.put("hide", false);
                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();
                notify = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "has sent a Document");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });
    }

    private void sendAudio(Uri audio_uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_audio/" + ""+System.currentTimeMillis());
        storageReference.putFile(audio_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg",  downloadUri.toString());
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", ""+System.currentTimeMillis());
                hashMap.put("type", "audio");
                hashMap.put("hide", false);
                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();
                notify = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "has sent a audio");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

//    @SuppressLint("StaticFieldLeak")
//    private class CompressVideo extends AsyncTask<String,String,String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String videoPath = null;
//            try {
//                Uri mUri = Uri.parse(strings[1]);
//                videoPath = SiliCompressor.with(ChatActivity.this)
//                        .compressVideo(mUri,strings[2]);
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            return videoPath;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            File file = new File(s);
//            Uri videoUri = Uri.fromFile(file);
//            sendVideo(videoUri);
//        }
//    }

    private void sendVideo(Uri videoUri){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_video/" + ""+System.currentTimeMillis());
        storageReference.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg",  downloadUri.toString());
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", ""+System.currentTimeMillis());
                hashMap.put("type", "video");
                hashMap.put("hide", false);
                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();
                notify = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "has sent a video");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void sendImage(Uri dp_uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_photo/" + ""+System.currentTimeMillis());
        storageReference.putFile(dp_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver", hisId);
                hashMap.put("msg",  downloadUri.toString());
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", ""+System.currentTimeMillis());
                hashMap.put("type", "image");
                hashMap.put("hide", false);
                FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();
                notify = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "has sent a image");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void readMessage(){
        nChat = new ArrayList<>();
        //Get
        FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if (Objects.requireNonNull(chat).getReceiver().equals(userId) && chat.getSender().equals(hisId) ||
                                chat.getReceiver().equals(hisId) && chat.getSender().equals(userId)){
                            nChat.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, nChat);
                    binding.chat.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(){
       FirebaseDatabase.getInstance().getReference("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if (snapshot.exists()){
                        ModelChat modelChat = snapshot.getValue(ModelChat.class);
                            if (Objects.requireNonNull(modelChat).getReceiver().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) && modelChat.getSender().equals(hisId)){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isSeen", true);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " : " + message, "New Message", hisId, "chat", R.drawable.logo);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Log.d("onResponse%s", response.toString()), error -> Log.d("onResponse%s", error.toString())){
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        String timestamp = ""+System.currentTimeMillis();
        FirebaseDatabase.getInstance().getReference("Users").child(hisId).child("Countm").child(timestamp).setValue(true);
    }

    private void imBLockedOrNot (){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(hisId);
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            binding.constraintLayout5.setVisibility(View.GONE);
                            binding.constraintLayout49.setVisibility(View.VISIBLE);
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(hisId)) {
                                blocked.setText("Unblock");
                                binding.constraintLayout99.setVisibility(View.VISIBLE);
                                binding.constraintLayout5.setVisibility(View.GONE);
                                isBlocked = true;
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void BlockUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child(hisId).setValue(true).addOnSuccessListener(aVoid -> {
                    binding.constraintLayout5.setVisibility(View.GONE);
                    binding.constraintLayout99.setVisibility(View.VISIBLE);
                }).addOnFailureListener(e -> {
                });
    }

    private void unBlockUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChild(hisId)) {
                    snapshot.getRef().child(hisId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            binding.constraintLayout5.setVisibility(View.VISIBLE);
                            binding.constraintLayout99.setVisibility(View.GONE);
                            blocked.setText("Block");
                            isBlocked = false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
