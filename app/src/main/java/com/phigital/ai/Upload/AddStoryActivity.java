package com.phigital.ai.Upload;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityAddStoryBinding;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

@SuppressWarnings("ALL")
public class AddStoryActivity extends BaseActivity {

    ActivityAddStoryBinding binding;
    ProgressBar pd;
    String myUid;
    String storyId;
    long timeend;
    String mText;
    File compressedImageFile;
    DatabaseReference reference;
    SharedPref sharedPref;
    Uri image_uri;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.Dark);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_story);

        myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        reference = FirebaseDatabase.getInstance().getReference("Story").child(myUid);
        storyId = reference.push().getKey();
        timeend = System.currentTimeMillis()+86400000;
        
        binding.upload.setOnClickListener(v -> {
            uploadImage(image_uri);
        });

        binding.addimage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });

        binding.addimage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });

        binding.back.setOnClickListener(v -> onBackPressed());
    }

    private void uploadImage(Uri compressedImageFile) {
        binding.pd.setVisibility(View.VISIBLE);
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Story/" + "Story_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(compressedImageFile).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageUri", downloadUri);
                hashMap.put("timestart", ServerValue.TIMESTAMP);
                hashMap.put("timeend", timeend);
                hashMap.put("storyid", storyId);
                hashMap.put("userid", myUid);

                reference.child(storyId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddStoryActivity.this, "Story uploaded", Toast.LENGTH_SHORT).show();
                        binding.pd.setVisibility(View.GONE);
                        onBackPressed();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.pd.setVisibility(View.GONE);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
//                Alerter.create(AddStoryActivity.this)
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
//                Alerter.create(AddStoryActivity.this)
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if (data != null) {
                image_uri = Objects.requireNonNull(data).getData();
                binding.imageView.setImageURI(image_uri);
                try {
                    compressedImageFile = new Compressor(this).compressToFile(new File(image_uri.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.imageView.setVisibility(View.VISIBLE);
            }
    }}

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}