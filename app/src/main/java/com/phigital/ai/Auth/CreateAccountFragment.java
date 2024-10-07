package com.phigital.ai.Auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.databinding.FragmentCreateAccountBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;


public class CreateAccountFragment extends Fragment {

    public final static String USERNAME_PATTERN = "^[a-z_.0-9]{3,15}$";
    private FirebaseAuth mAuth;
    public CreateAccountFragment() {
    }

    FragmentCreateAccountBinding binding;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateAccountBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.LoginTV.setOnClickListener(v -> ((RegisterActivity) requireActivity()).setFragment(new LoginFragment()));
        binding.ccp.registerCarrierNumberEditText(binding.Phone);

        binding.link.setOnClickListener(v -> {
            String url = "https://sites.google.com/view/phigital/privacy-policy";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });



         binding.Confirm.setOnClickListener(v -> {
             if (!binding.terms.isChecked()){
                 Snackbar.make(v,"Please accept Terms and Conditions", Snackbar.LENGTH_LONG).show();
             }else{
                 binding.progressbar.setVisibility(View.VISIBLE);
                 binding.Confirm.setEnabled(false);

                 String mEmail = Objects.requireNonNull(binding.Email.getText()).toString().trim();
                 String mPhone = binding.ccp.getFullNumberWithPlus();
                 String mUsername = Objects.requireNonNull(binding.username.getText()).toString().trim().replaceAll("\\s","").toLowerCase();;
                 String mPassword = Objects.requireNonNull(binding.Password.getText()).toString().trim();

                 if (mEmail.isEmpty()){
                     Snackbar.make(v,"Enter your email", Snackbar.LENGTH_LONG).show();
                     binding.progressbar.setVisibility(View.INVISIBLE);
                 }else if(mPassword.isEmpty()){
                     Snackbar.make(v,"Enter your password", Snackbar.LENGTH_LONG).show();
                     binding.progressbar.setVisibility(View.INVISIBLE);
                 }
                 else if(mPhone.isEmpty()){
                     Snackbar.make(v,"Enter your Phone", Snackbar.LENGTH_LONG).show();
                     binding.progressbar.setVisibility(View.INVISIBLE);
                 }
                 else if(mUsername.isEmpty() || !mUsername.matches(USERNAME_PATTERN)){
                     Snackbar.make(v,"Usernames can only use letters, numbers, underscore and dot.", Snackbar.LENGTH_LONG).show();
                     binding.progressbar.setVisibility(View.INVISIBLE);
                 }
                 else if (mPassword.length()<6){
                     Snackbar.make(v,"Password should have minimum 6 characters", Snackbar.LENGTH_LONG).show();
                     binding.progressbar.setVisibility(View.INVISIBLE);
                 }
                 else {
                     Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(mEmail);
                     emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if (snapshot.getChildrenCount()>0){
                                 Snackbar.make(v,"Email already exist, try with new one", Snackbar.LENGTH_LONG).show();
                                 binding.progressbar.setVisibility(View.INVISIBLE);
                                 binding.Confirm.setEnabled(true); // Re-enable the button

                             }else {
                                 Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(mUsername);
                                 usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (snapshot.getChildrenCount()>0){
                                             Snackbar.make(v,"Username already exist, try with new one", Snackbar.LENGTH_LONG).show();
                                             binding.progressbar.setVisibility(View.INVISIBLE);
                                             binding.Confirm.setEnabled(true); // Re-enable the button

                                         }else {
                                             Query userQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(mPhone);
                                             userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                     if (snapshot.getChildrenCount()>0){
                                                         Snackbar.make(v,"Phone number already exist, try with new one", Snackbar.LENGTH_LONG).show();
                                                         binding.progressbar.setVisibility(View.INVISIBLE);
                                                         binding.Confirm.setEnabled(true); // Re-enable the button

                                                     }else{
                                                         register(v,mEmail,mPassword,mPhone,mUsername);
                                                     }
                                                 }

                                                 @Override
                                                 public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                     Snackbar.make(view, "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                                                     binding.progressbar.setVisibility(View.INVISIBLE);
                                                     binding.Confirm.setEnabled(true); // Re-enable the button

                                                 }
                                             });

                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                         Snackbar.make(view, "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                                         binding.progressbar.setVisibility(View.INVISIBLE);
                                         binding.Confirm.setEnabled(true); // Re-enable the button

                                     }
                                 });
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                             Snackbar.make(view, "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                             binding.progressbar.setVisibility(View.INVISIBLE);
                             binding.Confirm.setEnabled(true); // Re-enable the button

                         }
                     });
                 }
             }
        });
    }

    private void register(View v, String mEmail, String mPassword, String mPhone, String mUsername) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registered successfully
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String timeStamp = "" + System.currentTimeMillis();
                                String userId = Objects.requireNonNull(user).getUid();

                                // Save user information in Realtime Database
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("bio", "");
                                map.put("birthdate", "");
                                map.put("bloodgroup", "");
                                map.put("city", "");
                                map.put("Count", "");
                                map.put("education", "");
                                map.put("email", mEmail);
                                map.put("fambers", "");
                                map.put("gender", "");
                                map.put("hometown", "");
                                map.put("id", userId);
                                map.put("job", "");
                                map.put("joined", timeStamp);
                                map.put("language", "");
                                map.put("likes", "");
                                map.put("link", "");
                                map.put("location", "");
                                map.put("name", "");
                                map.put("notify", "all");
                                map.put("password", mPassword);
                                map.put("phone", mPhone);
                                map.put("privacy", "public");
                                map.put("relation", "");
                                map.put("status", "");
                                map.put("talent", "");
                                map.put("typingTo", "noOne");
                                map.put("username", mUsername);
                                map.put("verified", "");
                                map.put("photo", "https://firebasestorage.googleapis.com/v0/b/phigital-in.appspot.com/o/profile_images%2Fplaceholder.png?alt=media&token=df3a9c2a-e130-433f-8ec7-4db57d76d2c3");
                                userRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Data saved successfully, proceed to next activity
                                            binding.progressbar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getContext(), "Please Edit your profile", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                            requireActivity().finish();
                                        } else {
                                            // Error while saving user data
                                            binding.progressbar.setVisibility(View.INVISIBLE);
                                            Snackbar.make(v, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else {
                                // Registration failed
                                Snackbar.make(v, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            // Registration failed
                            binding.progressbar.setVisibility(View.INVISIBLE);
                            Snackbar.make(v, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

}