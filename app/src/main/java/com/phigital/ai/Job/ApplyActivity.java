package com.phigital.ai.Job;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.Upload.HireActivity;
import com.phigital.ai.Upload.WorkActivity;
import com.phigital.ai.databinding.FragmentApplyNowBinding;


public class ApplyActivity extends BaseActivity {

    FragmentApplyNowBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_apply_now);
        binding.back.setOnClickListener(v -> onBackPressed());

        binding.hireLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ApplyActivity.this, HireActivity.class);
            startActivity(intent);
        });
        binding.workLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ApplyActivity.this, WorkActivity.class);
            startActivity(intent);
        });
    }
}