package com.phigital.ai.admin;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityAdminBinding;

import org.jetbrains.annotations.NotNull;

public class AdminActivity extends BaseActivity {

    ActivityAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin);
        binding.menu3.setOnClickListener(v -> onBackPressed());
        //Numbers
        Query queryOnline = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("status").equalTo("online");
             queryOnline.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                           String onlinenumber = String.valueOf(snapshot.getChildrenCount());
                           binding.onlineText.setText(onlinenumber);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                String usernumber = String.valueOf(snapshot1.getChildrenCount());
                binding.usersText.setText(usernumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                String postnumber = String.valueOf(snapshot2.getChildrenCount());
                binding.postsText.setText(postnumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                String msgnumber = String.valueOf(snapshot3.getChildrenCount());
                binding.msgText.setText(msgnumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Click
        binding.usersManagecheck.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), AdminDetails.class);
            startActivity(intent4);
        });
         binding.usersManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), UserListActivity.class);
            startActivity(intent4);
        });

        binding.onlineusersManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), OnlineUserListActivity.class);
            startActivity(intent4);
        });

        binding. postsManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), PostListActivity.class);
            startActivity(intent4);
        });

        binding. verificationManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), PendingVerificationActivity.class);
            startActivity(intent4);
        });

        binding. reportedUsers.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), UserReportListActivity.class);
            startActivity(intent4);
        });

        binding. reportedPosts.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), PostReportListActivity.class);
            startActivity(intent4);
        });
        binding. reportedPolls.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), PollReportListActivity.class);
            startActivity(intent4);
        });
        binding. reportedArticle.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), ArticleReportListActivity.class);
            startActivity(intent4);
        });
        binding. reportedHire.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), HireReportListActivity.class);
            startActivity(intent4);
        });
        binding. reportedWork.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), WorkReportListActivity.class);
            startActivity(intent4);
        });
        binding. reportedComment.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), CommentReportListActivity.class);
            startActivity(intent4);
        });
        binding. reportedGroup.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), GroupReportListActivity.class);
            startActivity(intent4);
        });
        binding. warnUsers.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), WarnUserActivity.class);
            startActivity(intent4);
        });
        binding. warnGroups.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), WarnGroupActivity.class);
            startActivity(intent4);
        });
    }
}