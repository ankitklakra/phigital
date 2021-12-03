package com.phigital.ai.Upload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.phigital.ai.Adapter.AdapterUsersPost;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityPostBinding;
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
@SuppressLint("SetTextI18n")
public class PostActivity extends BaseActivity implements View.OnClickListener {

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

    SharedPref sharedPref;
    Uri resulturi;

    AdapterUsersPost adapterUsers;
    List<ModelUser> userList;

    BottomSheetDialog locationdialog;
    Button locationdone;

    TextInputEditText address;

    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;

    String name, dp, id ,username,privacyType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        id = Objects.requireNonNull(user).getUid();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "usertag"
        ));

        binding.image.setOnClickListener(v -> {
            TedImagePicker.with(PostActivity.this).backButton(R.drawable.ic_left).start(this::showSingleImage);
        });

        binding.meme.setOnClickListener(v -> {
            TedImagePicker.with(PostActivity.this).backButton(R.drawable.ic_left).start(this::showSingleImage);
        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.location.setOnClickListener(v -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken("pk.eyJ1IjoicGhpZ2l0YWwtYWkiLCJhIjoiY2tzaGQ4dWJrMTloZzMwb2ZocHdwZzg5ZiJ9.i4HyC_bMbjwyZiAdwIbO7w")
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#ffffff"))
                            .build(PlaceOptions.MODE_CARDS))
                    .build(this);
            startActivityForResult(intent, LOCATION_PICK_CODE);
        });

        verifyStoragePermission(PostActivity.this);

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

        binding.postIt.setOnClickListener(v -> {
            binding.postIt.setEnabled(false);

            if (privacyType == null){
                privacyType = "public";
            }

            binding.pb.setVisibility(View.VISIBLE);

            if (type.equals("image")){
                uploadImage(compressedImageFile);
            }
            if (type.equals("none")){
                if (Objects.requireNonNull(binding.textBox.getText()).toString().isEmpty()){
                    Snackbar.make(v,"Type something", Snackbar.LENGTH_LONG).show();
                    binding.pb.setVisibility(View.GONE);
                    binding.postIt.setEnabled(true);
                }else {
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    hashMap.put("pId", timeStamp);
                    hashMap.put("text", binding.textBox.getText().toString());
                    hashMap.put("pViews", "0");
                    hashMap.put("rViews", "0");
                    hashMap.put("type", "text");
                    hashMap.put("video", "no");
                    hashMap.put("image", "no");
                    hashMap.put("reTweet", "");
                    hashMap.put("reId", "");
                    hashMap.put("content", "normal");
                    hashMap.put("privacy", privacyType);
                    hashMap.put("pTime", timeStamp);
                    hashMap.put("location", ""+mLocation);
                    FirebaseDatabase.getInstance().getReference("Posts").child(timeStamp).setValue(hashMap);
                    Snackbar.make(v,"Post Uploaded", Snackbar.LENGTH_LONG).show();

                    //extra
                    setDefault();
                }
//                if (!TextUtils.isEmpty(mText) && compressedImageFile == null){
//                    String timeStamp = String.valueOf(System.currentTimeMillis());
//                    HashMap<Object, String> hashMap = new HashMap<>();
//                    hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//                    hashMap.put("pId", timeStamp);
//                    hashMap.put("text", mText);
//                    hashMap.put("pViews", "0");
//                    hashMap.put("rViews", "0");
//                    hashMap.put("type", "text");
//                    hashMap.put("video", "no");
//                    hashMap.put("image", "no");
//                    hashMap.put("reTweet", "");
//                    hashMap.put("reId", "");
//                    hashMap.put("content", "normal");
//                    hashMap.put("privacy", "" + privacyType);
//                    hashMap.put("pTime", timeStamp);
//                    hashMap.put("location", locations);
//                    hashMap.put("link", "");
//                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
//                    dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
//                        binding.textBox.setText("");
//                        binding.meme.setImageURI(null);
//                        binding.postIt.setEnabled(true);
//                        binding.pb.setVisibility(View.GONE);
//                        type = "none";
//                        new StyleableToast
//                                .Builder(getApplicationContext())
//                                .text("Feel Uploaded")
//                                .textColor(Color.WHITE)
//                                .textBold()
//                                .gravity(0)
//                                .length(2000)
//                                .solidBackground()
//                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                                .show();
//                    });
//                }
            }

        });

        userList = new ArrayList<>();

        binding.rcv.setHasFixedSize(true);
        binding.rcv.setLayoutManager(new LinearLayoutManager(PostActivity.this));
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

        loadb();
    }

    private void uploadImage(File compressedImageFile) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Post/" + "Post_" + ""+System.currentTimeMillis());
        storageReference.putFile(Uri.fromFile(compressedImageFile)).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUri = uriTask.getResult();
                    if (uriTask.isSuccessful()){
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        hashMap.put("pId", timeStamp);
                        hashMap.put("text", binding.textBox.getText().toString());
                        hashMap.put("pViews", "0");
                        hashMap.put("rViews", "0");
                        hashMap.put("type", "image");
                        hashMap.put("video", "no");
                        hashMap.put("image", downloadUri.toString());
                        hashMap.put("reTweet", "");
                        hashMap.put("reId", "");
                        hashMap.put("content", "normal");
                        hashMap.put("privacy", privacyType);
                        hashMap.put("pTime", timeStamp);
                        hashMap.put("location", ""+mLocation);
                        FirebaseDatabase.getInstance().getReference("Posts").child(timeStamp).setValue(hashMap);
                        Snackbar.make(binding.postlayout,"Post Uploaded", Snackbar.LENGTH_LONG).show();

                        setDefault();
                    }
        });
    }


    private void setDefault() {
        binding.textBox.setText("");
        binding.meme.setImageURI(null);
        binding.postIt.setEnabled(true);
        binding.loctv.setText("Add Location");
        binding.pb.setVisibility(View.GONE);
        type = "none";
        mLocation = "";
    }


