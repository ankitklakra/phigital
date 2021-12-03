package com.phigital.ai.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutMe extends BaseActivity implements View.OnClickListener {

    TextView mName,mUsername,mBio,mWork,mLocation,mEdu,mHometown,mBD,mPhone,mEmail,mTalent,mLA,mLI,mFA,mRE,mBG,gender;
    CircleImageView photo;
    ImageView backbtn,more;
    SharedPref sharedPref;

    private DatabaseReference mDatabase;
    String dbImage;
    Context context;
    BottomSheetDialog settingbottomsheetDialog;
    ConstraintLayout privateCL,friendsCL,publicCL;
    RadioButton private2,friends2,public2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        backbtn = findViewById(R.id.imageView3);
        mName = findViewById(R.id.nameet);
        mUsername = findViewById(R.id.knet);
        mBio = findViewById(R.id.bioet);
        mWork = findViewById(R.id.worket);
        mEdu = findViewById(R.id.edet);
        mLocation = findViewById(R.id.currentcity);
        mHometown = findViewById(R.id.hometown);
        mBD = findViewById(R.id.birthdate);
        mPhone = findViewById(R.id.number);
        mEmail = findViewById(R.id.email);
        mTalent = findViewById(R.id.talent);
        mLA = findViewById(R.id.language);
        mLI = findViewById(R.id.likes);
        mFA = findViewById(R.id.fambers);
        mRE = findViewById(R.id.relation);
        mBG = findViewById(R.id.bloodgroup);
        gender = findViewById(R.id.gender);
        photo = findViewById(R.id.profile_image);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                mUsername.setText(username);
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                mName.setText(name);
                String bio = Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString();
                mBio.setText(bio);
                String work = Objects.requireNonNull(dataSnapshot.child("job").getValue()).toString();
                mWork.setText(work);
                String education = Objects.requireNonNull(dataSnapshot.child("education").getValue()).toString();
                mEdu.setText(education);
                if (dataSnapshot.hasChild("city")){
                    String dbLocation = dataSnapshot.child("city").getValue().toString();
                    mLocation.setText(dbLocation);
                    String url ="http://maps.google.co.in/maps?q=" + dbLocation;
                    mLocation.setOnClickListener(v -> {
                        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    });
                }
                String homecity = Objects.requireNonNull(dataSnapshot.child("hometown").getValue()).toString();
                mHometown.setText(homecity);
                String bd = Objects.requireNonNull(dataSnapshot.child("birthdate").getValue()).toString();
                mBD.setText(bd);
                String ph = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
                mPhone.setText(ph);
                String em = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                mEmail.setText(em);
                String ta = Objects.requireNonNull(dataSnapshot.child("talent").getValue()).toString();
                mTalent.setText(ta);
                String language = Objects.requireNonNull(dataSnapshot.child("language").getValue()).toString();
                mLA.setText(language);
                String likes = Objects.requireNonNull(dataSnapshot.child("likes").getValue()).toString();
                mLI.setText(likes);
                String fambers = Objects.requireNonNull(dataSnapshot.child("fambers").getValue()).toString();
                mFA.setText(fambers);
                String relation = Objects.requireNonNull(dataSnapshot.child("relation").getValue()).toString();
                mRE.setText(relation);
                String bloodgroup = Objects.requireNonNull(dataSnapshot.child("bloodgroup").getValue()).toString();
                mBG.setText(bloodgroup);
                String genders = Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString();
                gender.setText(genders);
                dbImage = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                try {
                    Picasso.get().load(dbImage).into(photo);
//                    pb.setVisibility(View.GONE);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.placeholder).into(photo);
//                    pb.setVisibility(View.GONE);
                }
                photo.setOnClickListener(v -> {
                            Intent intent = new Intent(AboutMe.this, MediaView.class);
                            intent.putExtra("type","image");
                            intent.putExtra("uri",dbImage);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            AboutMe.this.startActivity(intent);
                        }
                );
                String data = Objects.requireNonNull(dataSnapshot.child("privacy").getValue()).toString();

                switch (data) {
                    case "private":
                        private2.setChecked(true);
                        friends2.setChecked(false);
                        public2.setChecked(false);
                        break;
                    case "friends":
                        private2.setChecked(false);
                        friends2.setChecked(true);
                        public2.setChecked(false);
                        break;
                    case "public":
                        private2.setChecked(false);
                        friends2.setChecked(false);
                        public2.setChecked(true);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AboutMe.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
//                pb.setVisibility(View.GONE);

            }
        });

        backbtn.setOnClickListener(view -> onBackPressed());
        more =findViewById(R.id.more);

        more.setOnClickListener(v -> {
            settingbottomsheetDialog.show();
        });
       loadb();
    }

    private void loadb() {
        if (settingbottomsheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(AboutMe.this).inflate(R.layout.setting_bottom_sheet, null);
            privateCL = view.findViewById(R.id.privateCL);
            friendsCL = view.findViewById(R.id.friendsCL);
            publicCL = view.findViewById(R.id.publicCL);
            private2 = view.findViewById(R.id.private2);
            friends2 = view.findViewById(R.id.friends2);
            public2 = view.findViewById(R.id.public2);
            privateCL.setOnClickListener(this);
            friendsCL.setOnClickListener(this);
            publicCL.setOnClickListener(this);
            private2.setOnClickListener(this);
            friends2.setOnClickListener(this);
            public2.setOnClickListener(this);
            settingbottomsheetDialog = new BottomSheetDialog(AboutMe.this);
            settingbottomsheetDialog.setContentView(view);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privateCL:
                settingbottomsheetDialog.cancel();
                setPrivate();
                private2.setChecked(true);
                friends2.setChecked(false);
                public2.setChecked(false);
                Toast.makeText(AboutMe.this, "Privacy is set to Private", Toast.LENGTH_SHORT).show();
                break;
            case R.id.friendsCL:
                settingbottomsheetDialog.cancel();
                setfriends();
                private2.setChecked(false);
                friends2.setChecked(true);
                public2.setChecked(false);
                Toast.makeText(AboutMe.this, "Privacy is set to Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.publicCL:
                settingbottomsheetDialog.cancel();
                setpublic();
                private2.setChecked(false);
                friends2.setChecked(false);
                public2.setChecked(true);
                Toast.makeText(AboutMe.this, "Privacy is set to Public", Toast.LENGTH_SHORT).show();
                break;
            case R.id.private2:
                setPrivate();
                private2.setChecked(true);
                friends2.setChecked(false);
                public2.setChecked(false);
                Toast.makeText(AboutMe.this, "Privacy is set to Private", Toast.LENGTH_SHORT).show();
                 break;
            case R.id.friends2:
                setfriends();
                private2.setChecked(false);
                friends2.setChecked(true);
                public2.setChecked(false);
                Toast.makeText(AboutMe.this, "Privacy is set to Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.public2:
                setpublic();
                private2.setChecked(false);
                friends2.setChecked(false);
                public2.setChecked(true);
                Toast.makeText(AboutMe.this, "Privacy is set to Public", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setpublic() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("privacy","public");
                mDatabase.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setfriends() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("privacy","friends");
                mDatabase.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPrivate() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("privacy","private");
                mDatabase.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}