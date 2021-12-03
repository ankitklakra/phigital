package com.phigital.ai.Utility;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUser extends BaseActivity implements View.OnClickListener {

    TextView mName,mUsername,mBio,mWork,mLocation,mEdu,mHometown,mBD,mPhone,mEmail,mTalent,mLA,mLI,mFA,mRE,mBG,gender;
    CircleImageView photo;
    ImageView backbtn,more;
    SharedPref sharedPref;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String hisUid;
    String dbImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_user);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        hisUid = getIntent().getStringExtra("hisUid");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(hisUid);

        backbtn = findViewById(R.id.imageView3);
        more = findViewById(R.id.more);
        mName = findViewById(R.id.nameet);
        mUsername = findViewById(R.id.knet);
        mBio = findViewById(R.id.bioet);
        mWork = findViewById(R.id.worket);
        mEdu = findViewById(R.id.edet);
        mLocation = findViewById(R.id.currentcity);
        mHometown = findViewById(R.id.hometown);
        mBD = findViewById(R.id.birthdate);
//        mPhone = findViewById(R.id.number);
//        mEmail = findViewById(R.id.email);
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
//                String ph = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
//                mPhone.setText(ph);
//                String em = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
//                mEmail.setText(em);
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
                    Intent intent = new Intent(AboutUser.this, MediaView.class);
                    intent.putExtra("type","image");
                    intent.putExtra("uri",dbImage);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AboutUser.this.startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AboutUser.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
//                pb.setVisibility(View.GONE);

            }
        });


        backbtn.setOnClickListener(view -> onBackPressed());
        more =findViewById(R.id.more);
//        more.setOnClickListener(v -> {
//            AboutBottomSheet bottom = new AboutBottomSheet();
//            bottom.show(getSupportFragmentManager(),"bottomsheet");
//        });

    }

    @Override
    public void onClick(View v) {

    }
}