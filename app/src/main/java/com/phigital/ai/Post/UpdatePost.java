package com.phigital.ai.Post;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.phigital.ai.Adapter.AdapterUsersPost;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityPostBinding;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import gun0912.tedimagepicker.builder.TedImagePicker;
import id.zelory.compressor.Compressor;

public class UpdatePost extends BaseActivity {

    int a = 1;
    int b = 4;
    int c = 5;

    String type = "none";
    private static final int LOCATION_PICK_CODE = 1009;
    ActivityPostBinding binding;

    int duration = Toast.LENGTH_SHORT;

    private static final int PERMISSION_CODE = 1001;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int MY_READ_PERMISSION_CODE = 101;

    String mLocation = "";

    File compressedImageFile;

    String hisId,hispId,hisText,hisViews,hisrViews,hisType,hisImage,hisVideo,hisreTweet,hisreId,hisTime,hisPrivacy,hisLocation,hisContent,hisLink;
    String change = "no";
    Uri resulturi;

    AdapterUsersPost adapterUsers;
    List<ModelUser> userList;

    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;

    String name, dp, id ,username,privacyType;

    SharedPref sharedPref;
    String editPostId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        id = Objects.requireNonNull(user).getUid();

        Intent intent1 = getIntent();
        String isUpdateKey = ""+intent1.getStringExtra("key");
        editPostId = ""+intent1.getStringExtra("editPostId");
        if (isUpdateKey.equals("editPost")){
            loadPostData(editPostId);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "usertag"
        ));

        binding.image.setOnClickListener(v -> {
            TedImagePicker.with(UpdatePost.this).backButton(R.drawable.ic_left).start(this::showSingleImage);
        });

        binding.meme.setOnClickListener(v -> {
            TedImagePicker.with(UpdatePost.this).backButton(R.drawable.ic_left).start(this::showSingleImage);
        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

//        binding.location.setOnClickListener(v -> {
//            Intent intent2 = new PlaceAutocomplete.IntentBuilder()
//                    .accessToken("pk.eyJ1IjoicGhpZ2l0YWwtYWkiLCJhIjoiY2tzaGQ4dWJrMTloZzMwb2ZocHdwZzg5ZiJ9.i4HyC_bMbjwyZiAdwIbO7w")
//                    .placeOptions(PlaceOptions.builder()
//                            .backgroundColor(Color.parseColor("#ffffff"))
//                            .build(PlaceOptions.MODE_CARDS))
//                    .build(this);
//            startActivityForResult(intent2, LOCATION_PICK_CODE);
//        });

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
                username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        userList = new ArrayList<>();

        binding.rcv.setHasFixedSize(true);
        binding.rcv.setLayoutManager(new LinearLayoutManager(UpdatePost.this));
        binding.rcv.smoothScrollToPosition(0);

        binding.textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                parseText(s.toString());
            }
        });

        binding.postIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(change.equals("updated")){
                    binding.postIt.setEnabled(false);

                    if (privacyType == null){
                        privacyType = "public";
                    }

                    binding.pb.setVisibility(View.VISIBLE);

                    if (type.equals("image")){
                        uploadImage(compressedImageFile);
                    }
                    else{
                        if (Objects.requireNonNull(binding.textBox.getText()).toString().isEmpty()){
                            Snackbar.make(v,"Type something", Snackbar.LENGTH_LONG).show();
                            binding.pb.setVisibility(View.GONE);
                            binding.postIt.setEnabled(true);
                        }
                        else {
                            if (hisImage.equals("no")){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("text", binding.textBox.getText().toString());
                                hashMap.put("type", "text");
                                hashMap.put("privacy", privacyType);
                                hashMap.put("location", ""+mLocation);
                                FirebaseDatabase.getInstance().getReference("Posts").child(editPostId).updateChildren(hashMap);
                                Snackbar.make(v,"Post Updated", Snackbar.LENGTH_LONG).show();

                                //extra
                                setDefault();
                            }
                            else{
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("text", binding.textBox.getText().toString());
                                hashMap.put("type", "image");
                                hashMap.put("privacy", privacyType);
                                hashMap.put("location", ""+mLocation);
                                FirebaseDatabase.getInstance().getReference("Posts").child(editPostId).updateChildren(hashMap);
                                Snackbar.make(v,"Post Updated", Snackbar.LENGTH_LONG).show();

                                //extra
                                setDefault();
                            }
                        }
                    }
                }
                else{
                    binding.postIt.setEnabled(false);

                    if (privacyType == null){
                        privacyType = "public";
                    }

                    binding.pb.setVisibility(View.VISIBLE);

                    if (Objects.requireNonNull(binding.textBox.getText()).toString().isEmpty()){
                        Snackbar.make(v,"Type something", Snackbar.LENGTH_LONG).show();
                        binding.pb.setVisibility(View.GONE);
                        binding.postIt.setEnabled(true);
                    }
                    else {
                        if (hisImage.equals("no")){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("text", binding.textBox.getText().toString());
                            hashMap.put("type", "text");
                            hashMap.put("privacy", privacyType);
                            hashMap.put("location", ""+mLocation);
                            FirebaseDatabase.getInstance().getReference("Posts").child(editPostId).updateChildren(hashMap);
                            Snackbar.make(v,"Post Updated", Snackbar.LENGTH_LONG).show();

                            //extra
                            setDefault();
                        }
                        else{
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("text", binding.textBox.getText().toString());
                            hashMap.put("type", "image");
                            hashMap.put("privacy", privacyType);
                            hashMap.put("location", ""+mLocation);
                            FirebaseDatabase.getInstance().getReference("Posts").child(editPostId).updateChildren(hashMap);
                            Snackbar.make(v,"Post Updated", Snackbar.LENGTH_LONG).show();

                            //extra
                            setDefault();
                        }
                    }
                }
            }
        });
    }

    private void uploadImage(File compressedImageFile) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Post/" + "Post_" + ""+System.currentTimeMillis());
        storageReference.putFile(Uri.fromFile(compressedImageFile)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("text", binding.textBox.getText().toString());
                hashMap.put("type", "image");
                hashMap.put("image", downloadUri.toString());
                hashMap.put("privacy", privacyType);
                hashMap.put("location", ""+mLocation);
                FirebaseDatabase.getInstance().getReference("Posts").child(editPostId).updateChildren(hashMap);
                Snackbar.make(binding.postlayout,"Post Updated", Snackbar.LENGTH_LONG).show();

                setDefault();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setDefault() {
        binding.textBox.setText("");
        binding.meme.setImageURI(null);
        binding.postIt.setEnabled(true);
        binding.loctv.setText("Add Location");
        binding.pb.setVisibility(View.GONE);
        type = "none";
        mLocation = "";
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String user = intent.getStringExtra("username");
            binding.textBox.append(user);
        }
    };

    private void parseText(String text) {
        String[] words = text.split("[ \\.]");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0
                    && words[i].charAt(0) == '@') {
                if (!TextUtils.isEmpty(text)) {
                    filterUser(words[i]);
                }
//                System.out.println(words[i]);
            }
        }
    }

    private void filterUser(String query) {
        String string2 = query.replaceFirst("@", "");
        binding.rcv.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (Objects.requireNonNull(modelUser).getUsername().toLowerCase().contains(string2.toLowerCase()) ||
                            modelUser.getName().toLowerCase().contains(string2.toLowerCase()))
                        userList.add(modelUser);
                    adapterUsers = new AdapterUsersPost(UpdatePost.this, userList);
                    binding.rcv.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
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
                            binding.textBox.setVisibility(View.VISIBLE);
                            binding.textBox.setText(hisText);
                            break;
                        }
                        case"image": {
                            if (!hisText.isEmpty()){
                                binding.textBox.setText(hisText);
                                binding.textBox.setVisibility(View.VISIBLE);
                            }
                            binding.meme.setVisibility(View.VISIBLE);
                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.meme);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case"meme":{
                            binding.textBox.setVisibility(View.VISIBLE);
                            binding.textBox.setText(hisText);
                            binding.meme.setVisibility(View.VISIBLE);
                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.meme);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                    if (!hisLocation.isEmpty()){
                        mLocation = hisLocation;
                        binding.loctv.setText(hisLocation);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

//                pd.setVisibility(View.GONE);
//                Alerter.create(UpdatePost.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_check_wt)
//                        .setBackgroundColorRes(R.color.colorPrimaryDark)
//                        .setDuration(10000)
//                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .setText(databaseError.getMessage())
//                        .show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    resulturi = UCrop.getOutput(data);
                    binding.meme.setVisibility(View.VISIBLE);
                    binding.meme.setImageURI(resulturi);
                    try {
                        compressedImageFile = new Compressor(this).compressToFile(new File(resulturi.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    type = "image";
                    change = "updated";
                }
            }
        }

        //Location
//        if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_PICK_CODE && data != null) {
//            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
//            binding.loctv.setText(feature.text());
//            mLocation = feature.text();
//            change = "updated";
//        }
    }

    private void showSingleImage(Uri uri) {
        BitmapFactory.Options options  = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(),options);
        int H = options.outHeight;
        int W = options.outWidth;
        if (H > W){
            crop1(uri);
        }
        if (H < W){
            crop2(uri);
        }
        if (H == W){
            crop1(uri);
        }
    }

    private void crop2(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(UpdatePost.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(16,9);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(UpdatePost.this);
    }

    private void crop1(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(UpdatePost.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        options.setAspectRatioOptions(1,
                new AspectRatio(null,a,a),
                new AspectRatio(null,b,c)
        );

        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(UpdatePost.this);
    }

}