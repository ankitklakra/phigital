package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Adapter.AdapterGroups;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Adapter.AdapterSearchUsers;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelGroups;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivitySearchBinding;
import com.phigital.ai.databinding.ActivitySearchmainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchMainFragment extends Fragment {

    ActivitySearchmainBinding binding;
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivitySearchmainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
        groupRef = FirebaseDatabase.getInstance().getReference("Groups");
        //Back
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), MainActivity.class);
                startActivity(intent1);
            }
        });

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setStackFromEnd(true);
        layoutManager1.setReverseLayout(true);
        binding.users.setLayoutManager(layoutManager1);
        //User
        modeluserList = new ArrayList<>();


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setStackFromEnd(true);
        layoutManager2.setReverseLayout(true);
        binding.post.setLayoutManager(layoutManager2);
        //Post
        modelPostsList = new ArrayList<>();

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
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
                    binding.post.setVisibility(View.GONE);
                    binding.groups.setVisibility(View.VISIBLE);
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


        binding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
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

        if (requireActivity().getIntent().hasExtra("hashTag")){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.users.setVisibility(View.GONE);
            binding.groups.setVisibility(View.GONE);
            binding.post.setVisibility(View.VISIBLE);
            type = "post";
            Objects.requireNonNull(binding.tabLayout.getTabAt(1)).select();
            filterPost(requireActivity().getIntent().getStringExtra("hashTag"));
            binding.editText.setText(requireActivity().getIntent().getStringExtra("hashTag"));
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
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                    if (Objects.requireNonNull(modelGroups).getgName().toLowerCase().contains(query.toLowerCase()) || modelGroups.getgUsername().contains(query.toLowerCase()) ){
                        modelGroupsList.add(modelGroups);
                    }
                    adapterGroups = new AdapterGroups(getActivity(), modelGroupsList);
                    binding.groups.setAdapter(adapterGroups);
                    binding.progressBar.setVisibility(View.GONE);
                }
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
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(query.toLowerCase()) ||
                            modelPost.getType().toLowerCase().contains(query.toLowerCase()) ||
                            modelPost.getLocation().toLowerCase().contains(query.toLowerCase())) {
                        modelPostsList.add(modelPost);
                    }
                    adapterPost = new AdapterPost(getActivity(), modelPostsList);
                    binding.post.setAdapter(adapterPost);
                    binding.progressBar.setVisibility(View.GONE);

                }
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
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!userId.equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                            modelUser.getCity().toLowerCase().contains(query.toLowerCase())){
                            modeluserList.add(modelUser);
                        }
                    }
                    adapterUsers = new AdapterSearchUsers(getActivity(), modeluserList);
                    adapterUsers.notifyDataSetChanged();
                    binding.users.setAdapter(adapterUsers);
                    binding.progressBar.setVisibility(View.GONE);

                }
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
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                    modelGroupsList.add(modelGroups);
                    adapterGroups = new AdapterGroups(getActivity(), modelGroupsList);
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

                }
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
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    modelPostsList.add(modelPost);
                    adapterPost = new AdapterPost(getActivity(), modelPostsList);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        Query q = userRef.limitToFirst(mCurrenPage3 * TOTAL_ITEMS_TO_LOAD2);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modeluserList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!userId.equals(Objects.requireNonNull(modelUser).getId())) {
                        modeluserList.add(modelUser);
                    }
                    Collections.shuffle(modeluserList);
                    adapterUsers = new AdapterSearchUsers(getActivity(), modeluserList);
                    binding.users.setAdapter(adapterUsers);
                    adapterUsers.notifyItemInserted(modeluserList.size()-1);
                    if (adapterUsers.getItemCount() == 0){
                        binding.progressBar.setVisibility(View.GONE);
                        binding.users.setVisibility(View.GONE);
                    }else{
                        binding.progressBar.setVisibility(View.GONE);
                        binding.users.setVisibility(View.VISIBLE);
                        binding.post.setVisibility(View.GONE);
                        binding.groups.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    //Pressed return button - returns to the results menu
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
