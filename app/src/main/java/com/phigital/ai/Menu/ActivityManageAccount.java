package com.phigital.ai.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.admin.VerifyActivity;
import com.phigital.ai.databinding.ActivityManageAccountBinding;


public class ActivityManageAccount extends BaseActivity {

    ActivityManageAccountBinding binding;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_account);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);

        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.cons1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityManageAccount.this, ChangePhone.class);
                startActivity(intent);
            }
        });
        binding.cons2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityManageAccount.this, ChangeEmail.class);
                startActivity(intent);

            }
        });
        binding.cons3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityManageAccount.this, ChangePassword.class);
                startActivity(intent);
            }
        });
        binding.cons4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityManageAccount.this, VerifyActivity.class);
                startActivity(intent);
            }
        });

    }

}
