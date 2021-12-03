package com.phigital.ai.Upload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityHireBinding;

import java.util.HashMap;
import java.util.Objects;

public class HireActivity extends BaseActivity {
    Context context = HireActivity.this;
    int duration = Toast.LENGTH_SHORT;
    ActivityHireBinding binding;
    Uri pdfuri;
    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;
    String name, dp, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hire);
        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.pb.setVisibility(View.INVISIBLE);
        String[] items = new String[]{
                "Private",
                "Skilled",
                "Assistant",
                "Freelancing",
                "Others"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                HireActivity.this,
                R.layout.dropdown_menu,
                items
        );
        binding.jobtype.setAdapter(adapter);

        String[] items2 = new String[]{
                "Diploma",
                "Certificate",
                "Secondary school",
                "Higher Secondary school",
                "Bachelors/Honors",
                "Masters",
                "Others"
        };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                HireActivity.this,
                R.layout.dropdown_menu,
                items2
        );
        binding.education.setAdapter(adapter2);

        String[] items3 = new String[]{
                "Monthly",
                "Yearly",
                "Daily",
                "One time"
        };
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(
                HireActivity.this,
                R.layout.dropdown_menu,
                items3
        );
        binding.wage.setAdapter(adapter3);

        String[] items4 = new String[]{
                "Months",
                "Years"
        };
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(
                HireActivity.this,
                R.layout.dropdown_menu,
                items4
        );
        binding.experiencetime.setAdapter(adapter4);

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
        binding.cv.setOnClickListener(v -> uploadcv());
        binding.applynow.setOnClickListener(v -> {
            String mjobtype = binding.jobtype.getText().toString().trim();
            String mbussiness = Objects.requireNonNull(binding.businessname.getText()).toString().trim();
            String medu = binding.education.getText().toString().trim();
            String mhire = Objects.requireNonNull(binding.hire.getText()).toString().trim();
            String mexpert = Objects.requireNonNull(binding.expert.getText()).toString().trim();
            String mcity = Objects.requireNonNull(binding.city.getText()).toString().trim();
            String msalary = Objects.requireNonNull(binding.salary.getText()).toString().trim();
            String mwage = binding.wage.getText().toString().trim();
            String mexperiencenum = Objects.requireNonNull(binding.experiencenum.getText()).toString().trim();
            String mexperiencetime = binding.experiencetime.getText().toString().trim();
            String memail = Objects.requireNonNull(binding.email.getText()).toString().trim();
            String maddress = Objects.requireNonNull(binding.address.getText()).toString().trim();
            String mdescription = Objects.requireNonNull(binding.description.getText()).toString().trim();

            if (TextUtils.isEmpty(mjobtype)) {
                Toast.makeText(context, "Enter Job", duration).show();
                return;
            }
            if (TextUtils.isEmpty(medu)) {
                Toast.makeText(context, "Enter Education", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mexpert)) {
                Toast.makeText(context, "Enter Expert", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mcity)) {
                Toast.makeText(context, "Enter City", duration).show();
                return;
            }
            if (TextUtils.isEmpty(msalary)) {
                Toast.makeText(context, "Enter Salary", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mwage)) {
                Toast.makeText(context, "Enter Wage", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mexperiencenum)) {
                Toast.makeText(context, "Enter Experience", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mexperiencetime)) {
                Toast.makeText(context, "Enter Experience Time", duration).show();
                return;
            }
            if (TextUtils.isEmpty(memail)) {
                Toast.makeText(context, "Enter Email", duration).show();
                return;
            }
            if (TextUtils.isEmpty(maddress)) {
                Toast.makeText(context, "Enter Address", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mdescription)) {
                Toast.makeText(context, "Enter Description", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mbussiness)) {
                Toast.makeText(context, "Enter Business Name", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mhire)) {
                Toast.makeText(context, "Enter Hiring number", duration).show();
                return;
            }

            if (pdfuri == null){
                uploadwithoutcv(mbussiness,mhire,mjobtype,medu,mexpert,mcity,msalary,mwage,mexperiencenum,mexperiencetime,memail,maddress,mdescription);
                binding.pb.setVisibility(View.VISIBLE);
            }
            else {
                uploadwithcv(mbussiness,mhire,mjobtype,medu,mexpert,mcity,msalary,mwage,mexperiencenum,mexperiencetime,memail,maddress,mdescription);
                binding.pb.setVisibility(View.VISIBLE);
            }
        });
    }

    private void uploadwithcv(String mbussiness, String mhire, String mjobtype, String medu, String mexpert, String mcity, String msalary, String mwage, String mexperiencenum, String mexperiencetime, String memail, String maddress, String mdescription) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "GetJob/" + "GetJob_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(pdfuri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                    if (uriTask.isSuccessful()) {
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("id", id);
                        hashMap.put("name", name);
                        hashMap.put("dp", dp);
                        hashMap.put("pId", timeStamp);
                        hashMap.put("jobtype", mjobtype);
                        hashMap.put("education", medu);
                        hashMap.put("business", mbussiness);
                        hashMap.put("hire", mhire);
                        hashMap.put("expert", mexpert);
                        hashMap.put("city", mcity);
                        hashMap.put("salary", msalary);
                        hashMap.put("wage", mwage);
                        hashMap.put("experiencenum", mexperiencenum);
                        hashMap.put("experiencetime", mexperiencetime);
                        hashMap.put("email", memail);
                        hashMap.put("address", maddress);
                        hashMap.put("description", mdescription);
                        hashMap.put("cv", downloadUri);
                        hashMap.put("pTime", timeStamp);
                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Hire");
                        dRef.child(timeStamp).setValue(hashMap)
                                .addOnSuccessListener(aVoid -> {
                                    binding.pb.setVisibility(View.GONE);
                                    Toast.makeText(context, "Uploaded", duration).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    binding.pb.setVisibility(View.GONE);
                                    Toast.makeText(context,  e.getMessage(), duration).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    binding.pb.setVisibility(View.GONE);
                    Toast.makeText(context, e.getMessage(), duration).show();
                });
    }

    private void uploadwithoutcv(String mbussiness, String mhire, String mjobtype, String medu, String mexpert, String mcity, String msalary, String mwage, String mexperiencenum, String mexperiencetime, String memail, String maddress, String mdescription) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("name", name);
        hashMap.put("dp", dp);
        hashMap.put("pId", timeStamp);
        hashMap.put("business", mbussiness);
        hashMap.put("hire", mhire);
        hashMap.put("jobtype", mjobtype);
        hashMap.put("education", medu);
        hashMap.put("expert", mexpert);
        hashMap.put("city", mcity);
        hashMap.put("salary", msalary);
        hashMap.put("wage", mwage);
        hashMap.put("experiencenum", mexperiencenum);
        hashMap.put("experiencetime", mexperiencetime);
        hashMap.put("email", memail);
        hashMap.put("address", maddress);
        hashMap.put("description", mdescription);
        hashMap.put("cv","nocv");
        hashMap.put("pTime", timeStamp);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Hire");
        dRef.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    binding.pb.setVisibility(View.GONE);
                    Toast.makeText(context, "Uploaded", duration).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.pb.setVisibility(View.GONE);
                    Toast.makeText(context,  e.getMessage(), duration).show();
                });
    }

    private void uploadcv() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"), 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1   && data != null && data.getData() != null){
            pdfuri = data.getData();
        }
    }
}