package com.phigital.ai.Menu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityChangePasswordBinding;

import java.util.Objects;

public class ChangePassword extends BaseActivity {
    ActivityChangePasswordBinding binding;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        binding.imageView3.setOnClickListener(v -> onBackPressed());

        binding.button.setOnClickListener(v -> {
            binding.pb.setVisibility(View.VISIBLE);
            String oldP = binding.currentpassword.getText().toString().trim();
            String newP = binding.newpassword.getText().toString().trim();
            if (TextUtils.isEmpty(oldP)){
                binding.pb.setVisibility(View.GONE);
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }else if (TextUtils.isEmpty(newP)){
                binding.pb.setVisibility(View.GONE);
                Toast.makeText(this, "Enter new Password", Toast.LENGTH_SHORT).show();
                return;
            }else if (newP.length()<6){
                binding.pb.setVisibility(View.GONE);
                Toast.makeText(this, "Password should have minimum 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            updatePassword(oldP,newP);
        });
    }

    private void updatePassword(String oldP, String newP) {
        binding.pb.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), oldP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updatePassword(newP)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChangePassword.this, "Password has changed", Toast.LENGTH_SHORT).show();
                                binding.pb.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                               binding.pb.setVisibility(View.GONE);
                           }
                       })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                binding.pb.setVisibility(View.GONE);
                            }
                        })
                );
    }
}