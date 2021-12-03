package com.phigital.ai.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.Query;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.Adapter.AdapterShareUsers;
import com.phigital.ai.Model.ModelUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.databinding.ActivityShareBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShareActivity extends BaseActivity {

    ActivityShareBinding binding;

    AdapterShareUsers adapterUsers;
    List<ModelUser> userList;

    private static String postId,type,content;
    SharedPref sharedPref;

    public static String getPostId() {
        return postId;
    }
    public static String getType() {
        return type;
    }
    public static String getContent() {
        return content;
    }

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrenPage = 1;

    public ShareActivity(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        type = intent.getStringExtra("type");
        content = intent.getStringExtra("content");

        binding.pg.setVisibility(View.VISIBLE);
        binding.back.setOnClickListener(v -> onBackPressed());

        binding.usersNsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                getAllUsers();
            }
        });

        binding.usersRv.setLayoutManager(new LinearLayoutManager(ShareActivity.this));
        userList = new ArrayList<>();

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
                    filter(s.toString());
                }else {
                    getAllUsers();
                }

            }
        });
        getAllUsers();

    }

    private void filter(final String query) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query q = databaseReference.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);

                        }
                    }
                    adapterUsers = new AdapterShareUsers(ShareActivity.this, userList);
                    adapterUsers.notifyDataSetChanged();
                    binding.usersRv.setAdapter(adapterUsers);
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query q = databaseReference.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        userList.add(modelUser);

                    }
                    adapterUsers = new AdapterShareUsers(ShareActivity.this, userList);
                    binding.usersRv.setAdapter(adapterUsers);
                    binding.pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
