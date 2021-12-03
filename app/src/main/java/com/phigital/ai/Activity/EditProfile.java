package com.phigital.ai.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.Upload.HireActivity;
import com.phigital.ai.databinding.ActivityEditProfileBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.HashMap;
import java.util.Objects;

public class EditProfile extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    ActivityEditProfileBinding binding;
    Uri image_uri;
    private static final int PERMISSION_CODE = 1001;
    public final static String USERNAME_PATTERN = "^[a-z_.0-9]{3,15}$";
    SharedPref sharedPref;
    String un,gender;
    BottomSheetDialog lay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.gender.setShowSoftInputOnFocus(false);

        binding.gender.setOnClickListener(v -> lay.show());

        createBottomsheet();
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (user != null){
                    try {
                        Picasso.get().load(user.getPhoto()).placeholder(R.drawable.placeholder).into(binding.profileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    binding.name.setText((user).getName());
                    binding.username.setText((user).getUsername());
                    binding.bio.setText((user).getBio());
                    binding.link.setText((user).getLink());
                    binding.work.setText((user).getJob());
                    binding.education.setText((user).getEducation());
                    binding.currentcity.setText((user).getCity());
                    binding.hometown.setText((user).getHometown());
                    binding.birthdate.setText((user).getBirthdate());
                    binding.language.setText((user).getLanguage());
                    binding.number.setText((user).getPhone());
                    binding.email.setText((user).getEmail());
                    binding.likes.setText((user).getLikes());
                    binding.talent.setText((user).getTalent());
                    binding.fambers.setText((user).getFambers());
                    binding.relation.setText((user).getRelation());
                    binding.bloodgroup.setText((user).getBloodgroup());
                    binding.gender.setText((user).getGender());
                    un = user.getUsername();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.save.setOnClickListener(v -> {
           binding.pb.setVisibility(View.VISIBLE);
           String name = binding.name.getText().toString().trim();
           String bio = binding.bio.getText().toString();
           String username = binding.username.getText().toString().trim().replaceAll("\\s","");
           String link = binding.link.getText().toString();
           String city = binding.currentcity.getText().toString();
           String work = binding.work.getText().toString();
           String education = binding.education.getText().toString();
           String hometown = binding.hometown.getText().toString();
           String birthdate = binding.birthdate.getText().toString();
           String phone = binding.number.getText().toString();
           String email = binding.email.getText().toString();
           String talent = binding.talent.getText().toString();
           String language = binding.language.getText().toString();
           String likes = binding.likes.getText().toString();
           String fambers = binding.fambers.getText().toString();
           String relation = binding.relation.getText().toString();
           String bloodgroup = binding.bloodgroup.getText().toString();
           String gender = binding.gender.getText().toString();

           if (name.isEmpty()){
               Snackbar.make(v,"Enter your Name", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }else if (gender.isEmpty()){
                Snackbar.make(v,"Enter your Gender", Snackbar.LENGTH_LONG).show();
                binding.pb.setVisibility(View.INVISIBLE);
            }
           else if(username.isEmpty() || !username.matches(USERNAME_PATTERN)){
               Snackbar.make(v,"Usernames can only use letters, numbers, underscores and dot.", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }
           else if (work.isEmpty()) {
               Snackbar.make(v,"Enter your Work", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }
           else if (talent.isEmpty()) {
               Snackbar.make(v,"Enter your Talent", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }
           else if (city.isEmpty()) {
               Snackbar.make(v,"Enter your City", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }
           else if (birthdate.isEmpty()) {
               Snackbar.make(v,"Enter your Birthdate", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }
           else if (binding.profileImage.getDrawable() == null) {
               Snackbar.make(v,"Upload Photo", Snackbar.LENGTH_LONG).show();
               binding.pb.setVisibility(View.INVISIBLE);
           }
           else {
               Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(username);
               usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.getChildrenCount()>0){
                           if (un.equals(username)){
                               if (image_uri != null){
                                   String filePathName = "profile_images/" + "" + FirebaseAuth.getInstance().getUid();
                                   StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
                                   storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
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
                                           hashMap.put("bio", bio);
                                           hashMap.put("link", link);
                                           hashMap.put("gender", gender);
                                           hashMap.put("education", education);
                                           hashMap.put("hometown", hometown);
                                           hashMap.put("language", language);
                                           hashMap.put("likes", likes);
                                           hashMap.put("fambers", fambers);
                                           hashMap.put("relation", relation);
                                           hashMap.put("bloodgroup", bloodgroup);
                                           hashMap.put("username", username);
                                           FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap)
                                                   .addOnSuccessListener(aVoid -> {
                                                       binding.pb.setVisibility(View.GONE);
                                                       Snackbar.make(binding.main, "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
                                                   }).addOnFailureListener(e -> {
                                               binding.pb.setVisibility(View.GONE);
                                               Snackbar.make(binding.main, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                                           });
                                       }});
                               }
                               else {
                                   HashMap<String, Object> hashMap = new HashMap<>();
                                   hashMap.put("name", name);
                                   hashMap.put("job", work);
                                   hashMap.put("city", city);
                                   hashMap.put("talent", talent);
                                   hashMap.put("birthdate", birthdate);
                                   hashMap.put("bio", bio);
                                   hashMap.put("link", link);
                                   hashMap.put("gender", gender);
                                   hashMap.put("education", education);
                                   hashMap.put("hometown", hometown);
                                   hashMap.put("language", language);
                                   hashMap.put("likes", likes);
                                   hashMap.put("fambers", fambers);
                                   hashMap.put("relation", relation);
                                   hashMap.put("bloodgroup", bloodgroup);
                                   hashMap.put("username", username);
                                   FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap)
                                           .addOnSuccessListener(aVoid -> {
                                               binding.pb.setVisibility(View.GONE);
                                               Snackbar.make(binding.main, "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
                                           }).addOnFailureListener(e -> {
                                       binding.pb.setVisibility(View.GONE);
                                       Snackbar.make(binding.main, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                                   });
                               }
                           }else{
                               Snackbar.make(v,"Username already exist, try with new one", Snackbar.LENGTH_LONG).show();
                               binding.pb.setVisibility(View.INVISIBLE);
                           }
                       }else {
                           if (image_uri != null){
                               String filePathName = "profile_images/" + "" + FirebaseAuth.getInstance().getUid();
                               StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
                               storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
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
                                       hashMap.put("bio", bio);
                                       hashMap.put("link", link);
                                       hashMap.put("gender", gender);
                                       hashMap.put("education", education);
                                       hashMap.put("hometown", hometown);
                                       hashMap.put("language", language);
                                       hashMap.put("likes", likes);
                                       hashMap.put("fambers", fambers);
                                       hashMap.put("relation", relation);
                                       hashMap.put("bloodgroup", bloodgroup);
                                       hashMap.put("username", username);
                                       FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap)
                                               .addOnSuccessListener(aVoid -> {
                                                   binding.pb.setVisibility(View.GONE);
                                                   Snackbar.make(binding.main, "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
                                               }).addOnFailureListener(e -> {
                                           binding.pb.setVisibility(View.GONE);
                                           Snackbar.make(binding.main, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                                       });
                                   }});
                           }
                           else {
                               HashMap<String, Object> hashMap = new HashMap<>();
                               hashMap.put("name", name);
                               hashMap.put("job", work);
                               hashMap.put("city", city);
                               hashMap.put("talent", talent);
                               hashMap.put("birthdate", birthdate);
                               hashMap.put("bio", bio);
                               hashMap.put("gender", gender);
                               hashMap.put("link", link);
                               hashMap.put("education", education);
                               hashMap.put("hometown", hometown);
                               hashMap.put("language", language);
                               hashMap.put("likes", likes);
                               hashMap.put("fambers", fambers);
                               hashMap.put("relation", relation);
                               hashMap.put("bloodgroup", bloodgroup);
                               hashMap.put("username", username);
                               FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(hashMap)
                                       .addOnSuccessListener(aVoid -> {
                                           binding.pb.setVisibility(View.GONE);
                                           Snackbar.make(binding.main, "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();
                                       }).addOnFailureListener(e -> {
                                   binding.pb.setVisibility(View.GONE);
                                   Snackbar.make(binding.main, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                               });
                           }
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       Snackbar.make(v,error.getMessage(), Snackbar.LENGTH_LONG).show();
                   }
               });
           }
        });
        binding.birthdate.setShowSoftInputOnFocus(false);
        binding.birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpinnerDatePickerDialogBuilder()
                        .context(EditProfile.this)
                        .callback(EditProfile.this)
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(2021, 0, 1)
                        .maxDate(2021, 0, 1)
                        .minDate(1900, 0, 1)
                        .build()
                        .show();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void createBottomsheet() {
        if (lay == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(EditProfile.this).inflate(R.layout.gender_layout, null);
            ConstraintLayout male = view.findViewById(R.id.male);
            ConstraintLayout female = view.findViewById(R.id.female);
            ConstraintLayout others = view.findViewById(R.id.others);

            male.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.gender.setText("male");
                    lay.dismiss();
                    Toast.makeText(getApplicationContext(), "Male", Toast.LENGTH_SHORT).show();
                }
            });
            female.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.gender.setText("female");
                    lay.dismiss();
                    Toast.makeText(getApplicationContext(), "Female", Toast.LENGTH_SHORT).show();
                }
            });
            others.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.gender.setText("others");
                    lay.dismiss();
                    Toast.makeText(getApplicationContext(), "Others", Toast.LENGTH_SHORT).show();
                }
            });

            lay = new BottomSheetDialog(EditProfile.this);
            lay.setContentView(view);
        }
    }


    private void selectImage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorPrimary))
                .setActivityTitle("Profile Photo")
                .setFixAspectRatio(true)
                .setAspectRatio(1,1)
                .start(this);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                image_uri = result.getUri();
                binding.profileImage.setImageURI(image_uri);
                binding.profileImage.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        binding.birthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
    }
}
