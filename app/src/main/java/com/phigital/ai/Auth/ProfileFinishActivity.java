package com.phigital.ai.Auth;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.Check;
import com.phigital.ai.MainActivity;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityUsernameBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class ProfileFinishActivity extends AppCompatActivity{

    ActivityUsernameBinding binding;

    private Uri image_uri;
    private int mYear;
    private int mMonth;
    private int mDay;
   File compressedImageFile;
    private static final int PERMISSION_CODE = 1001;
    public final static String USERNAME_PATTERN = "^[a-z0-9_]{3,15}$";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_username);

        binding.uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.mBirthdate.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.mBirthdate.getRight() - binding.mBirthdate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    showDatePickerDialog();
                    return true;
                }
            }
            return false;
        });

//        binding.userName.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (!TextUtils.isEmpty(s.toString())) {
//                        checkusername(s.toString());
//                    }
//                }
//            });

        binding.next.setOnClickListener(v -> {
            binding.progressbar.setVisibility(View.VISIBLE);
            String name = Objects.requireNonNull(binding.fullName.getText()).toString().trim();
            String work = Objects.requireNonNull(binding.mJob.getText()).toString().trim();
            String talent = Objects.requireNonNull(binding.mTalent.getText()).toString().trim();
            String city = Objects.requireNonNull(binding.mcity.getText()).toString().trim();
            String birthdate = Objects.requireNonNull(binding.mBirthdate.getText()).toString().trim();
            String gender = binding.male.isChecked() ? "Male" : "Female";

            if (name.isEmpty()) {
                Snackbar.make(v,"Enter your Name", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else if (work.isEmpty()) {
                Snackbar.make(v,"Enter your Work", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else if (talent.isEmpty()) {
                Snackbar.make(v,"Enter your Talent", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else if (city.isEmpty()) {
                Snackbar.make(v,"Enter your City", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else if (birthdate.isEmpty()) {
                Snackbar.make(v,"Enter your Birthdate", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else if (compressedImageFile == null) {
                Snackbar.make(v,"Upload Photo", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else{
                savedata(name,work,talent,city,birthdate,gender);
            }
         });
    }

    private void savedata(String name, String work, String talent, String city, String birthdate, String gender) {
        String filePathName = "profile_images/" + "" + FirebaseAuth.getInstance().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
        storageReference.putFile(Uri.fromFile(compressedImageFile)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadImageUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("photo", downloadImageUri.toString());
                hashMap.put("name", name);
                hashMap.put("job", work);
                hashMap.put("city", city);
                hashMap.put("talent", talent);
                hashMap.put("birthdate", birthdate);
                hashMap.put("gender", gender);
                FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> {
                    binding.progressbar.setVisibility(View.GONE);
                    Snackbar.make(binding.main, "Uploaded Successfully", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(ProfileFinishActivity.this, Check.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> {
                    binding.progressbar.setVisibility(View.GONE);
                    Snackbar.make(binding.main, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

//    private void checkusername(String username) {
//        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(username);
//        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getChildrenCount() > 0) {
//                    progressbar.setVisibility(View.INVISIBLE);
//                    binding.tickone.setVisibility(View.GONE);
//                    binding.ticktwo.setVisibility(View.VISIBLE);
//                } else {
//                    if (TextUtils.isEmpty(username) || username.length() < 3) {
//                        binding.tickone.setVisibility(View.GONE);
//                        binding.ticktwo.setVisibility(View.VISIBLE);
//                        progressbar.setVisibility(View.INVISIBLE);
//                    }else {
//                        binding.tickone.setVisibility(View.VISIBLE);
//                        binding.ticktwo.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressbar.setVisibility(View.INVISIBLE);
//                new StyleableToast
//                        .Builder(context)
//                        .text(databaseError.getMessage())
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//            }
//        });
//    }

    private void showDatePickerDialog() {

        final Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mMonth = c.get(Calendar.MONTH);
        mYear = c.get(Calendar.YEAR);
        @SuppressLint("SetTextI18n")
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileFinishActivity.this,
                (view, year, monthOfYear, dayOfMonth) ->
                        binding.mBirthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mDay, mMonth, mYear)
                ;

        datePickerDialog.show();
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
        }
    }

    //upload image from camera or gallery
    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorPrimary))
                .setActivityTitle("Profile Photo")
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                image_uri = result.getUri();
                binding.profileimage.setImageURI(image_uri);
                try {
                    compressedImageFile = new Compressor(this).compressToFile(new File(image_uri.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.profileimage.setVisibility(View.VISIBLE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}