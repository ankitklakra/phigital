package com.phigital.ai.Auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.phigital.ai.databinding.FragmentCreateAccountBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;


public class CreateAccountFragment extends Fragment {

    public final static String USERNAME_PATTERN = "^[a-z_.0-9]{3,15}$";
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

//         binding.Confirm.setOnClickListener(v -> {
//            p =  binding.ccp.getFullNumberWithPlus();
//            String email = Objects.requireNonNull(binding.Email.getText()).toString().trim();
//            String phone = Objects.requireNonNull(binding.Phone.getText()).toString().trim();
//            String password = Objects.requireNonNull(binding.Password.getText()).toString().trim();
//            String name = Objects.requireNonNull(binding.Fullname.getText()).toString().trim();
//
//            if (TextUtils.isEmpty(email)){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Enter Email")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//                return;
//            }
//            if (TextUtils.isEmpty(p)){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Enter Phone")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//                return;
//            }
//            if (TextUtils.isEmpty(password)){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Enter Password")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//                return;
//            }
//            if (TextUtils.isEmpty(name)){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Enter Name")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//                return;
//            }
//
////            Email.setError(null);
////            Phone.setError(null);
////            Password.setError(null);
////            Fullname.setError(null);
////            progressbar.setVisibility(View.INVISIBLE);
////
////            if (Email.getText().toString().isEmpty()) {
////                Email.setError("Required");
////                return;
////            }
////            if (Phone.getText().toString().isEmpty()) {
////                Phone.setError("Required");
////                return;
////            }
////            if (Password.getText().toString().isEmpty()) {
////                Password.setError("Required");
////                return;
////            }
////            if (Fullname.getText().toString().isEmpty()) {
////                Fullname.setError("Required");
////                return;
////            }
//            if (! VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Please enter a valid Email")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//                return;
//            }
////            if (phone.length() != 10){
////                new StyleableToast
////                        .Builder(requireContext())
////                        .text("Please enter a valid Phone number")
////                        .textColor(Color.WHITE)
////                        .textBold()
////                        .length(2000)
////                        .gravity(0)
////                        .solidBackground()
////                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
////                        .show();
////                return;
////            }
//            if (password.length() < 6){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Password cannot be less than 6 characters!")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//                return;
//            }
//
//            if (!binding.terms.isChecked()){
//                new StyleableToast
//                        .Builder(requireContext())
//                        .text("Please check the box")
//                        .textColor(Color.WHITE)
//                        .textBold()
//                        .length(2000)
//                        .gravity(0)
//                        .solidBackground()
//                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                        .show();
//            }
//            else {
//                ConfirmBtn(email, p, password, name);
//            }
//        });

         binding.Confirm.setOnClickListener(v -> {
             if (!binding.terms.isChecked()){
                 Snackbar.make(v,"Please accept Terms and Conditions", Snackbar.LENGTH_LONG).show();
             }else{
                 binding.progressbar.setVisibility(View.VISIBLE);
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
                             }else {
                                 Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(mUsername);
                                 usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (snapshot.getChildrenCount()>0){
                                             Snackbar.make(v,"Username already exist, try with new one", Snackbar.LENGTH_LONG).show();
                                             binding.progressbar.setVisibility(View.INVISIBLE);
                                         }else {
                                             Query userQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(mPhone);
                                             userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                     if (snapshot.getChildrenCount()>0){
                                                         Snackbar.make(v,"Phone number already exist, try with new one", Snackbar.LENGTH_LONG).show();
                                                         binding.progressbar.setVisibility(View.INVISIBLE);
                                                     }else{
                                                         register(v,mEmail,mPassword,mPhone,mUsername);
                                                     }
                                                 }

                                                 @Override
                                                 public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                 }
                                             });

                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {
                                         Snackbar.make(v,error.getMessage(), Snackbar.LENGTH_LONG).show();
                                     }
                                 });
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {
                             Snackbar.make(v,error.getMessage(), Snackbar.LENGTH_LONG).show();
                         }
                     });
                 }
             }
        });
    }

    private void register(View v,String mEmail, String mPassword, String mPhone, String mUsername) {
        ((RegisterActivity) requireActivity()).setFragment(new OtpFragment(mEmail,mPassword,mPhone,mUsername));
    }

