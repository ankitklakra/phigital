package com.phigital.ai.Job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterHire;
import com.phigital.ai.Adapter.AdapterWork;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelHire;
import com.phigital.ai.Model.ModelWork;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityJobsearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JobSearch extends BaseActivity {

    ActivityJobsearchBinding binding;
    SharedPref sharedPref;
    //Job
    AdapterWork adapterUsers;
    List<ModelWork> userList;
    //People
    AdapterHire adapterPost;
    List<ModelHire> postList;
    String postId,userId;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrenPage = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_jobsearch);
        binding.pg.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        if (intent.hasExtra("hashTag")){
            String tag = getIntent().getStringExtra("hashTag");
            binding.searchBar.setText("#"+tag);
        }

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.jobs.setTextColor(Color.parseColor("#00ACED"));

        binding.jobly.setOnClickListener(v -> {
            binding.jobs.setTextColor(Color.parseColor("#00ACED"));
            binding.people.setTextColor(Color.parseColor("#757575"));
            binding.cv2.setVisibility(View.GONE);
            binding.cv.setVisibility(View.VISIBLE);
        });

        binding.peoply.setOnClickListener(v -> {
            binding.jobs.setTextColor(Color.parseColor("#757575"));
            binding.people.setTextColor(Color.parseColor("#00ACED"));
            binding.cv2.setVisibility(View.VISIBLE);
            binding.cv.setVisibility(View.GONE);
        });

        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                getAllPost();
            }
        });

        binding.cv2.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                getAllUsers();
            }
        });

        binding.peoplerv.setLayoutManager(new LinearLayoutManager(JobSearch.this));
        userList = new ArrayList<>();
        getAllUsers();
        //PostActivity

        binding.jobsrv.setLayoutManager(new LinearLayoutManager(JobSearch.this));
        postList = new ArrayList<>();
        getAllPost();


        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    binding.pg.setVisibility(View.VISIBLE);
                    filterUser(s.toString());
                    filterPost(s.toString());
                }else {
                    getAllUsers();
                    getAllPost();
                }

            }
        });
    }

    private void filterPost(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hire");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelHire modelPost = ds.getValue(ModelHire.class);
                    if (Objects.requireNonNull(modelPost).getExpert().toLowerCase().contains(query.toLowerCase()) ||
                            modelPost.getCity().contains(query.toLowerCase())||
                            modelPost.getDescription().contains(query.toLowerCase())||
                            modelPost.getBusiness().contains(query.toLowerCase()))
                        postList.add(modelPost);
                    adapterPost = new AdapterHire(JobSearch.this, postList);
                    adapterPost.notifyDataSetChanged();
                    binding.jobsrv.setAdapter(adapterPost);
                    binding.pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void filterUser(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Work");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getCity().contains(query.toLowerCase()) ||
                            modelUser.getExpert().toLowerCase().contains(query.toLowerCase()) ||
                            modelUser.getDescription().toLowerCase().contains(query.toLowerCase()))
                            userList.add(modelUser);
                    adapterUsers = new AdapterWork(JobSearch.this, userList);
                    adapterUsers.notifyDataSetChanged();
                    binding.peoplerv.setAdapter(adapterUsers);
                    binding.pg.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hire");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelHire modelPost = ds.getValue(ModelHire.class);
                    postList.add(modelPost);
                    adapterPost = new AdapterHire(JobSearch.this, postList);
                    binding.jobsrv.setAdapter(adapterPost);
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Work");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    userList.add(modelUser);
                    adapterUsers = new AdapterWork(JobSearch.this, userList);
                    binding.peoplerv.setAdapter(adapterUsers);
                    binding.pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}