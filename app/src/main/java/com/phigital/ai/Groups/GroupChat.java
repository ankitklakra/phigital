package com.phigital.ai.Groups;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iceteck.silicompressorr.SiliCompressor;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.Adapter.AdapterGroupChat;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Chat.ChatActivity;
import com.phigital.ai.Model.ModelGroupChat;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityGroupChatBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupChat extends BaseActivity implements View.OnClickListener {

    private static String GroupId;
    public static String getGroupId() {
        return GroupId;
    }
    ActivityGroupChatBinding binding;
    DatabaseReference likeRef;

    ImageView imageView21;
    String mchatid,msgtimestamp,sender,msg,type;
    RecyclerView recyclerView;
    private static final int PICK_VIDEO_REQUEST = 1;
    EditText textBox;
    ImageView send,back,attach,more;
    TextView mName,mUsername;
    CircleImageView circleImageView;
    String  myGroupRole;
    private FirebaseAuth mAuth;
    private ArrayList<ModelGroupChat> groupChats;
    private AdapterGroupChat adapterGroupChat;
    ConstraintLayout attachphoto;
    BottomSheetDialog bottomSheetDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int VIDEO_PICK_CODE = 1002;
    private static final int PERMISSION_CODE = 1001;
    private String userId;
    ConstraintLayout add,info,post,edit,leave,reportCL;
    BottomSheetDialog bottomDialog;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");
        send = findViewById(R.id.imageView10);
        textBox = findViewById(R.id.textBox);
        recyclerView = findViewById(R.id.chat);
        mName = findViewById(R.id.name);
        mUsername = findViewById(R.id.username);
        attach = findViewById(R.id.imageView11);
        back = findViewById(R.id.imageView9);
        back.setOnClickListener(v -> onBackPressed());
        more = findViewById(R.id.more);
        circleImageView = findViewById(R.id.circleImageView3);
//        binding.chat.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GroupChat.this);
//        linearLayoutManager.setStackFromEnd(true);
//        binding.chat.setLayoutManager(linearLayoutManager);
        loadGroupInfo();
        loadGroupMessage();
        loadMyGroupRole();

        attach.setOnClickListener(v -> bottomSheetDialog.show());

        send.setOnClickListener(v -> {
            String message = textBox.getText().toString().trim();
            if (TextUtils.isEmpty(message)){

            }else {
                sendMessage(message);
            }
            textBox.setText("");
        });

        more.setOnClickListener(v -> bottomDialog.show());
        createBottomDialog();
    }

    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants")
                .orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child("role").getValue();
                            if(myGroupRole.equals("creator")){
                                add.setVisibility(View.VISIBLE);
                                edit.setVisibility(View.VISIBLE);
                                leave.setVisibility(View.GONE);
                                post.setVisibility(View.VISIBLE);
                                reportCL.setVisibility(View.GONE);
                            }
                            if (myGroupRole.equals("admin")){
                                add.setVisibility(View.VISIBLE);
                                edit.setVisibility(View.VISIBLE);
                                reportCL.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createBottomDialog() {
        if (bottomDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.more_group_bottom_sheet, null);

            add = view.findViewById(R.id.chatshare);
            info = view.findViewById(R.id.appshare);

            post = view.findViewById(R.id.addgpost);
            edit = view.findViewById(R.id.editgroup);
            leave = view.findViewById(R.id.leave);
            reportCL = view.findViewById(R.id.reportCL);

            add.setOnClickListener(this);
            info.setOnClickListener(this);
            post.setOnClickListener(this);
            edit.setOnClickListener(this);
            leave.setOnClickListener(this);
            reportCL.setOnClickListener(this);

            add.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);


        }
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.add_bottom_sheet, null);
            ConstraintLayout  attachphoto = view.findViewById(R.id.attachphoto);
            ConstraintLayout  attachvideo = view.findViewById(R.id.attachvideo);

            attachphoto.setOnClickListener(this);
            attachvideo.setOnClickListener(this);

            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

    private void loadGroupMessage() {
        groupChats = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChats.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelGroupChat modelGroupChat = ds.getValue(ModelGroupChat.class);
                            groupChats.add(modelGroupChat);
                        }
                       adapterGroupChat = new AdapterGroupChat(GroupChat.this, groupChats);
                        recyclerView.setAdapter(adapterGroupChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String message) {

        String timestamp = ""+ System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", mAuth.getUid());
        hashMap.put("msg", message);
        hashMap.put("type", "text");
        hashMap.put("timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message").push()
                .setValue(hashMap)
                .addOnSuccessListener(aVoid -> textBox.setText("")).addOnFailureListener(e -> {}
        );
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(GroupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("gName").getValue();
                            String userName = ""+ds.child("gUsername").getValue();
                            String icon = ""+ds.child("gIcon").getValue();

                            mName.setText(name);
                            mUsername.setText(userName);
                            try {
                                Picasso.get().load(icon).placeholder(R.drawable.group).into(circleImageView);
                            }catch (Exception e){
                                Picasso.get().load(R.drawable.group).into(circleImageView);
                            }
                            mName.setOnClickListener(v -> {

                                Intent intent8 = new Intent(GroupChat.this, GroupProfile.class);
                                intent8.putExtra("groupId", GroupId);
                                startActivity(intent8);
                            });
                            circleImageView.setOnClickListener(v -> {

                                Intent intent8 = new Intent(GroupChat.this, GroupProfile.class);
                                intent8.putExtra("groupId", GroupId);
                                startActivity(intent8);
                            });
                            mUsername.setOnClickListener(v -> {

                                Intent intent8 = new Intent(GroupChat.this, GroupProfile.class);
                                intent8.putExtra("groupId", GroupId);
                                startActivity(intent8);
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.attachphoto:
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
            case R.id.chatshare:
                bottomDialog.cancel();
                Intent intent = new Intent(this, AddParticipants.class);
                intent.putExtra("groupId", GroupId);
                startActivity(intent);
                break;
            case R.id.leave:
                bottomDialog.cancel();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupChat.this);
                builder2.setTitle("Leave group");
                builder2.setMessage("Are you sure to leave this group?");
                builder2.setPositiveButton("Leave", (dialog, which) -> {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(GroupId).child("Participants").child(userId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text("You left group")
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .gravity(0)
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }).addOnFailureListener(e -> {
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text(e.getMessage())
                                .textColor(Color.WHITE)
                                .textBold()
                                .gravity(0)
                                .length(2000)
                                .solidBackground()
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                    });
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder2.create().show();
                break;
            case R.id.appshare:
                bottomDialog.cancel();
                Intent intent8 = new Intent(this, GroupProfile.class);
                intent8.putExtra("groupId", GroupId);
                startActivity(intent8);
                break;
            case R.id.addgpost:
                bottomDialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChat.this);
                builder.setTitle("Delete group");
                builder.setMessage("Are you sure to delete this group?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Groups");
                    ref2.child(GroupId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text("Group deleted")
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .gravity(0)
                                        .length(2000)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }).addOnFailureListener(e -> {
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text(e.getMessage())
                                .textColor(Color.WHITE)
                                .textBold()
                                .gravity(0)
                                .length(2000)
                                .solidBackground()
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                    });
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
                break;
            case R.id.editgroup:
                bottomDialog.cancel();
                Intent intent9 = new Intent(this, EditGroup.class);
                intent9.putExtra("groupId", GroupId);
                startActivity(intent9);
                break;
            case R.id.reportCL:
                bottomDialog.cancel();
                AlertDialog.Builder builder4 = new AlertDialog.Builder(GroupChat.this);
                builder4.setTitle("Delete");
                builder4.setMessage("Are you sure to report this group?");
                builder4.setPositiveButton("Report", (dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference().child("GroupReport").child(GroupId).setValue(true);
                    Toast.makeText(GroupChat.this, "Group is reported", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder4.create().show();
                break;
    }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
//                Alerter.create(GroupChat.this)
//                        .setTitle("Successful")
//                        .setIcon(R.drawable.ic_check_wt)
//                        .setBackgroundColorRes(R.color.colorPrimaryDark)
//                        .setDuration(10000)
//                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .setText("Storage permission Allowed")
//                        .show();
            } else {
//                Alerter.create(GroupChat.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_error)
//                        .setBackgroundColorRes(R.color.colorPrimaryDark)
//                        .setDuration(10000)
//                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .setText("Storage permission is required")
//                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendVideo(Uri video_uri) {
        String timeStamp = ""+ System.currentTimeMillis();
        String filenameAndPath = "GroupChatImages/"+"post_"+ timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", mAuth.getUid());
                hashMap.put("msg", downloadUri);
                hashMap.put("type", "video");
                hashMap.put("timestamp", timeStamp);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(GroupId).child("Message").push().setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Void unused) {
                        textBox.setText("");
                        binding.progressBar.setVisibility(View.GONE);
                        Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Snackbar.make(binding.main, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });


            }
        }).addOnFailureListener(e -> {
            Snackbar.make(binding.main, e.getMessage(), Snackbar.LENGTH_LONG).show();
        });
    }

    private void sendImage(Uri image_uri) {
        String timeStamp = ""+ System.currentTimeMillis();
        String filenameAndPath = "GroupChatImages/"+"post_"+ timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", mAuth.getUid());
                hashMap.put("msg", downloadUri);
                hashMap.put("type", "image");
                hashMap.put("timestamp", timeStamp);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(GroupId).child("Message").push().setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull @NotNull Void unused) {
                                textBox.setText("");
                                binding.progressBar.setVisibility(View.GONE);
                                Snackbar.make(binding.main, "Sent", Snackbar.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Snackbar.make(binding.main, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Snackbar.make(binding.main, e.getMessage(), Snackbar.LENGTH_LONG).show();
        });

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

    @SuppressLint("StaticFieldLeak")
    private class CompressVideo extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String videoPath = null;
            try {
                Uri mUri = Uri.parse(strings[1]);
                videoPath = SiliCompressor.with(GroupChat.this)
                        .compressVideo(mUri,strings[2]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return videoPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            File file = new File(s);
            Uri videoUri = Uri.fromFile(file);
            sendVideo(videoUri);
        }
    }
}