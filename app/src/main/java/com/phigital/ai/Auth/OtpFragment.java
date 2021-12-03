package com.phigital.ai.Auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Check;
import com.phigital.ai.MainActivity;
import com.phigital.ai.databinding.FragmentOtpBinding;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class OtpFragment extends Fragment {

    FragmentOtpBinding binding;

    public OtpFragment() {
        // Required empty public constructor

    }
     public OtpFragment(String mEmail,String mPassword, String mPhone, String mUsername){
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mPhone = mPhone;
        this.mUsername = mUsername;
     }

    private String verificationId,mEmail,mPhone,mPassword,mUsername;
    private Timer timer;
    private int count = 60;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvPhone.setText("Verification code has been sent to  "+mPhone);
        sendVerificationCode(mPhone);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    if (count == 0){
                        binding. Resend.setText("RESEND");
                        binding. Resend.setEnabled(true);
                        binding. Resend.setAlpha(1f);
                    }else{
                        binding. Resend.setText("Resend in "+count);
                        count--;
                    }
                });
            }
        },0,1000);

       binding.Resend.setOnClickListener(v -> {
            resendOTP();
            binding.Resend.setEnabled(false);
            binding.Resend.setAlpha(0.5f);
            count = 60;
        });

       binding.Verify.setOnClickListener(v -> {
           binding. progressbar.setVisibility(View.VISIBLE);
            String code =binding. OTP.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6){
                Snackbar.make(v,"Enter OTP", Snackbar.LENGTH_LONG).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }else {
                verifyCode(code);
            }
        });

        binding.Newuser.setOnClickListener(v -> ((RegisterActivity) requireActivity()).setFragment(new CreateAccountFragment()));
    }



    private void sendVerificationCode(String mPhone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhone,
                60,
                TimeUnit.SECONDS,
                requireActivity(),
                mCallbacks);
    }

    private  void resendOTP(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhone,
                60,
                TimeUnit.SECONDS,
                requireActivity(),
                mCallbacks,mResendToken);
    }
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                binding. OTP.setText(code);
                verifyCode(code);
                binding. progressbar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            binding. progressbar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            binding. progressbar.setVisibility(View.INVISIBLE);
        }
    };

    private  void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        binding. progressbar.setVisibility(View.VISIBLE);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(mEmail).addOnSuccessListener(signInMethodQueryResult ->
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                String timeStamp = ""+ System.currentTimeMillis();
                FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                AuthCredential credential1 = EmailAuthProvider.getCredential(mEmail,mPassword);
                Objects.requireNonNull(user).linkWithCredential(credential1).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        String userid = Objects.requireNonNull(user).getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                        HashMap<Object, String> map = new HashMap<>();
                        map.put("bio", "");
                        map.put("birthdate","");
                        map.put("bloodgroup", "");
                        map.put("city", "");
                        map.put("Count", "");
                        map.put("education","");
                        map.put("email",mEmail);
                        map.put("fambers","");
                        map.put("gender","");
                        map.put("hometown","");
                        map.put("id", userid);
                        map.put("job","");
                        map.put("joined",timeStamp);
                        map.put("language","");
                        map.put("likes", "");
                        map.put("link","");
                        map.put("location","");
                        map.put("name","");
                        map.put("notify","all");
                        map.put("password",mPassword);
                        map.put("phone",mPhone);
                        map.put("privacy","public");
                        map.put("relation","");
                        map.put("status","");
                        map.put("talent","");
                        map.put("typingTo","noOne");
                        map.put("username",mUsername);
                        map.put("verified", "");
                        map.put("photo", "https://firebasestorage.googleapis.com/v0/b/phigital-in.appspot.com/o/profile_images%2Fplaceholder.png?alt=media&token=df3a9c2a-e130-433f-8ec7-4db57d76d2c3");
                        reference.setValue(map).addOnCompleteListener(task2 -> {
                            if (task1.isSuccessful()){
                                Intent usernameIntent = new Intent(getContext(), SearchActivity.class);
                                usernameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(usernameIntent);
                                requireActivity().finish();
                            }else{
                                String error = Objects.requireNonNull(task1.getException()).getMessage();
                                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else{
                        String error = Objects.requireNonNull(task1.getException()).getMessage();
                        Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                        binding.progressbar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            else {
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                binding.progressbar.setVisibility(View.INVISIBLE);
            }
        })).addOnFailureListener(e -> {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            binding. progressbar.setVisibility(View.INVISIBLE);
        });

    }
//    private void sendOTP(){
//        mCallback =  new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                // This callback will be invoked in two situations:
//                // 1 - Instant verification. In some cases the phone number can be instantly
//                //     verified without needing to send or enter a verification code.
//                // 2 - Auto-retrieval. On some devices Google Play services can automatically
//                //     detect the incoming verification SMS and perform verification without
//                //     user action.
//                //    Log.d(TAG, "onVerificationCompleted:" + credential);
//
//                 signInWithPhoneAuthCredential(credential);
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                // Log.w(TAG, "onVerificationFailed", e);
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    OTP.setError(e.getMessage());
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    OTP.setError(e.getMessage());
//                }
//
//                // Show a message and update the UI
//                // ...
//            }
//
//            @Override
//            public void onCodeSent(@NonNull String verificationId,
//                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                //  Log.d(TAG, "onCodeSent:" + verificationId);
//
//                // Save verification ID and resending token so we can use them later
//                mVerificationId = verificationId;
//                mResendToken = token;
//
//                // ...
//            }
//        };
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                Phone,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                requireActivity(),               // Activity (for callback binding)
//                mCallback);        // OnVerificationStateChangedCallbacks
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(requireActivity(), task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                       // Log.d(TAG, "signInWithCredential:success");
//                        String timeStamp = ""+ System.currentTimeMillis();
//                        FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
//                        AuthCredential credential1 = EmailAuthProvider.getCredential(Email, Password);
//                        assert user != null;
//                        user.linkWithCredential(credential1).addOnCompleteListener(task1 -> {
//                            if (task1.isSuccessful()){
//                                String userid = Objects.requireNonNull(user).getUid();
//                                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
//                                Map<String,Object>map = new HashMap<>();
//                                map.put("email",Email);
//                                map.put("phone",Phone);
//                                map.put("name",Fullname);
//                                map.put("password",Password);
//
//                                map.put("username","");
//                                map.put("job","");
//                                map.put("talent","");
//                                map.put("birthdate","");
//                                map.put("gender","");
//                                map.put("verified", "");
//                                map.put("id", userid);
//
//                                map.put("bio", "");
//                                map.put("location","");
//                                map.put("status","online");
//                                map.put("typingTo","noOne");
//                                map.put("link","");
//
//                                map.put("bloodgroup", "");
//                                map.put("education","");
//                                map.put("fambers","");
//                                map.put("hometown","");
//                                map.put("joined",timeStamp);
//                                map.put("language","");
//                                map.put("likes", "");
//                                map.put("relation","");
//                                map.put("notify","all");
//                                map.put("privacy","public");
//
//                                map.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/avatar.jpg?alt=media&token=8b875027-3fa4-4da4-a4d5-8b661d999472");
//                                reference.setValue(map).addOnCompleteListener(task2 -> {
//                                    if (task1.isSuccessful()){
//                                        Intent usernameIntent = new Intent(getContext(), ProfileFinishActivity.class);
//                                        startActivity(usernameIntent);
//                                        requireActivity().finish();
//                                    }else{
//                                        String error = Objects.requireNonNull(task1.getException()).getMessage();
//                                        Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                            }else{
//                                String error = Objects.requireNonNull(task1.getException()).getMessage();
//                                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        // Sign in failed, display a message and update the UI
//                       // Log.w(TAG, "signInWithCredential:failure", task.getException());
//                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                            OTP.setError("Invaild OTP");
//                        }
//                    }
//                });
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
