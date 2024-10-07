package com.phigital.ai.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterGroups;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Adapter.AdapterSearchUsers;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelGroups;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivitySearchBinding;
import com.phigital.ai.databinding.ActivitySearchmainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends BaseActivity {

    ActivitySearchmainBinding binding;
    SharedPref sharedPref;
    //User
    AdapterSearchUsers adapterUsers;
    List<ModelUser> modeluserList;
    //PostActivity
    AdapterPost adapterPost;
    List<ModelPost> modelPostsList;
    //Groups
    AdapterGroups adapterGroups;
    List<ModelGroups> modelGroupsList;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private static final int TOTAL_ITEMS_TO_LOAD2 = 20;
    private int mCurrenPage1 = 1;
    private int mCurrenPage2 = 1;
    private int mCurrenPage3 = 1;
    DatabaseReference userRef,postRef,groupRef;
    String userId;
    String type = "user";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searchmain);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        //Back
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
            }
        });

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(SearchActivity.this);
        layoutManager1.setStackFromEnd(true);
        layoutManager1.setReverseLayout(true);
        binding.users.setLayoutManager(layoutManager1);
        //User
        modeluserList = new ArrayList<>();

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(SearchActivity.this);
        layoutManager2.setStackFromEnd(true);
        layoutManager2.setReverseLayout(true);
        binding.post.setLayoutManager(layoutManager2);
        //Post
        modelPostsList = new ArrayList<>();

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(SearchActivity.this);
        layoutManager3.setStackFromEnd(true);
        layoutManager3.setReverseLayout(true);
        binding.groups.setLayoutManager(layoutManager3);
        //Groups
        modelGroupsList = new ArrayList<>();

        //EdiText
        binding.editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                switch (type) {
                    case "user":
                        filterUser(binding.editText.getText().toString());
                        break;
                    case "post":
                        filterPost(binding.editText.getText().toString());
                        break;
                    case "group":
                        filterGroups(binding.editText.getText().toString());
                        break;
                }
                return true;
            }
            return false;
        });

        //TabLayout
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (binding.tabLayout.getSelectedTabPosition() == 0) {
                    binding.users.setVisibility(View.VISIBLE);
                    binding.post.setVisibility(View.GONE);
                    binding.groups.setVisibility(View.GONE);
                    type = "user";
                    getAllUsers();
                }
                else if (binding.tabLayout.getSelectedTabPosition() == 1) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.users.setVisibility(View.GONE);
                    binding.groups.setVisibility(View.GONE);
                    binding.post.setVisibility(View.VISIBLE);
                    type = "post";
                    getAllPost();
                }
                else if (binding.tabLayout.getSelectedTabPosition() == 2) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.users.setVisibility(View.GONE);
                    binding.groups.setVisibility(View.VISIBLE);
                    binding.post.setVisibility(View.GONE);
                    type = "group";
                    getAllGroups();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        binding.groupsly.setOnClickListener(v -> {
//            binding.users.setTextColor(Color.parseColor("#e1e1e1"));
//            binding.groups.setTextColor(Color.parseColor("#00ACED"));
//            binding.post.setTextColor(Color.parseColor("#e1e1e1"));
//            binding.usersNsv.setVisibility(View.GONE);
//            binding.postsNsv.setVisibility(View.GONE);
//            binding.groupsNsv.setVisibility(View.VISIBLE);
//        });
//
//        binding.postly.setOnClickListener(v -> {
//            binding.users.setTextColor(Color.parseColor("#e1e1e1"));
//            binding.groups.setTextColor(Color.parseColor("#e1e1e1"));
//            binding.post.setTextColor(Color.parseColor("#00ACED"));
//            binding.usersNsv.setVisibility(View.GONE);
//            binding.postsNsv.setVisibility(View.VISIBLE);
//            binding.groupsNsv.setVisibility(View.GONE);
//        });
//
//        binding.userly.setOnClickListener(v -> {
//            binding.users.setTextColor(Color.parseColor("#00ACED"));
//            binding.post.setTextColor(Color.parseColor("#e1e1e1"));
//            binding.groups.setTextColor(Color.parseColor("#e1e1e1"));
//            binding.usersNsv.setVisibility(View.VISIBLE);
//            binding.postsNsv.setVisibility(View.GONE);
//            binding.groupsNsv.setVisibility(View.GONE);
//        });

        binding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 200)) {
                switch (type) {
                    case "post":
                        mCurrenPage1++;
                        getAllPost();
                        break;
                    case "group":
                        mCurrenPage2++;
                        getAllGroups();
                        break;
                    case "user":
                        mCurrenPage3++;
                        getAllUsers();
                        break;
                }
            }
        });

//        //User
//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
//        layoutManager1.setStackFromEnd(true);
//        layoutManager1.setReverseLayout(true);
//        binding.usersRv.setLayoutManager(layoutManager1);
//
//
//        //PostActivity
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
//        binding.postsRv.setLayoutManager(layoutManager);
//
//
//        //Groups
//        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
//        layoutManager3.setStackFromEnd(true);
//        layoutManager3.setReverseLayout(true);
//        binding.groupsRv.setLayoutManager(layoutManager3);

