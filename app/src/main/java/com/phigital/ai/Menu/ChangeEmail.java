package com.phigital.ai.Menu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityChangeEmailBinding;

import java.util.Objects;

public class ChangeEmail extends BaseActivity {

    ActivityChangeEmailBinding binding;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_email);

        mAuth = FirebaseAuth.getInstance();

        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.button.setOnClickListener(v -> {
            binding.pb.setVisibility(View.VISIBLE);
            String newE = binding.email.getText().toString().trim();
            String newP = binding.Password.getText().toString().trim();
            if (TextUtils.isEmpty(newE)) {
                binding.pb.setVisibility(View.GONE);
                Toast.makeText(this, "New Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(newP)) {
                binding.pb.setVisibility(View.GONE);
                Toast.makeText(this, "Password", Toast.LENGTH_SHORT).show();
                return;
            }
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(newE);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() > 0) {
                        binding.pb.setVisibility(View.GONE);
                        Toast.makeText(ChangeEmail.this, "Email Already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateEmail(newE, newP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void updateEmail(String newE, String newP) {
        binding.pb.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), newP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updateEmail(newE)
                        .addOnSuccessListener(aVoid1 -> {
                                    binding.pb.setVisibility(View.GONE);
                                    Toast.makeText(ChangeEmail.this, "Email has changed", Toast.LENGTH_SHORT).show();
                                }))
                .addOnFailureListener(e -> {
                    binding.pb.setVisibility(View.GONE);
                    Toast.makeText(ChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

        );
    }
}