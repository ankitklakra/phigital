package com.phigital.ai.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterChatList;
import com.phigital.ai.Adapter.AdapterGroups;
import com.phigital.ai.Adapter.AdapterUsers;
import com.phigital.ai.Adapter.GoodchatViewPagerAdapter;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Groups.CreateGroup;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelChatList;
import com.phigital.ai.Model.ModelGroups;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;

import com.phigital.ai.Utility.UserProfile;
import com.phigital.ai.databinding.ActivityGoodchatBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatViewPagerActivity extends BaseActivity  {

    ActivityGoodchatBinding binding;
    String userId;
    int pos;

    AdapterGroups adapterGroups;
    AdapterChatList adapterChatList;

    List<ModelGroups> modelGroupsList;
    List<ModelUser> userList;

    BottomSheetDialog bottomDialog;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goodchat);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        (ChatViewPagerActivity.this).setSupportActionBar(binding.toolbar);
        GoodchatViewPagerAdapter adapter = new GoodchatViewPagerAdapter(getSupportFragmentManager());
        binding.viewPagerChat.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPagerChat);

        binding.back.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Countm").getRef().removeValue();
            Intent intent = new Intent(ChatViewPagerActivity.this, MainActivity.class);
            startActivity(intent);
        });
        binding.add.setOnClickListener(v -> {
            Intent intent = new Intent(ChatViewPagerActivity.this, CreateGroup.class);
            startActivity(intent);
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
             @Override
             public void onTabSelected(TabLayout.Tab tab) {
                 pos =tab.getPosition();
             }

             @Override
             public void onTabUnselected(TabLayout.Tab tab) {

             }

             @Override
             public void onTabReselected(TabLayout.Tab tab) {

             }
         });

        binding.searchuserrv.setHasFixedSize(true);
        binding.searchuserrv.setLayoutManager(new LinearLayoutManager(ChatViewPagerActivity.this));
        userList = new ArrayList<>();

        binding.searchnumrv.setHasFixedSize(true);
        binding.searchnumrv.setLayoutManager(new LinearLayoutManager(ChatViewPagerActivity.this));
        modelGroupsList= new ArrayList<>();

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0){
                    binding.relbox1.setVisibility(View.VISIBLE);
                    binding.search.setVisibility(View.GONE);
//                    binding.nsv.setVisibility(View.GONE);
                    binding.searchuserrv.setVisibility(View.VISIBLE);
                    binding.searchnumrv.setVisibility(View.GONE);

                } else if (pos ==1){

                } else if (pos ==2){
                    binding.relbox1.setVisibility(View.VISIBLE);
                    binding.search.setVisibility(View.GONE);
//                    binding.nsv.setVisibility(View.GONE);
                    binding.searchuserrv.setVisibility(View.GONE);
                    binding.searchnumrv.setVisibility(View.VISIBLE);
                }


            }

        });

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.relbox1.setVisibility(View.GONE);
                binding.search.setVisibility(View.VISIBLE);
//                binding.nsv.setVisibility(View.VISIBLE);
                binding.searchuserrv.setVisibility(View.GONE);
                binding.searchnumrv.setVisibility(View.GONE);
            }

        });

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
//                    pg.setVisibility(View.VISIBLE);

                 if (pos ==0) {  filterUser(s.toString());}
                 if (pos ==1) {  }
                 if (pos ==2) {  filterUser2(s.toString());}

                }else {
//                    getAllUsers();
                }

            }
        });

        binding.more.setOnClickListener(v -> bottomDialog.show());

        createBottomDialog();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = ref.orderByChild("id").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String photo = ""+ds.child("photo").getValue();
                    String name = ""+ds.child("name").getValue();

                    binding.name.setText(name);

                    try {
                        Picasso.get().load(photo).into(binding.circleImageView3);
                    }
                    catch (Exception e ){
                        Picasso.get().load(R.drawable.placeholder).into(binding.circleImageView3);
                    }

                    Picasso.get().load(photo).into(binding.circleImageView3);

                    binding.circleImageView3.setOnClickListener(v -> {
                        Intent intent = new Intent(ChatViewPagerActivity.this, UserProfile.class);
                        intent.putExtra("hisUid", userId);
                        startActivity(intent);
                    });

                    binding.name.setOnClickListener(v -> {
                        Intent intent = new Intent(ChatViewPagerActivity.this, UserProfile.class);
                        intent.putExtra("hisUid", userId);
                        startActivity(intent);
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterUser2(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelGroups modelPost = ds.getValue(ModelGroups.class);
                    if (Objects.requireNonNull(modelPost).getgName().toLowerCase().contains(query.toLowerCase())
                            || Objects.requireNonNull(modelPost).getgUsername().contains(query.toLowerCase()))
                            modelGroupsList.add(modelPost);
                    adapterGroups = new AdapterGroups(ChatViewPagerActivity.this, modelGroupsList);
                    adapterGroups.notifyDataSetChanged();
                    binding.searchnumrv.setAdapter(adapterGroups);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createBottomDialog(){
        if ( bottomDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.lineup_bottom_sheet, null);
            ConstraintLayout  starred = view.findViewById(R.id.starred);
            ConstraintLayout  blocklist = view.findViewById(R.id.blocklist);

            starred.setOnClickListener(v -> {
                bottomDialog.cancel();
                Intent intent = new Intent(ChatViewPagerActivity.this, ChatSaveActivity.class);
                startActivity(intent);
            });
            blocklist.setOnClickListener(v -> {
                bottomDialog.cancel();
                Intent intent2 = new Intent(ChatViewPagerActivity.this, BlockListActivity.class);
                startActivity(intent2);
            });

            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);
        }
    }

    private void filterUser(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!userId.equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getPhone().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }
                    adapterChatList = new AdapterChatList(ChatViewPagerActivity.this, userList);
                    binding.searchuserrv.setAdapter(adapterChatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}