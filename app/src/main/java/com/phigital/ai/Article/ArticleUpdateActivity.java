package com.phigital.ai.Article;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;

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
import com.phigital.ai.databinding.ActivityArticle2Binding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import gun0912.tedimagepicker.builder.TedImagePicker;
import id.zelory.compressor.Compressor;


public class ArticleUpdateActivity extends BaseActivity {
    ActivityArticle2Binding binding;
    SharedPref sharedPref;

    Uri resulturi;
    File compressedImageFile;
    String type = "none";
    String editText, editMeme, hisImage,editTitle,editCategory;
    String editPostId;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ArticleUpdateActivity.this, R.layout.activity_article2);
        binding.imageView.setOnClickListener(v -> onBackPressed());

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
                ArticleUpdateActivity.this,
                R.layout.dropdown_menu,
                items
        );
        binding.category.setAdapter(adapter);

        binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .gravity(0)
                        .text("Category can't be changed")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

        Intent intent = getIntent();
        String isUpdateKey = ""+intent.getStringExtra("key");
        editPostId = ""+intent.getStringExtra("editPostId");

        if (isUpdateKey.equals("editPost")){
            loadPostData(editPostId);
        }
        binding.addimage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    TedImagePicker.with(ArticleUpdateActivity.this).start(this::showSingleImage);
                }
            }
            else {
                TedImagePicker.with(ArticleUpdateActivity.this).start(this::showSingleImage);
            }
        });

        binding.postIt.setOnClickListener(v -> {
            binding.pb.setVisibility(View.VISIBLE);
            String category = binding.category.getText().toString().trim();
            String title = Objects.requireNonNull(binding.title.getText()).toString().trim();
            String text = Objects.requireNonNull(binding.text.getText()).toString().trim();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(ArticleUpdateActivity.this, "Enter text", Toast.LENGTH_SHORT).show();
                binding.pb.setVisibility(View.GONE);
            }else if (TextUtils.isEmpty(category)) {
                Toast.makeText(ArticleUpdateActivity.this, "Enter Category", Toast.LENGTH_SHORT).show();
                binding.pb.setVisibility(View.GONE);
            }else if (TextUtils.isEmpty(title)) {
                Toast.makeText(ArticleUpdateActivity.this, "Enter Title", Toast.LENGTH_SHORT).show();
                binding.pb.setVisibility(View.GONE);
            } else{
                if (type.equals("image")){
                    uploadMeme(category,title,text);
                }else{
                    uploadText(category,title,text);
                }
            }

        });

    }

    private void uploadText(String category, String title, String text) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("category", category);
        hashMap.put("title", title);
        hashMap.put("text", text);
        FirebaseDatabase.getInstance().getReference("Article").child(editPostId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull @NotNull Void unused) {
                onBackPressed();
                binding.pb.setVisibility(View.GONE);
            }
        });
        Snackbar.make(binding.articleLayout,"Article Updated", Snackbar.LENGTH_LONG).show();

    }

    private void uploadMeme(String category, String title, String text) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Article/" + "Article_" + ""+System.currentTimeMillis());
        storageReference.putFile(Uri.fromFile(compressedImageFile)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("category", category);
                hashMap.put("title", title);
                hashMap.put("text", text);
                hashMap.put("image", downloadUri.toString());
                FirebaseDatabase.getInstance().getReference("Article").child(editPostId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Void unused) {
                        onBackPressed();
                        binding.pb.setVisibility(View.GONE);
                    }
                });
                Snackbar.make(binding.articleLayout,"Article Updated", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSingleImage(Uri uri) {
        Uri destinationfile = Uri.fromFile(new File(ArticleUpdateActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(uri, destinationfile);
        uCrop.withAspectRatio(1,1);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(ArticleUpdateActivity.this);
    };

    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Article");
        Query query = reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    editText = ""+ds.child("text").getValue();
                    editMeme = ""+ds.child("meme").getValue();
                    editTitle = ""+ds.child("title").getValue();
                    editCategory = ""+ds.child("category").getValue();
                    hisImage = ""+ds.child("image").getValue();

                    binding.category.setText(editCategory);
                    binding.text.setText(editText);
                    binding.title.setText(editTitle);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.meme);
                        binding.meme.setVisibility(View.VISIBLE);
                        binding.postIt.setVisibility(View.VISIBLE);
                    }catch (Exception ignored){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
//                Alerter.create(UpdatePost.this)
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
//                Alerter.create(UpdatePost.this)
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
                type = "image";
                binding.articleLayout.setVisibility(View.VISIBLE);
            }
        }else if(resultCode ==UCrop.RESULT_ERROR  ) {
            Toast.makeText(ArticleUpdateActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

        }
    }

}