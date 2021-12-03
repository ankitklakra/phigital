package com.phigital.ai.Job;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.MainActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityWorkDetailsBinding;


import java.util.Objects;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class WorkDetails extends BaseActivity implements View.OnClickListener {

    ActivityWorkDetailsBinding binding;
    SharedPref sharedPref;

    BottomSheetDialog DeleteBottomSheet;
    private String userId, myName, myDp;
    private DatabaseReference mDatabase;
    private FirebaseStorage firebaseStorage;
    String hisId, hisName, pid, hisJobtype, hisDp, hisEducation, hisExpert, hisTime, hisCity,
            hisSalary, hisDescription, hisPid, hisExperiencetime, hisExperiencenum, hisWage,
            hisEmail, hisAddress, hisCv;

    ConstraintLayout delete,reportCL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_details);

        binding.pb.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        pid = intent.getStringExtra("postId");
        String emtype = intent.getStringExtra("emtype");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.downloadcv.setOnClickListener(v -> downloadpdf());

        binding.jobtype.setShowSoftInputOnFocus(false);
        binding.education.setShowSoftInputOnFocus(false);
        binding.expert.setShowSoftInputOnFocus(false);
        binding.city.setShowSoftInputOnFocus(false);
        binding.salary.setShowSoftInputOnFocus(false);
        binding.wage.setShowSoftInputOnFocus(false);
        binding.experiencenum.setShowSoftInputOnFocus(false);
        binding.experiencetime.setShowSoftInputOnFocus(false);
        binding.email.setShowSoftInputOnFocus(false);
        binding.address.setShowSoftInputOnFocus(false);
        binding.description.setShowSoftInputOnFocus(false);

        loadPostInfo();
        load();

    }

    private void load() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Work");
        Query query = ref.orderByChild("pId").equalTo(pid);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    hisId = "" + ds.child("id").getValue();
                    hisName = "" + ds.child("name").getValue();
                    hisDp = "" + ds.child("dp").getValue();
                    hisPid = "" + ds.child("pId").getValue();
                    hisJobtype = "" + ds.child("jobtype").getValue();
                    hisEducation = "" + ds.child("education").getValue();
                    hisExpert = "" + ds.child("expert").getValue();
                    hisCity = "" + ds.child("city").getValue();
                    hisSalary = "" + ds.child("salary").getValue();
                    hisWage = "" + ds.child("wage").getValue();
                    hisExperiencenum = "" + ds.child("experiencenum").getValue();
                    hisExperiencetime = "" + ds.child("experiencetime").getValue();
                    hisEmail = "" + ds.child("email").getValue();
                    hisAddress = "" + ds.child("address").getValue();
                    hisDescription = "" + ds.child("description").getValue();
                    hisCv = "" + ds.child("cv").getValue();
                    hisTime = "" + ds.child("pTime").getValue();


                    long lastTime = Long.parseLong(hisTime);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
//                    binding.time.setText(lastSeenTime);

                    binding.textView11.setText(hisName);
                    //DP
//                    try {Picasso.get().load(hisDp).placeholder(R.drawable.placeholder).into(binding.circleImageView);}
//                    catch (Exception ignored){}

                    binding.jobtype.setText(hisJobtype);
                    binding.education.setText(hisEducation);
                    binding.expert.setText(hisExpert);
                    binding.city.setText(hisCity);
                    binding.salary.setText(hisSalary);
                    binding.wage.setText(hisWage);
                    binding.experiencenum.setText(hisExperiencenum);
                    binding.experiencetime.setText(hisExperiencetime);
                    binding.email.setText(hisEmail);
                    binding.address.setText(hisAddress);
                    binding.description.setText(hisDescription);
                    String url = "http://maps.google.co.in/maps?q=" + hisCity;
                    binding.city.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    });
                    String url2 = "http://maps.google.co.in/maps?q=" + hisAddress;
                    binding.address.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                        startActivity(intent);
                    });
                    binding.email.setOnClickListener(v -> {
                        Intent intent = new Intent (Intent.ACTION_VIEW,Uri.parse("mailto:" + hisEmail));
                        intent.putExtra(Intent.EXTRA_SUBJECT,"");
                        intent.putExtra(Intent.EXTRA_TEXT,"");
                        startActivity(intent);
                    });

                    if (userId.equals(hisId)) {
                        delete.setVisibility(View.VISIBLE);
                        reportCL.setVisibility(View.GONE);
                    }

                    binding.more.setOnClickListener(v -> {
                        DeleteBottomSheet.show();
                    });

                    if (hisCv.equals("nocv")) {
                        binding.downloadcv.setVisibility(View.GONE);
                    } else {
                        binding.downloadcv.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadpdf() {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(hisCv);
        picRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String url = uri.toString();
            downloadFile(WorkDetails.this, "Work", ".pdf", DIRECTORY_DOWNLOADS, url);

        }).addOnFailureListener(e -> {

        });
    }

//    private void addToHisNotification(String hisUid, String pId, String notification){
//        String timestamp = ""+ System.currentTimeMillis();
//        HashMap<Object, String> hashMap = new HashMap<>();
//        hashMap.put("pId", pId);
//        hashMap.put("timestamp", timestamp);
//        hashMap.put("pUid", hisUid);
//        hashMap.put("notification", notification);
//        hashMap.put("sUid", userId);
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
//                .addOnSuccessListener(aVoid -> {
//
//                }).addOnFailureListener(e -> {
//
//                });
//
//    }

    private void loadUserInfo() {
        Query query = FirebaseDatabase.getInstance().getReference("Users");
        query.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    myName = "" + ds.child("name").getValue();
                    myDp = "" + ds.child("photo").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        if (DeleteBottomSheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(WorkDetails.this).inflate(R.layout.delete_bottom_sheet, null);
            delete = view.findViewById(R.id.delete);
            reportCL = view.findViewById(R.id.reportCL);
            delete.setOnClickListener(this);
            reportCL.setOnClickListener(this);
            if (userId.equals(hisId)){
                reportCL.setVisibility(View.GONE);
            }
            DeleteBottomSheet = new BottomSheetDialog(WorkDetails.this);
            DeleteBottomSheet.setContentView(view);
        }
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                DeleteBottomSheet.cancel();
                deletecontent();
                break;
            case R.id.reportCL:
                DeleteBottomSheet.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkDetails.this);
                builder.setTitle("Report");
                builder.setMessage("Are you sure to report this work?");
                builder.setPositiveButton("Report",(dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference().child("WorkReport").child(pid).setValue(true);
                    Toast.makeText(WorkDetails.this, "Work Reported...", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
                break;
        }
    }

    private void deletecontent() {
        Query query = FirebaseDatabase.getInstance().getReference("Work").orderByChild("pId").equalTo(pid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Intent intent = new Intent(WorkDetails.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}