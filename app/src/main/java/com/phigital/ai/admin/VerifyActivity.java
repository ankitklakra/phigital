package com.phigital.ai.admin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityVerifyBinding;
//import com.razorpay.Checkout;
//import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class VerifyActivity extends BaseActivity {

    ActivityVerifyBinding binding;
    String refOne,refTwo,gId,Type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify);
        binding.back.setOnClickListener(v -> onBackPressed());

        String[] cont = new String[]{
                "Brand/Business/Organisation",
                "Influencer/Artists",
                "Government/Politics"
        };

       ArrayAdapter <String> items = new ArrayAdapter <>(
               this,
               R.layout.dropdown_menu2,
               cont);
       binding.type.setAdapter(items);

       binding.submit.setOnClickListener(v -> {
             refOne = binding.rOne.getText().toString();
             gId =   binding.id.getText().toString();
//             Type = binding.type.getText().toString();
            if (refOne.isEmpty() && refTwo.isEmpty() && gId.isEmpty() && Type.isEmpty()){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter every details")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }else {
//                startPayment();
            }
        });

    }
//    public void startPayment() {
//        Checkout checkout = new Checkout();
//        checkout.setKeyID("rzp_live_QEbhniXCho8QRC");
//
////        checkout.setImage(R.drawable.logo);
//
//        final Activity activity = this;
//
//        try {
//            JSONObject options = new JSONObject();
//
//            options.put("name", "Phigital");
////            options.put("description", "Reference No. #123456");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
////            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
//            options.put("theme.color", "#00ACED");
//            options.put("currency", "INR");
//            options.put("amount", 121000);
//            options.put("prefill.email", "phigital@gmail.com");
//            options.put("prefill.contact","9988776655");
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 4);
//            options.put("retry", retryObj);
//            checkout.setKeyID("rzp_live_QEbhniXCho8QRC");
//            checkout.open(activity, options);
//
//        } catch(Exception e) {
//
//        }
//    }
//    @Override
//    public void onPaymentSuccess(String s) {
//
//        Toast.makeText(this,"we are verifying please wait", Toast.LENGTH_SHORT).show();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//        hashMap.put("type", Type);
//        hashMap.put("gId",gId);
//        hashMap.put("refOne",refOne);
//        hashMap.put("refTwo",refTwo);
//
//        FirebaseDatabase.getInstance().getReference().child("Verification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hashMap).addOnSuccessListener(aVoid -> {
//            new StyleableToast
//                    .Builder(getApplicationContext())
//                    .text("Request sent")
//                    .textColor(Color.WHITE)
//                    .textBold()
//                    .gravity(0)
//                    .length(2000)
//                    .solidBackground()
//                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                    .show();
//            binding.rOne.setText("");
////            binding.type.setText("");
//            binding.id.setText("");
//        });
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//    }
}