package com.phigital.ai.Auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.phigital.ai.R;

import java.util.Objects;
import java.util.regex.Pattern;


public class ForgotPasswordFragment extends Fragment {


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private EditText Email;
    private Button resetbtn;
    private TextView Newuser;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init (view);
        firebaseAuth = FirebaseAuth.getInstance();
        resetbtn.setOnClickListener(v -> {
            Email.setError(null);
            if (VALID_EMAIL_ADDRESS_REGEX.matcher(Email.getText().toString()).find()) {
                resetbtn.setEnabled(false);
                firebaseAuth.sendPasswordResetEmail(Email.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(),"Password reset email Sent successfully",Toast.LENGTH_LONG).show();
                                requireActivity().onBackPressed();
                            }else{
                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                Email.setError(error);
                            }
                            resetbtn.setEnabled(true);
                        });

                }
            else{
                Email.setError("Please provide a valid email");
            }
        });

        Newuser.setOnClickListener(v -> ((RegisterActivity) requireActivity()).setFragment(new CreateAccountFragment()));
    }

    private void  init(View view){

    Email = view.findViewById(R.id.Email);
    resetbtn = view.findViewById(R.id. resetbtn);
    Newuser= view.findViewById(R.id.Newuser);
    }
}