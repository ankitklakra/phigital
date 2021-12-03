package com.phigital.ai.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Check;
import com.phigital.ai.MainActivity;
import com.phigital.ai.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {

    public LoginFragment() {

    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private EditText mEmail,mPassword;
    private Button letsgo_btn;
    private ProgressBar progressbar;
    private TextView ForgetPassword,Newuser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private String oldp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        init(view);
        letsgo_btn.setOnClickListener(v -> {
            mEmail.setError(null);
            mPassword.setError(null);
            progressbar.setVisibility(View.INVISIBLE);
            if (mEmail.getText().toString().isEmpty()){
                mEmail.setError("Required");
                return;
            }
            if (mPassword.getText().toString().isEmpty()){
                mPassword.setError("Required");
                return;
            }
            if (VALID_EMAIL_ADDRESS_REGEX.matcher(mEmail.getText().toString()).find()) {
                login (mEmail.getText().toString().trim());

            }
//            else if (mEmail.getText().toString().matches("\\d{10}")){
//
//                FirebaseAuth mAuth =FirebaseAuth.getInstance();
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
//                ref.orderByChild("phone").equalTo("+91"+mEmail.getText().toString()).
//                        addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()){
//                            String s = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
////                            progressbar.setVisibility(View.INVISIBLE);
//                            new StyleableToast
//                                    .Builder(requireContext())
//                                    .text(s)
//                                    .textColor(Color.WHITE)
//                                    .textBold()
//                                    .length(2000)
//                                    .gravity(0)
//                                    .solidBackground()
//                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                                    .show();
//                        }else{
//                            new StyleableToast
//                                    .Builder(requireContext())
//                                    .text("user not found")
//                                    .textColor(Color.WHITE)
//                                    .textBold()
//                                    .length(2000)
//                                    .gravity(0)
//                                    .solidBackground()
//                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                                    .show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
////                firebaseAuth = FirebaseAuth.getInstance();
////                userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
////                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
////                mDatabase.addValueEventListener(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        oldp = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////                        Toast.makeText(requireActivity(), databaseError.getMessage(),Toast.LENGTH_SHORT).show();
////
////                    }
////                });
////                FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone",mEmail.getText().toString())
////                        .get().addOnCompleteListener(task -> {
////                            if (task.isSuccessful()){
////                                List<DocumentSnapshot> document = Objects.requireNonNull(task.getResult()).getDocuments();
////                                if (document.isEmpty()){
////                                    mEmail.setError("phone number not found");
////                                }else{
////                                    String email = Objects.requireNonNull(document.get(0).get("email")).toString();
////                                    login (email);
////                                }
////                            }else{
////                                progressbar.setVisibility(View.INVISIBLE);
////                                String error = Objects.requireNonNull(task.getException()).getMessage();
////                                Toast.makeText(getContext(),error, Toast.LENGTH_SHORT).show();
////                            }
////                        });
//            }
            else {
                progressbar.setVisibility(View.INVISIBLE);
                mEmail.setError("Please enter a valid Email or Phone");
            }

        });
        ForgetPassword.setOnClickListener(v -> ((RegisterActivity)requireActivity()).setFragment(new ForgotPasswordFragment()));
        Newuser.setOnClickListener(v -> ((RegisterActivity)requireActivity()).setFragment(new CreateAccountFragment()));

    }
    private void  init(View view){
        mEmail = view.findViewById(R.id.Email);
        mPassword = view.findViewById(R.id. Password);
        letsgo_btn = view.findViewById(R.id.letsgo_btn);
        ForgetPassword = view.findViewById(R.id.ForgetPassword);
        Newuser= view.findViewById(R.id.Newuser);
        progressbar= view.findViewById(R.id.progressbar);
    }

    private  void  login(String email){
        progressbar.setVisibility(View.VISIBLE);
        String password = mPassword.getText().toString().trim();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
          if (task.isSuccessful()){
              FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if (snapshot.exists()){
                          Query userQuery = FirebaseDatabase.getInstance().getReference().child("Ban");
                          userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                  if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                      progressbar.setVisibility(View.INVISIBLE);
                                      Toast.makeText(getContext(), "We have terminate your account due to violating our community guidelines.", Toast.LENGTH_LONG).show();
                                  }else{
                                      Intent intent = new Intent(getContext(), Check.class);
                                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                      startActivity(intent);
                                  }
                              }

                              @Override
                              public void onCancelled(@NonNull @NotNull DatabaseError error) {

                              }
                          });
                      }else {
                          FirebaseAuth.getInstance().getCurrentUser().delete();
                          Toast.makeText(getContext(), "User doesn't exist", Toast.LENGTH_SHORT).show();
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });
          }else{
              progressbar.setVisibility(View.INVISIBLE);
              String error = Objects.requireNonNull(task.getException()).getMessage();
              Toast.makeText(getContext(),error, Toast.LENGTH_SHORT).show();
          }
        });
    }
}