package com.phigital.ai.Upload;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


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
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityPollBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import gun0912.tedimagepicker.builder.TedImagePicker;
import id.zelory.compressor.Compressor;

public class PollActivity extends BaseActivity {

    Context context = PollActivity.this;

    int duration = Toast.LENGTH_SHORT;
    ActivityPollBinding binding;
    ProgressBar pd;
    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;
    StorageReference ref;
    String name, dp, id;
    Uri resulturi;
    Uri resulturi2;
    Uri resulturi3;
    Uri resulturi4;
    File compressedImageFile,compressedImageFile2,compressedImageFile3,compressedImageFile4;
    private static final int Hour = 3600000;
    private static final int Day = 86400000;
    private static final int Minute = 60000;
    private static final int IMAGE_PICK_CODE1 = 1000;
    private static final int IMAGE_PICK_CODE2 = 2000;
    private static final int IMAGE_PICK_CODE3 = 3000;
    private static final int IMAGE_PICK_CODE4 = 4000;

    private static final int PERMISSION_CODE = 1001;
    private static final int NUM_ONE = 1;
    private static final int NUM_TWO = 2;
    private static final int NUM_THREE = 3;
    private static final int NUM_FOUR = 4;
    String mPath;
    ArrayList<String> listPaths,dirPaths,directoryNames,listofImages;
    final String append = "file:/";
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_poll);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        id = user.getUid();
        dRef = FirebaseDatabase.getInstance().getReference().child("Poll");
        ref = FirebaseStorage.getInstance().getReference();

        binding.imageView.setOnClickListener(v -> onBackPressed());

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.a1.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop1);
        });

        binding.b2.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop2);
        });

        binding.c3.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop3);
        });

        binding.d4.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop4);
        });

        String[] items = new String[]{
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                PollActivity.this,
                R.layout.dropdown_menu,
                items
        );
        binding.days.setAdapter(adapter);

        String[] items2 = new String[]{
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20",
                "21",
                "22",
                "23"
        };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                PollActivity.this,
                R.layout.dropdown_menu,
                items2
        );
        binding.hours.setAdapter(adapter2);

        String[] items3 = new String[]{
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20",
                "21",
                "22",
                "23",
                "24",
                "25",
                "26",
                "27",
                "28",
                "29",
                "30",
                "31",
                "32",
                "33",
                "34",
                "35",
                "36",
                "37",
                "38",
                "39",
                "40",
                "41",
                "42",
                "43",
                "44",
                "45",
                "46",
                "47",
                "48",
                "49",
                "50",
                "51",
                "52",
                "53",
                "54",
                "55",
                "56",
                "57",
                "58",
                "59"
        };
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(
                PollActivity.this,
                R.layout.dropdown_menu,
                items3
        );
        binding.minutes.setAdapter(adapter3);



        binding.postIt.setOnClickListener(v -> {
            long timeStamp = System.currentTimeMillis();
            String mText = Objects.requireNonNull(binding.titletext.getText()).toString().trim();
            String text1 = Objects.requireNonNull(binding.choice1.getText()).toString().trim();
            String text2 = Objects.requireNonNull(binding.choice2.getText()).toString().trim();
            String text3 = Objects.requireNonNull(binding.choice3.getText()).toString().trim();
            String text4 = Objects.requireNonNull(binding.choice4.getText()).toString().trim();

            String daytv = binding.days.getText().toString().trim();
            String hourtv = binding.hours.getText().toString().trim();
            String mintv = binding.minutes.getText().toString().trim();

            if (daytv.isEmpty()){
                daytv ="0";
            }else{

            }
            if (hourtv.isEmpty()){
                hourtv = "0";
            }else{

            }
            if (mintv.isEmpty()){
                mintv = "0";
            }else{

            }

            int D = Integer.parseInt(daytv);
            int H = Integer.parseInt(hourtv);
            int M = Integer.parseInt(mintv);

            long dates = timeStamp + D * Day;
            long hours = dates + H * Hour;
            long minutes = hours + M * Minute;
            String s = String.valueOf(minutes);

            if (TextUtils.isEmpty(mintv)) {
                Toast.makeText(context, "Please set Poll length", duration).show();
                return;
            }else if (TextUtils.isEmpty(mText)) {
                Toast.makeText(context, "Enter words", duration).show();
                return;
            }else if (TextUtils.isEmpty(text1)) {
                Toast.makeText(context, "Enter words in option1 ", duration).show();
                return;
            }else if (TextUtils.isEmpty(text2)) {
                Toast.makeText(context, "Enter words in option2 ", duration).show();
                return;
            }else if (TextUtils.isEmpty(text3)) {
                Toast.makeText(context, "Enter words in option3 ", duration).show();
                return;
            }else if (TextUtils.isEmpty(text4)) {
                Toast.makeText(context, "Enter words in option4 ", duration).show();
                return;
            }else if (compressedImageFile == null){
                Toast.makeText(context, "Add Image in option1", duration).show();
            }else if (compressedImageFile2 == null){
                Toast.makeText(context, "Add Image in option2", duration).show();
            }else if (compressedImageFile3 == null){
                Toast.makeText(context, "Add Image in option3", duration).show();
            }else if (compressedImageFile4 == null){
                Toast.makeText(context, "Add Image in option4", duration).show();
            } else {
                uploadData(mText,text1,text2,text3,text4,s,compressedImageFile,compressedImageFile2,compressedImageFile3,compressedImageFile4);
                binding.pb.setVisibility(View.VISIBLE);
            }
        });

        binding.ima1.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop1);
        });
        binding.imb2.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop2);
        });
        binding.imc3.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop3);
        });
        binding.imd4.setOnClickListener(v -> {
            TedImagePicker.with(PollActivity.this).backButton(R.drawable.ic_left).start(this::crop4);
        });

    }

    private void crop1(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(PollActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(1,1);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(PollActivity.this,IMAGE_PICK_CODE1);
    }

    private void crop2(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(PollActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(1,1);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(PollActivity.this,IMAGE_PICK_CODE2);
    }

    private void crop3(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(PollActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(1,1);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(PollActivity.this,IMAGE_PICK_CODE3);
    }

    private void crop4(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(PollActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(1,1);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(PollActivity.this,IMAGE_PICK_CODE4);
    }

    private void uploadData(String mText, String text1, String text2, String text3, String text4,String s,File resulturi, File resulturi2, File resulturi3,File resulturi4) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Poll/" + "Poll_" + timeStamp;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploader = storage.getReference().child(filePathAndName + 1);
        uploader.putFile(Uri.fromFile(resulturi))
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    StorageReference uploader2 = storage.getReference().child(filePathAndName + 2);
                    uploader2.putFile(Uri.fromFile(resulturi2))
                            .addOnSuccessListener(taskSnapshot2 -> {
                                Task<Uri> uriTask2 = taskSnapshot2.getStorage().getDownloadUrl();
                                while (!uriTask2.isSuccessful()) ;
                                StorageReference uploader3 = storage.getReference().child(filePathAndName + 3);
                                uploader3.putFile(Uri.fromFile(resulturi3))
                                        .addOnSuccessListener(taskSnapshot3 -> {
                                            Task<Uri> uriTask3 = taskSnapshot3.getStorage().getDownloadUrl();
                                            while (!uriTask3.isSuccessful()) ;
                                            StorageReference uploader4 = storage.getReference().child(filePathAndName + 4);
                                            uploader4.putFile(Uri.fromFile(resulturi4))
                                                    .addOnSuccessListener(taskSnapshot4 -> {
                                                        Task<Uri> uriTask4 = taskSnapshot4.getStorage().getDownloadUrl();
                                                        while (!uriTask4.isSuccessful()) ;
                                                        String image1 = Objects.requireNonNull(uriTask.getResult()).toString();
                                                        String image2 = Objects.requireNonNull(uriTask2.getResult()).toString();
                                                        String image3 = Objects.requireNonNull(uriTask3.getResult()).toString();
                                                        String image4 = Objects.requireNonNull(uriTask4.getResult()).toString();
                                                        if (uriTask.isSuccessful() && uriTask2.isSuccessful() && uriTask3.isSuccessful() && uriTask4.isSuccessful()) {
                                                            HashMap<Object, String> hashMap = new HashMap<>();
                                                            hashMap.put("id", id);
                                                            hashMap.put("pId", timeStamp);
                                                            hashMap.put("titletext", mText);
                                                            hashMap.put("text1", text1);
                                                            hashMap.put("text2", text2);
                                                            hashMap.put("text3", text3);
                                                            hashMap.put("text4", text4);
                                                            hashMap.put("option1", "0");
                                                            hashMap.put("option2", "0");
                                                            hashMap.put("option3", "0");
                                                            hashMap.put("option4", "0");
                                                            hashMap.put("pic1", image1);
                                                            hashMap.put("pic2", image2);
                                                            hashMap.put("pic3", image3);
                                                            hashMap.put("pic4", image4);
                                                            hashMap.put("pTime", timeStamp);
                                                            hashMap.put("timeRemain", s);
                                                            hashMap.put("type", "Poll");
                                                            hashMap.put("pViews", "0");
                                                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Poll");
                                                            dRef.child(timeStamp).setValue(hashMap);
                                                        }
                                                    })
                                                    .addOnSuccessListener(aVoid -> {
                                                        binding.pb.setVisibility(View.GONE);
                                                        Toast.makeText(context, "Poll Uploaded", duration).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        pd.setVisibility(View.GONE);
                                                        Toast.makeText(context, e.getMessage(), duration).show();
                                                    });
                                        });
                            });
                });
    };

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK && requestCode == IMAGE_PICK_CODE1 ){
            if  (data != null) {
                resulturi = UCrop.getOutput(data);
                binding.ima1.setVisibility(View.VISIBLE);
                binding.ima1.setImageURI(resulturi);
                try {
                    compressedImageFile = new Compressor(this).compressToFile(new File(resulturi.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.galleryLayout.setVisibility(View.GONE);
                binding.pollLayout.setVisibility(View.VISIBLE);
                binding.a1.setVisibility(View.GONE);
                }
        }else if(resultCode ==UCrop.RESULT_ERROR ) {
            final Throwable cropError = UCrop.getError(data);
        }
        if(resultCode ==RESULT_OK && requestCode == IMAGE_PICK_CODE2 ){
            if  (data != null) {
                resulturi2 = UCrop.getOutput(data);
                try {
                    compressedImageFile2 = new Compressor(this).compressToFile(new File(resulturi2.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.imb2.setVisibility(View.VISIBLE);
                binding.imb2.setImageURI(resulturi2);
                binding.galleryLayout.setVisibility(View.GONE);
                binding.pollLayout.setVisibility(View.VISIBLE);
                binding.b2.setVisibility(View.GONE);
            }
        }else if(resultCode ==UCrop.RESULT_ERROR ) {
            final Throwable cropError = UCrop.getError(data);
        }
        if(resultCode ==RESULT_OK && requestCode == IMAGE_PICK_CODE3 ){
            if  (data != null)  {
                resulturi3 = UCrop.getOutput(data);
                try {
                    compressedImageFile3 = new Compressor(this).compressToFile(new File(resulturi3.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.imc3.setVisibility(View.VISIBLE);
                binding.imc3.setImageURI(resulturi3);
                binding.galleryLayout.setVisibility(View.GONE);
                binding.pollLayout.setVisibility(View.VISIBLE);
                binding.c3.setVisibility(View.GONE);
            }
        }else if(resultCode ==UCrop.RESULT_ERROR ) {
            final Throwable cropError = UCrop.getError(data);
        }
        if(resultCode ==RESULT_OK && requestCode == IMAGE_PICK_CODE4 ){
            if  (data != null) {
                resulturi4 = UCrop.getOutput(data);
                try {
                    compressedImageFile4 = new Compressor(this).compressToFile(new File(resulturi4.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.imd4.setVisibility(View.VISIBLE);
                binding.imd4.setImageURI(resulturi4);
                binding.galleryLayout.setVisibility(View.GONE);
                binding.pollLayout.setVisibility(View.VISIBLE);
                binding.d4.setVisibility(View.GONE);
            }
        }else if(resultCode ==UCrop.RESULT_ERROR ) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

}