//    private void checkforphone(String email, String phone, String password,String name) {
//        binding.progressbar.setVisibility(View.VISIBLE);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
//        ref.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    binding.progressbar.setVisibility(View.INVISIBLE);
//                    new StyleableToast
//                            .Builder(requireContext())
//                            .text("Phone number already exists!")
//                            .textColor(Color.WHITE)
//                            .textBold()
//                            .length(2000)
//                            .gravity(0)
//                            .solidBackground()
//                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                            .show();
//                }else{
//                    ConfirmBtn(email,phone,password,name);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void ConfirmBtn(String email, String phone, String password,String name){
////        binding.progressbar.setVisibility(View.VISIBLE);
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(requireActivity(), task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        updateUI(user,email,phone,password,name);
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//                    }
//                });
////        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
////            @Override
////            public void onSuccess(AuthResult authResult) {
////                String timeStamp = ""+ System.currentTimeMillis();
////                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////                String userid = Objects.requireNonNull(user).getUid();
////                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
////                Map<String,Object> map = new HashMap<>();
////                map.put("email",email);
////                map.put("phone",phone);
////                map.put("name",name);
////                map.put("password",password);
////
////                map.put("username","");
////                map.put("job","");
////                map.put("talent","");
////                map.put("birthdate","");
////                map.put("gender","");
////                map.put("verified", "");
////                map.put("id", userid);
////
////                map.put("bio", "");
////                map.put("location","");
////                map.put("status","online");
////                map.put("typingTo","noOne");
////                map.put("link","");
////
////                map.put("bloodgroup", "");
////                map.put("education","");
////                map.put("fambers","");
////                map.put("hometown","");
////                map.put("joined",timeStamp);
////                map.put("language","");
////                map.put("likes", "");
////                map.put("relation","");
////                map.put("notify","all");
////                map.put("privacy","public");
////
////                map.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/avatar.jpg?alt=media&token=8b875027-3fa4-4da4-a4d5-8b661d999472");
////                reference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
////                    @Override
////                    public void onSuccess(Void unused) {
////                        Intent usernameIntent = new Intent(getContext(), ProfileFinishActivity.class);
////                        startActivity(usernameIntent);
////                        requireActivity().finish();
////                    }
////                }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull @NotNull Exception e) {
////                        String error = Objects.requireNonNull(e).getMessage();
////                        Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
////        });
////                addOnCompleteListener(task -> {
////            if (task.isSuccessful()){
////                if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty()){
////                    ((RegisterActivity) requireActivity()).setFragment(new OtpFragment(email,phone,password,name));
////                }else{
////                    progressbar.setVisibility(View.INVISIBLE);
////                    new StyleableToast
////                            .Builder(requireContext())
////                            .text("Email already exists!")
////                            .textColor(Color.WHITE)
////                            .textBold()
////                            .length(2000)
////                            .gravity(0)
////                            .solidBackground()
////                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
////                            .show();
////                }
////            }else{
////                progressbar.setVisibility(View.INVISIBLE);
////                String error = Objects.requireNonNull(task.getException()).getMessage();
////                Toast.makeText(getContext(),error, Toast.LENGTH_SHORT).show();
////            }
////        });
//    }
//
//    private void updateUI(FirebaseUser user,String email,String phone,String password,String name) {
//        binding.progressbar.setVisibility(View.VISIBLE);
//        String timeStamp = ""+ System.currentTimeMillis();
//        String userid = Objects.requireNonNull(user).getUid();
//
//        HashMap<Object, String> map = new HashMap<>();
//        map.put("email",email);
//        map.put("phone",phone);
//        map.put("name",name);
//        map.put("password",password);
//
//        map.put("username","");
//        map.put("job","");
//        map.put("talent","");
//        map.put("birthdate","");
//        map.put("gender","");
//        map.put("verified", "");
//        map.put("id", userid);
//
//        map.put("bio", "");
//        map.put("location","");
//        map.put("status","online");
//        map.put("typingTo","noOne");
//        map.put("link","");
//
//        map.put("bloodgroup", "");
//        map.put("education","");
//        map.put("fambers","");
//        map.put("hometown","");
//        map.put("joined",timeStamp);
//        map.put("language","");
//        map.put("likes", "");
//        map.put("relation","");
//        map.put("notify","all");
//        map.put("privacy","public");
//        map.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/avatar.jpg?alt=media&token=8b875027-3fa4-4da4-a4d5-8b661d999472");
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
//        reference.setValue(map).addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                Intent usernameIntent = new Intent(getContext(), ProfileFinishActivity.class);
//                startActivity(usernameIntent);
//                requireActivity().finish();
//            }else{
//                Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(e -> {
//            String error = Objects.requireNonNull(e).getMessage();
//            Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
//        });
//    }
}