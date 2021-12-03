package com.phigital.ai.Groups;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityCreateGroupBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class CreateGroup extends BaseActivity {

    ActivityCreateGroupBinding binding;

    SharedPref sharedPref;
    private Uri image_uri;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private FirebaseAuth mAuth;
    String username,name,bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group);

        mAuth = FirebaseAuth.getInstance();

        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.profileImage.setOnClickListener(v -> {
            //Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromGallery();
                }
            }
            else {
                pickImageFromGallery();
            }
        });
        binding.button3.setOnClickListener(v -> {
            binding.pb.setVisibility(View.VISIBLE);
            username = binding.username.getText().toString().trim();
            name = binding.name.getText().toString().trim();
            bio = binding.bio.getText().toString().trim();
            Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("gUsername").equalTo(username);
            usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0){
                        binding.pb.setVisibility(View.GONE);
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("ProfileFinishActivity already exist")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .gravity(0)
                                .solidBackground()
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                    }else {
                        if (TextUtils.isEmpty(username)){
                            binding.pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Enter username")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                        if (TextUtils.isEmpty(name)){
                            binding.pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Enter name")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                        if (TextUtils.isEmpty(bio)){
                            binding.pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Enter bio")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .length(2000)
                                    .gravity(0)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        }
                        else {
                            createGroup();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    binding.pb.setVisibility(View.GONE);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text(databaseError.getMessage())
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000)
                            .gravity(0)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();

                }
            });
        });
    }

    private void createGroup() {
        binding.pb.setVisibility(View.VISIBLE);
        String name = binding.name.getText().toString();
        String bio = binding.bio.getText().toString();
        String link = binding.link.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username)){
//            Alerter.create(CreateGroup.this)
//                    .setTitle("Error")
//                    .setIcon(R.drawable.ic_error)
//                    .setBackgroundColorRes(R.color.colorPrimary)
//                    .setDuration(10000)
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .enableSwipeToDismiss()
//                    .setText("Enter name & username")
//                    .show();
            return;
        }
        String timeStamp = ""+ System.currentTimeMillis();
        if (image_uri == null){
            binding.pb.setVisibility(View.GONE);
            new StyleableToast
                    .Builder(getApplicationContext())
                    .text("Profile image should not be empty")
                    .textColor(Color.WHITE)
                    .textBold()
                    .length(2000)
                    .gravity(0)
                    .solidBackground()
                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                    .show();
//            createNoImageGroup(""+timeStamp,
//                    ""+name,""+username,
//                    ""+bio,""+link);
        }else {
            String fileNameAndPAth = "Group_image/"+"image" + timeStamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameAndPAth);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            createImageGroup(""+timeStamp,
                                    ""+name,""+username,
                                    ""+bio,""+link, ""+downloadUri);
                        }
                    }).addOnFailureListener(e -> {
                binding.pb.setVisibility(View.GONE);
                    });

        }
    }

    private void createImageGroup(String timeStamp, String name, String username, String bio, String link, String downloadUri) {
        binding.pb.setVisibility(View.VISIBLE);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+timeStamp);
        hashMap.put("gName", ""+name);
        hashMap.put("gUsername", ""+username);
        hashMap.put("gBio", ""+bio);
        hashMap.put("gLink", ""+link);
        hashMap.put("gIcon", ""+downloadUri);
        hashMap.put("timestamp", ""+timeStamp);
        hashMap.put("createdBy", ""+mAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("id", mAuth.getUid());
                    hashMap1.put("role","creator");
                    hashMap.put("timestamp", timeStamp);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(timeStamp).child("Participants").child(Objects.requireNonNull(mAuth.getUid()))
                            .setValue(hashMap1)
                            .addOnSuccessListener(aVoid1 -> {
                                binding.pb.setVisibility(View.GONE);
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text("Group created")
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                                new Handler().postDelayed(() -> onBackPressed(), 1000);
                            }).addOnFailureListener(e -> {
                                binding.pb.setVisibility(View.GONE);
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text(e.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }

                    );

                }).addOnFailureListener(e ->{
                    binding.pb.setVisibility(View.GONE);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text(e.getMessage())
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000)
                            .gravity(0)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                }

        );

    }

    private void createNoImageGroup(String timeStamp, String name, String username, String bio, String link) {
        binding.pb.setVisibility(View.VISIBLE);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+timeStamp);
        hashMap.put("gName", ""+name);
        hashMap.put("gUsername", ""+username);
        hashMap.put("gBio", ""+bio);
        hashMap.put("gLink", ""+link);
        hashMap.put("gIcon", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/d-group.jpg?alt=media&token=bfaaa505-1c06-4b2f-bc58-8b82b45a8877");
        hashMap.put("timestamp", ""+timeStamp);
        hashMap.put("createdBy", ""+mAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("id", mAuth.getUid());
                    hashMap1.put("role","creator");
                    hashMap.put("timestamp", timeStamp);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(timeStamp).child("Participants").child(Objects.requireNonNull(mAuth.getUid()))
                            .setValue(hashMap1)
                            .addOnSuccessListener(aVoid1 -> {
                                binding.pb.setVisibility(View.GONE);
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text("Group created")
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                                new Handler().postDelayed(() -> onBackPressed(), 1000);
                            }).addOnFailureListener(e -> {
                                binding.pb.setVisibility(View.GONE);
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text(e.getMessage())
                                        .textColor(Color.WHITE)
                                        .textBold()
                                        .length(2000)
                                        .gravity(0)
                                        .solidBackground()
                                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }
                    );

                }).addOnFailureListener(e ->{
                    binding.pb.setVisibility(View.GONE);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text(e.getMessage())
                            .textColor(Color.WHITE)
                            .textBold()
                            .length(2000)
                            .gravity(0)
                            .solidBackground()
                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Storage permission is required")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .gravity(0)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(image_uri).into(binding.profileImage);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}