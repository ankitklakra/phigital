package com.phigital.ai.Job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterHire;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelHire;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.SinglesearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SingleSearch extends BaseActivity {
    DatabaseReference postnumref;
    SinglesearchBinding binding;
    String title;
    //People
    AdapterHire adapterPost;
    List<ModelHire> postList;
    String userId;
    SharedPref sharedPref;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.singlesearch);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        binding.pg.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        postList = new ArrayList<>();
        postnumref = FirebaseDatabase.getInstance().getReference().child("Hire");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.users.setLayoutManager(layoutManager);

        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                switch (title){
                    case "Private":
                        getPrivate();
                        binding.textView11.setText("Private");
                        break;
                    case "Skilled":
                        getSkilled();
                        binding.textView11.setText("Skilled");
                        break;
                    case "Assistant":
                        getAssistant();
                        binding.textView11.setText("Assistant");
                        break;
                    case "Freelancing":
                        getFreelancing();
                        binding.textView11.setText("Freelancing");
                        break;
                    case "Government":
                        getGovernment();
                        binding.textView11.setText("Government");
                        break;
                    case "Others":
                        getOthers();
                        binding.textView11.setText("Others");
                        break;
                }
            }
        });
        binding.imageView3.setOnClickListener(v -> onBackPressed());


        switch (title){
            case "Private":
                getPrivate();
                binding.textView11.setText("Private");
                break;
            case "Skilled":
                getSkilled();
                binding.textView11.setText("Skilled");
                break;
            case "Assistant":
                getAssistant();
                binding.textView11.setText("Assistant");
                break;
            case "Freelancing":
                getFreelancing();
                binding.textView11.setText("Freelancing");
                break;
            case "Government":
                getGovernment();
                binding.textView11.setText("Government");
                break;
            case "Others":
                getOthers();
                binding.textView11.setText("Others");
                break;
        }

    }

    private void getOthers() {
            postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelHire modelPost = ds.getValue(ModelHire.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Others")){
                            postList.add(modelPost);
                        }
                        adapterPost = new AdapterHire(SingleSearch.this, postList);
                        binding.users.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private void getGovernment() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelHire modelPost = ds.getValue(ModelHire.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Government")){
                            postList.add(modelPost);
                        }
                        adapterPost = new AdapterHire(SingleSearch.this, postList);
                        binding.users.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void getFreelancing() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelHire modelPost = ds.getValue(ModelHire.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Freelancing")){
                            postList.add(modelPost);
                        }
                        adapterPost = new AdapterHire(SingleSearch.this, postList);
                        binding.users.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void getAssistant() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelHire modelPost = ds.getValue(ModelHire.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Assistant")){
                            postList.add(modelPost);
                        }
                        adapterPost = new AdapterHire(SingleSearch.this, postList);
                        binding.users.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void getSkilled() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelHire modelPost = ds.getValue(ModelHire.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Skilled")){
                            postList.add(modelPost);
                        }
                        adapterPost = new AdapterHire(SingleSearch.this, postList);
                        binding.users.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void getPrivate() {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelHire modelPost = ds.getValue(ModelHire.class);
                    if (Objects.requireNonNull(modelPost).getJobtype().equals("Private")){
                        postList.add(modelPost);
                    }
                    adapterPost = new AdapterHire(SingleSearch.this, postList);
                    binding.users.setAdapter(adapterPost);
                    binding.pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}