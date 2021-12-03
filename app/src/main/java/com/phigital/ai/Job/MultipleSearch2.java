package com.phigital.ai.Job;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterWork;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelWork;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.Multiplesearch2Binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultipleSearch2 extends BaseActivity {
    DatabaseReference postnumref;
    Multiplesearch2Binding binding;
    String title;
    AdapterWork adapterPost;
    AdapterWork adapterPost2;
    AdapterWork adapterPost3;
    AdapterWork adapterPost4;
    AdapterWork adapterPost5;
    AdapterWork adapterPost6;
    List<ModelWork> postList;
    List<ModelWork> postList2;
    List<ModelWork> postList3;
    List<ModelWork> postList4;
    List<ModelWork> postList5;
    List<ModelWork> postList6;
    String userId;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.multiplesearch2);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        binding.pg.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        postnumref = FirebaseDatabase.getInstance().getReference().child("Work");

        binding.freelancingrv.setLayoutManager(new LinearLayoutManager(MultipleSearch2.this,LinearLayoutManager.HORIZONTAL,false));
        binding.othersrv.setLayoutManager(new LinearLayoutManager(MultipleSearch2.this,LinearLayoutManager.HORIZONTAL,false));
        binding.assistantrv.setLayoutManager(new LinearLayoutManager(MultipleSearch2.this,LinearLayoutManager.HORIZONTAL,false));
        binding.skilledrv.setLayoutManager(new LinearLayoutManager(MultipleSearch2.this,LinearLayoutManager.HORIZONTAL,false));
        binding.privaterv.setLayoutManager(new LinearLayoutManager(MultipleSearch2.this,LinearLayoutManager.HORIZONTAL,false));

        postList = new ArrayList<>();
        postList2 = new ArrayList<>();
        postList3 = new ArrayList<>();
        postList4 = new ArrayList<>();
        postList5 = new ArrayList<>();
        postList6 = new ArrayList<>();
        binding.imageView3.setOnClickListener(v -> onBackPressed());
        getPrivate();
        getSkilled();
        getAssistant();
        getFreelancing();
        getOthers();
    }

    private void getOthers() {
        {
            postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelWork modelPost = ds.getValue(ModelWork.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Others")) {
                        postList.add(modelPost);
                        }
                        adapterPost = new AdapterWork(MultipleSearch2.this, postList);
                        binding.othersrv.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void getFreelancing() {
        {
            postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList3.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelWork modelPost = ds.getValue(ModelWork.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Freelancing")) {
                        postList3.add(modelPost);
                        }
                        adapterPost3 = new AdapterWork(MultipleSearch2.this, postList3);
                        binding.freelancingrv.setAdapter(adapterPost3);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void getAssistant() {
        {
            postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList4.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelWork modelPost = ds.getValue(ModelWork.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Assistant")) {
                        postList4.add(modelPost);
                        }
                        adapterPost4 = new AdapterWork(MultipleSearch2.this, postList4);
                        binding.assistantrv.setAdapter(adapterPost4);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void getSkilled() {
        {
            postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList5.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelWork modelPost = ds.getValue(ModelWork.class);
                        if (Objects.requireNonNull(modelPost).getJobtype().equals("Skilled")) {
                            postList5.add(modelPost);
                        }
                        adapterPost5 = new AdapterWork(MultipleSearch2.this, postList5);
                        binding.skilledrv.setAdapter(adapterPost5);
                        binding.pg.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void getPrivate()
    {
        postnumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList6.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelPost = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelPost).getJobtype().equals("Private")) {
                        postList6.add(modelPost);
                    }
                    adapterPost6 = new AdapterWork(MultipleSearch2.this, postList6);
                    binding.privaterv.setAdapter(adapterPost6);
                    binding.pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}