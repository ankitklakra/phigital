package com.phigital.ai.Upload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.phigital.ai.BaseActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.databinding.ActivityArticleBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import gun0912.tedimagepicker.builder.TedImagePicker;
import id.zelory.compressor.Compressor;

public class ArticleActivity extends BaseActivity {

    Context context = ArticleActivity.this;
    File compressedImageFile;
    int duration = Toast.LENGTH_SHORT;
    ActivityArticleBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;
    StorageReference ref;
    String name, dp, id;

    private static final int PERMISSION_CODE = 1001;

    private static final int CAMERA_PERMISSION_CODE = 100;

    SharedPref sharedPref;
    Uri resulturi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        id = user.getUid();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article);
        binding.galleryLayout.setVisibility(View.GONE);
        binding.articleLayout.setVisibility(View.VISIBLE);
        ref = FirebaseStorage.getInstance().getReference();
        verifyCameraPermission(ArticleActivity.this);

        binding.imageView.setOnClickListener(v -> {
            Intent nextIntent = new Intent(context, MainActivity.class);
            nextIntent.putExtra("frgtoload", "ProfileFragment");
            startActivity(nextIntent);
        });
        binding.addimage.setShowSoftInputOnFocus(false);
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String[] items = new String[]{
                "Business",
                "Education",
                "Entertainment",
                "Fashion",
                "Food",
                "History",
                "Health",
                "Literature",
                "Media",
                "Politics",
                "Science",
                "Sports",
                "Technology",
                "Others"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ArticleActivity.this,
                R.layout.dropdown_menu,
                items
        );
        binding.category.setAdapter(adapter);

        binding.addimage.setOnClickListener(v -> {
            TedImagePicker.with(ArticleActivity.this).start(this::showSingleImage);
        });

       binding.postIt.setOnClickListener(v -> {
            String category = binding.category.getText().toString().trim();
            String title = Objects.requireNonNull(binding.title.getText()).toString().trim();
            String text = Objects.requireNonNull(binding.text.getText()).toString().trim();

           if (TextUtils.isEmpty(category)) {
               Toast.makeText(context, "Enter Category", duration).show();
               return;
           }
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(context, "Enter Title", duration).show();
                return;
            }
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(context, "Enter text", duration).show();
                return;
            }
           if (compressedImageFile == null){
               Toast.makeText(context, "Enter Image ", duration).show();
           }
            else {
                uploadData(category,title,text, compressedImageFile);
                binding.pb.setVisibility(View.VISIBLE);
            }

        });

       binding.meme.setOnClickListener(v -> {
           TedImagePicker.with(ArticleActivity.this).start(this::showSingleImage);
       });

       binding.imageView.setOnClickListener(v -> {
           onBackPressed();
       });

    }

    private void showSingleImage(Uri uri) {
        Uri destinationfile = Uri.fromFile(new File(ArticleActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(uri, destinationfile);
        uCrop.withAspectRatio(1,1);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(ArticleActivity.this);
    };

    private void uploadData(String category, String title, String text, File compressedImageFile) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Article/" + "Article_" + timeStamp;
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.fromFile(compressedImageFile))
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                        if (uriTask.isSuccessful()) {
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("pId", timeStamp);
                            hashMap.put("text", text);
                            hashMap.put("pViews", "0");
                            hashMap.put("type", "image");
                            hashMap.put("image", downloadUri);
                            hashMap.put("video", "no");
                            hashMap.put("category", category);
                            hashMap.put("title", title);
                            hashMap.put("pTime", timeStamp);
                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Article");
                            dRef.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(aVoid -> {
                                        binding.pb.setVisibility(View.GONE);
                                        Toast.makeText(context, "Article Uploaded", duration).show();
                                        Intent intent = new Intent(this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        binding.pb.setVisibility(View.GONE);
                                        Toast.makeText(context, e.getMessage(), duration).show();

                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.pb.setVisibility(View.GONE);
                        Toast.makeText(context, e.getMessage(), duration).show();
                    });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Storage permission Allowed", duration).show();
            } else {
                Toast.makeText(context, "Storage permission is required", duration).show();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK && requestCode == UCrop.REQUEST_CROP ){
            if  (data != null) {
                resulturi = UCrop.getOutput(data);
                binding.meme.setVisibility(View.VISIBLE);
                binding.meme.setImageURI(resulturi);
                try {
                    compressedImageFile = new Compressor(this).compressToFile(new File(resulturi.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.galleryLayout.setVisibility(View.GONE);
                binding.articleLayout.setVisibility(View.VISIBLE);
                binding.addimagely.setVisibility(View.GONE);
            }
        }else if(resultCode ==UCrop.RESULT_ERROR ) {
            final Throwable cropError = UCrop.getError(data);

        }

    }

    public static void verifyCameraPermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA}, // Request camera permission
                    CAMERA_PERMISSION_CODE // Define this constant as you did earlier
            );
        }
    }
}
