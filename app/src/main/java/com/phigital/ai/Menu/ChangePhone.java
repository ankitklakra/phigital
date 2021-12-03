package com.phigital.ai.Menu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ChangePhone extends BaseActivity {



    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    private DatabaseReference mDatabase;
    Button button;
    EditText otp;
    String oldp;
    EditText pass,name;
    ImageView imageView3,imageView4;
    ProgressBar pb;
    FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        pass = findViewById(R.id.pass);
        name = findViewById(R.id.name);
        pb = findViewById(R.id.pb);
        otp = findViewById(R.id.otp);
        button = findViewById(R.id.button2);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oldp = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChangePhone.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        sendOTP();
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if(otp.getText() == null || otp.getText().toString().isEmpty()){
                    return;
                }
                otp.setError(null);
                String code = otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                signInWithPhoneAuthCredential(credential,oldp);
            }
        });
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView3.setOnClickListener(v -> onBackPressed());

//        imageView4.setOnClickListener(v -> {
//            String oldP = name.getText().toString().trim();
//            String newP = pass.getText().toString().trim();
//            if (TextUtils.isEmpty(oldP)){
////                Alerter.create(ChangePassword.this)
////                        .setTitle("Error")
////                        .setIcon(R.drawable.ic_error)
////                        .setBackgroundColorRes(R.color.colorPrimary)
////                        .setDuration(10000)
////                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
////                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
////                        .enableSwipeToDismiss()
////                        .setText("Enter your current password")
////                        .show();
//                return;
//            }else if (TextUtils.isEmpty(newP)){
////                Alerter.create(ChangePassword.this)
////                        .setTitle("Error")
////                        .setIcon(R.drawable.ic_error)
////                        .setBackgroundColorRes(R.color.colorPrimary)
////                        .setDuration(10000)
////                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
////                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
////                        .enableSwipeToDismiss()
////                        .setText("Enter your new password")
////                        .show();
//                return;
//            }else if (newP.length()<6){
////                Alerter.create(ChangePassword.this)
////                        .setTitle("Error")
////                        .setIcon(R.drawable.ic_error)
////                        .setBackgroundColorRes(R.color.colorPrimary)
////                        .setDuration(10000)
////                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
////                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
////                        .enableSwipeToDismiss()
////                        .setText("Password should have minimum 6 characters")
////                        .show();
//                return;
//            }
//            sendOTP();
//
//        });

    }

    private void sendOTP(){
        String p = "7999836278";
        mCallback =  new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //    Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential,oldp);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                // Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    OTP.setError(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    OTP.setError(e.getMessage());
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //  Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+p,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
               ChangePhone.this,               // Activity (for callback binding)
                mCallback);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential ,String oldP) {
        pb.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getPhoneNumber()), oldP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updatePhoneNumber(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChangePhone.this, " Success", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePhone.this, " Fail", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePhone.this, " Fail", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                            }
                        })
                );
    }
}