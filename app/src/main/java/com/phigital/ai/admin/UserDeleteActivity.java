package com.phigital.ai.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.Adapter.AdapterDeleteUsers;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityUserListBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDeleteActivity extends BaseActivity {

    ActivityUserListBinding binding;
    //User
    AdapterDeleteUsers adapterUsers;
    List<ModelUser> userList;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        binding.imageView3.setOnClickListener(v -> onBackPressed());

        binding.pb.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.trendingRv.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        getAllUsers();
    }


    private void filterUser(String query) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!userId.equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                            binding.pb.setVisibility(View.GONE);
                        }
                    }
                    adapterUsers = new AdapterDeleteUsers(getApplicationContext(), userList);
                    adapterUsers.notifyDataSetChanged();
                    binding.trendingRv.setAdapter(adapterUsers);
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
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (modelUser != null){
                            if (!userId.equals(Objects.requireNonNull(modelUser).getId()) && !ds.hasChild("id")) {
                                userList.add(modelUser);
                                binding.pb.setVisibility(View.GONE);
                            }
                            adapterUsers = new AdapterDeleteUsers(UserDeleteActivity.this, userList);
                            binding.trendingRv.setAdapter(adapterUsers);
                            adapterUsers.notifyDataSetChanged();
                        }
                    }catch (Exception exception){
                        Toast.makeText(UserDeleteActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }


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