//        binding.searchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(s.toString())){
//                    binding.pg.setVisibility(View.VISIBLE);
//                    filterUser(s.toString().toLowerCase());
//                    filterPost(s.toString().toLowerCase());
//                    filterGroups(s.toString().toLowerCase());
//                }else {
//                    getAllUsers();
//                    getAllPost();
//                    getAllGroups();
//                }
//            }
//        });

        //Tag
        if (getIntent().hasExtra("hashTag")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.users.setVisibility(View.GONE);
            binding.groups.setVisibility(View.GONE);
            binding.post.setVisibility(View.VISIBLE);
            type = "post";
            Objects.requireNonNull(binding.tabLayout.getTabAt(1)).select();
            filterPost(getIntent().getStringExtra("hashTag"));
            binding.editText.setText(getIntent().getStringExtra("hashTag"));
        }else{
            getAllUsers();
        }
    }

    private void filterGroups(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                        if (Objects.requireNonNull(modelGroups).getgName().toLowerCase().contains(query.toLowerCase()) || modelGroups.getgUsername().contains(query.toLowerCase())) {
                            modelGroupsList.add(modelGroups);
                        }
                    }
                    runOnUiThread(() -> {
                        adapterGroups = new AdapterGroups(SearchActivity.this, modelGroupsList);
                        binding.groups.setAdapter(adapterGroups);
                        adapterGroups.notifyItemInserted(modelGroupsList.size()-1);
                        binding.progressBar.setVisibility(View.GONE);
                    });
                });

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterPost(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelPostsList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(query.toLowerCase()) ||
                                modelPost.getType().toLowerCase().contains(query.toLowerCase()) ||
                                modelPost.getLocation().toLowerCase().contains(query.toLowerCase())) {
                            modelPostsList.add(modelPost);
                        }
                    }
                    runOnUiThread(() -> {
                        adapterPost = new AdapterPost(SearchActivity.this, modelPostsList);
                        binding.post.setAdapter(adapterPost);
                        adapterPost.notifyItemInserted(modelPostsList.size()-1);
                        binding.progressBar.setVisibility(View.GONE);
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void filterUser(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modeluserList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (!userId.equals(Objects.requireNonNull(modelUser).getId()) && modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            modeluserList.add(modelUser);
                        }
                    }
                    runOnUiThread(() -> {
                        adapterUsers = new AdapterSearchUsers(SearchActivity.this, modeluserList);
                        binding.users.setAdapter(adapterUsers);
                        adapterUsers.notifyItemInserted(modeluserList.size() - 1);
                        binding.progressBar.setVisibility(View.GONE);
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllPost() {
        Query q = postRef.limitToLast(mCurrenPage1 * TOTAL_ITEMS_TO_LOAD);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelPostsList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        modelPostsList.add(modelPost);
                    }
                    Collections.shuffle(modelPostsList);
                    runOnUiThread(() -> {
                        adapterPost = new AdapterPost(SearchActivity.this, modelPostsList);
                        binding.post.setAdapter(adapterPost);
                        adapterPost.notifyItemInserted(modelPostsList.size()-1);
                        if (adapterPost.getItemCount() == 0){
                            binding.progressBar.setVisibility(View.GONE);
                            binding.post.setVisibility(View.GONE);
                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.post.setVisibility(View.VISIBLE);
                            binding.groups.setVisibility(View.GONE);
                            binding.users.setVisibility(View.GONE);
                        }
                    });
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllGroups() {
        Query q = groupRef.limitToLast(mCurrenPage2 * TOTAL_ITEMS_TO_LOAD);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                        modelGroupsList.add(modelGroups);
                    }
                    Collections.shuffle(modelGroupsList);
                    runOnUiThread(() -> {
                        adapterGroups = new AdapterGroups(SearchActivity.this, modelGroupsList);
                        binding.groups.setAdapter(adapterGroups);
                        adapterGroups.notifyItemInserted(modelGroupsList.size()-1);
                        if (adapterGroups.getItemCount() == 0){
                            binding.progressBar.setVisibility(View.GONE);
                            binding.groups.setVisibility(View.GONE);
                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.groups.setVisibility(View.VISIBLE);
                            binding.users.setVisibility(View.GONE);
                            binding.post.setVisibility(View.GONE);
                        }
                    });
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        Query q = userRef.limitToLast(mCurrenPage3 * TOTAL_ITEMS_TO_LOAD2);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modeluserList.clear();
                AsyncTask.execute(() -> {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (!userId.equals(Objects.requireNonNull(modelUser).getId())) {
                            modeluserList.add(modelUser);
                        }
                    }
                    Collections.shuffle(modeluserList);
                    runOnUiThread(() -> {
                        adapterUsers = new AdapterSearchUsers(SearchActivity.this, modeluserList);
                        binding.users.setAdapter(adapterUsers);
                        adapterUsers.notifyItemInserted(modeluserList.size() - 1);
                        if (adapterUsers.getItemCount() == 0) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.users.setVisibility(View.GONE);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.users.setVisibility(View.VISIBLE);
                            binding.post.setVisibility(View.GONE);
                            binding.groups.setVisibility(View.GONE);
                        }
                    });
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}