//    private void showvideo(Uri uri) {
////        binding.playerview.set(uri);
//        video_uri = uri;
//        final SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
//        binding.playerview.setPlayer(player);
//        MediaItem mediaItem = MediaItem.fromUri(video_uri);
//        player.setMediaItem(mediaItem);
//        player.setRepeatMode(Player.REPEAT_MODE_OFF);
//        player.setPlayWhenReady(true);
//        binding.fl.setVisibility(View.VISIBLE);
//        binding.playerview.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//        type = "video";
////                player.setVideoScalingMode();
//    };

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
        Uri destinationfile = Uri.fromFile(new File(PostActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(16,9);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(PostActivity.this);
    }

    private void crop1(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(PostActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        options.setAspectRatioOptions(1,
                new AspectRatio(null,a,a),
                new AspectRatio(null,b,c)
        );

        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(PostActivity.this);
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
                    adapterUsers = new AdapterUsersPost(PostActivity.this, userList);
                   binding.rcv.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadb() {
        if (locationdialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(PostActivity.this).inflate(R.layout.location_dialog, null);

            address = view.findViewById(R.id.address);
            locationdone = view.findViewById(R.id.locationdone);
            locationdone.setOnClickListener(this);
            locationdialog = new BottomSheetDialog(PostActivity.this);
            locationdialog.setContentView(view);

            String text = Objects.requireNonNull(address.getText()).toString();
            if (!text.isEmpty()){
                address.setText(text);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locationdone:
                locationdialog.cancel();
                String s =  (Objects.requireNonNull(address.getText())).toString().toLowerCase().trim();
                if (s.equals("sponsored")){
                    Toast.makeText(PostActivity.this, "Invalid location ", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostActivity.this, "Location is set to " +s, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Storage permission Allowed", duration).show();
            } else {
                Toast.makeText(getApplicationContext(), "Storage permission is required", duration).show();
            }
        }
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Read external storage Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
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
                }
            }
        }
        //Location
        if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_PICK_CODE && data != null) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
             binding.loctv.setText(feature.text());
            mLocation = feature.text();
        }
    }


    public static void verifyStoragePermission(Activity activity){
        int permission =ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}