package com.phigital.ai.Activity;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterArticle;
import com.phigital.ai.Adapter.AdapterPoll;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelArticle;
import com.phigital.ai.Model.ModelPoll;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.SinglesearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdditionalSearch extends BaseActivity {

    SinglesearchBinding binding;
    String title;
    //People
    AdapterPost adapterPost;
    List<ModelPost> postList;
    AdapterArticle adapterPost2;
    List<ModelArticle> postList2;
    AdapterPoll adapterPost4;
    List<ModelPoll> postList4;
//    AdapterShoot adapterPost5;
//    List<ModelShoot> postList5;
    String hisUid;
    SharedPref sharedPref;
    String noImage;

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
        hisUid = intent.getStringExtra("id");
        binding.pg.setVisibility(View.VISIBLE);
//        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        binding.users.setHasFixedSize(true);
//        binding.users.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postList2 = new ArrayList<>();

        postList4 = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.users.setLayoutManager(layoutManager);
        binding.imageView3.setOnClickListener(v -> onBackPressed());


        switch (title){
            case "polls":
                getPolls();
                binding.textView11.setText("Polls");
                break;
            case "feels":
                getFeels();
                binding.textView11.setText("Posts");
                break;
            case "articles":
                getArticles();
                binding.textView11.setText("Articles");
                break;
            case "shoots":
                getShoots();
                binding.textView11.setText("Shoots");
                break;
            case "videos":
                getVideos();
                binding.textView11.setText("Videos");
                break;
            case "photos":
                getPhotos();
                binding.textView11.setText("Photos");
                break;
            case "friends":
                getFriends();
                binding.textView11.setText("Friends");
                break;
            case "rejoys":
                getRejoy();
                binding.textView11.setText("Rejoy");
                break;
        }

    }

    private void getFriends() {

    }

    private void getPhotos() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(modelPost.getType().contains("meme") || modelPost.getType().contains("image")) {
                        if (modelPost.getReId().equals("")) {
                          postList.add(modelPost); }
                    }
                    adapterPost = new AdapterPost(AdditionalSearch.this, postList);
                    binding.users.setAdapter(adapterPost);
                    binding.pg.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getVideos() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(Objects.requireNonNull(modelPost).getType().contains("Video")) {
                        postList.add(modelPost);
                    }
                    adapterPost = new AdapterPost(AdditionalSearch.this, postList);
                    binding.users.setAdapter(adapterPost);
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getShoots() {

    }

    private void getArticles() {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Article");
            Query query = ref.orderByChild("id").equalTo(hisUid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList2.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelArticle modelPost = ds.getValue(ModelArticle.class);
                        postList2.add(modelPost);
                        adapterPost2 = new AdapterArticle(AdditionalSearch.this, postList2);
                        binding.users.setAdapter(adapterPost2);
                        binding.pg.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private void getFeels() {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("id").equalTo(hisUid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        if(modelPost.getType().contains("text")&&modelPost.getReId().equals("")) {
                            postList.add(modelPost);
                        }
                        adapterPost = new AdapterPost(AdditionalSearch.this, postList);
                        binding.users.setAdapter(adapterPost);
                        binding.pg.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private void getPolls() {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Poll");
            Query query = ref.orderByChild("id").equalTo(hisUid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList4.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelPoll modelPost = ds.getValue(ModelPoll.class);
                        postList4.add(modelPost);
                        adapterPost4 = new AdapterPoll(AdditionalSearch.this, postList4);
                        binding.users.setAdapter(adapterPost4);
                        binding.pg.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }
    private void getRejoy() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (modelPost.getId().equals(hisUid)&&!modelPost.getReId().isEmpty()){
                        postList.add(modelPost);
                    }
                    adapterPost = new AdapterPost(AdditionalSearch.this, postList);
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