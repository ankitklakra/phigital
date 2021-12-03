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
import com.phigital.ai.databinding.ActivityAdminDetailsBinding;

import org.jetbrains.annotations.NotNull;

public class AdminDetails extends BaseActivity {

    ActivityAdminDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_details);
        binding.menu3.setOnClickListener(v -> onBackPressed());

        //Click
        binding.onlinedelete.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), UserDeleteActivity.class);
            startActivity(intent4);
        });

         binding.addcity.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), UserAddCityActivity.class);
            startActivity(intent4);
        });

        binding.onlineuserscheck.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), OnlineUserCheckActivity.class);
            startActivity(intent4);
        });

        binding. postsManage.setOnClickListener(v -> {
            Intent intent4 = new Intent(getApplicationContext(), PostListAdminActivity.class);
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