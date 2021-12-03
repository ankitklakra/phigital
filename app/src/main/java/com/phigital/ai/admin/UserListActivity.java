package com.phigital.ai.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.Adapter.AdapterUsers;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityUserListBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListActivity extends BaseActivity {

    ActivityUserListBinding binding;
    //User
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    String userId;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        binding.imageView3.setOnClickListener(v -> onBackPressed());

        binding.pb.setVisibility(View.VISIBLE);

        binding.trendingRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.trendingRv.setLayoutManager(layoutManager);

        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterUser(s.toString());
                }else {
                    getAllUsers();
                }
                binding.pb.setVisibility(View.VISIBLE);

            }
        });

        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrentPage++;
                getAllUsers();
            }
        });

        //User
        userList = new ArrayList<>();
        getAllUsers();
    }


    private void filterUser(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (modelUser != null) {
                        if (!userId.equals(Objects.requireNonNull(modelUser).getId())) {
                            if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getUsername().toLowerCase().contains(query.toLowerCase())) {
                                userList.add(modelUser);
                            }
                        }
                    }
                    adapterUsers = new AdapterUsers(getApplicationContext(), userList);
                    binding.trendingRv.setAdapter(adapterUsers);
                    adapterUsers.notifyDataSetChanged();
                    binding.pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .gravity(0)
                        .text(databaseError.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });

    }


    private void getAllUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query q = databaseReference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (modelUser != null) {
                        if (!userId.equals(Objects.requireNonNull(modelUser).getId())) {
                            userList.add(modelUser);
                        }
                    }
                    adapterUsers = new AdapterUsers(getApplicationContext(), userList);
                    binding.trendingRv.setAdapter(adapterUsers);
                    adapterUsers.notifyDataSetChanged();
                    binding.pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(databaseError.getMessage())
                        .textColor(Color.WHITE)
                        .gravity(0)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }
}