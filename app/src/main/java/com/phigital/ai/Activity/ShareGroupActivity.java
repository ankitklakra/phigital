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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterChatShareGroups;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelChatListGroups;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityShareBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShareGroupActivity extends BaseActivity {

    ActivityShareBinding binding;

    AdapterChatShareGroups adapterChatShareGroups;
    List<ModelChatListGroups> modelGroupsList;

    private static String postId,type,userId,content;
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

    public ShareGroupActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        type = intent.getStringExtra("type");
        content = intent.getStringExtra("content");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        binding.pg.setVisibility(View.VISIBLE);

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.usersNsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                getMyGroups();
            }
        });

        binding.usersRv.setLayoutManager(new LinearLayoutManager(ShareGroupActivity.this));
        modelGroupsList = new ArrayList<>();

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
                    getMyGroups();
                }

            }
        });

        getMyGroups();

    }

    private void filter(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelChatListGroups modelGroups = ds.getValue(ModelChatListGroups.class);
                        if (Objects.requireNonNull(modelGroups).getgName().toLowerCase().contains(query.toLowerCase()) ||
                                modelGroups.getgUsername().toLowerCase().contains(query.toLowerCase())){
                            modelGroupsList.add(modelGroups);
                           binding.pg.setVisibility(View.GONE);
                        }
                    }
                    adapterChatShareGroups = new AdapterChatShareGroups(ShareGroupActivity.this, modelGroupsList);
                    adapterChatShareGroups.notifyDataSetChanged();
                    binding.usersRv.setAdapter(adapterChatShareGroups);
                    binding.pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMyGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelChatListGroups modelGroups = ds.getValue(ModelChatListGroups.class);
                        modelGroupsList.add(modelGroups);
                    }
                    adapterChatShareGroups = new AdapterChatShareGroups(ShareGroupActivity.this, modelGroupsList);
                    binding.usersRv.setAdapter(adapterChatShareGroups);
                    binding.pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
