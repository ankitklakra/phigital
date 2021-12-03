package com.phigital.ai.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VaultActivity extends BaseActivity {

    RecyclerView recyclerView;
    String userId;
    FirebaseAuth mAuth;
    TextView textView11,textView10;
    ProgressBar pg;
    ImageView imageView3;
    AdapterPost adapterPost;
    List<ModelPost> postList;
    List<String> mySaves;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerView = findViewById(R.id.users);
        imageView3 = findViewById(R.id.imageView3);
        textView10 = findViewById(R.id.textView10);
        textView11 = findViewById(R.id.textView11);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        imageView3.setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(VaultActivity.this,LinearLayoutManager.VERTICAL, false));
        postList = new ArrayList<>();
        adapterPost = new AdapterPost(this, postList);
        recyclerView.setAdapter(adapterPost);
        mySaved();
    }

    private void mySaved() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vault").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    mySaves.add(snapshot1.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelPost post = snapshot1.getValue(ModelPost.class);
                    for (String id: mySaves){
                        if (Objects.requireNonNull(post).getpId().equals(id)){
                            postList.add(post);
                        }
                    }
                }
                textView10.setText(String.valueOf(postList.size()));
                adapterPost.notifyDataSetChanged();
                pg.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}