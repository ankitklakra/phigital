package com.phigital.ai.Shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterSingleProductCart;
import com.phigital.ai.Model.ModelCart;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityPayBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class Pay2 extends AppCompatActivity implements  PaymentResultListener {
    int num = 1;
    private static final int UPI_PAYMENT = 1;
    ActivityPayBinding binding;
    SharedPref sharedPref;
    String userId;
    AdapterSingleProductCart adapters;
    List<ModelCart> productList;
    List<String> pList ;
    List<Integer> nList ;
    List<Integer> mrpList ;
    List<Integer> saveList ;

    String hiscountry,hisfullname,hismobile,hispincode,hishouseno,hisarea,hislandmark,hiscity,hisstate;
    int sum = 0;
    int mrp = 0;
    int save = 0;
    String hisId,hispId,hiscategory,hisproductname,hisbrandname,hismrp,hisdiscount,hisimages,hisprice,hissaveamount,hisaddress,hisdetail,hispTime,hisavailabilty,
            hisquantity,hisfinalprice,hisfinalmrp,hisfinalsave;
    String productId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");

        productList = new ArrayList<>();
        pList = new ArrayList<>();
        nList = new ArrayList<>();
        mrpList = new ArrayList<>();
        saveList = new ArrayList<>();

        adapters = new AdapterSingleProductCart(Pay2.this,productList);
        binding.cartView.setHasFixedSize(true);
        binding.cartView.setLayoutManager(new LinearLayoutManager(Pay2.this, RecyclerView.VERTICAL, false));
        binding.cartView.setAdapter(adapters);

//        binding.amount.setText("₹ " +amount);
//        budget = binding.budget.getText().toString();
//
//        binding.done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                budget = binding.budget.getText().toString();
//                int a =Integer.parseInt(budget);
//                if(a <100){
//                    Toast.makeText(Pay.this,"Amount cannot be less than 100", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(Pay.this," Transaction is complete", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        binding.budget.addTextChangedListener(new TextWatcher() {
//           @Override
//           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//           }
//
//           @Override
//           public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//           }
//
//           @Override
//           public void afterTextChanged(Editable s) {
//               setAudience(s.toString());
//               calculatetax(s.toString());
//           }
//       });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mySaved();
    }

    private void mySaved() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SingleCart").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelCart post1 = snapshot1.getValue(ModelCart.class);
                    if (Objects.requireNonNull(post1).getAvailable().equals("true")) {
                        pList.add(snapshot1.getKey());
                    }
                }
                readSave();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference d =  FirebaseDatabase.getInstance().getReference().child("SingleCart").child(userId);
        d.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                nList.clear();
                mrpList.clear();
                saveList.clear();
                productList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ModelCart post1 = postSnapshot.getValue(ModelCart.class);
                    for (String id: pList){
                        if (Objects.requireNonNull(post1).getpId().equals(id)&&post1.getAvailable().equals("true")) {
                            productList.add(post1);
                            try {
                                nList.add(Integer.parseInt(Objects.requireNonNull(post1).getFinalprice()));
                                mrpList.add(Integer.parseInt(Objects.requireNonNull(post1).getFinalmrp()));
                                saveList.add(Integer.parseInt(Objects.requireNonNull(post1).getFinalsave()));
                            }catch (Exception ignored){

                            }
                        }

                    }

                }

                adapters.notifyDataSetChanged();
                sum = nList.stream().mapToInt(Integer::intValue).sum();
                binding.totalamount.setText("₹ "+sum);
                mrp = mrpList.stream().mapToInt(Integer::intValue).sum();
                binding.price.setText("₹ "+mrp);
                save = saveList.stream().mapToInt(Integer::intValue).sum();
                binding.discount.setText("- ₹ "+save);
                binding.deliverycharges.setText("Free");
                binding.text.setText("You will save ₹ "+save +"  on this order");
                binding.done.setOnClickListener(new View.OnClickListener()  {
                    @Override
                    public void onClick(View v) {
                        int am = sum * 100;
                        copyFirebaseData();
//                        startPayment(String.valueOf(am));
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Product");
//        reference2.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                productList.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                    ModelCart post2 = snapshot1.getValue(ModelCart.class);
//                    for (String id: pList){
//                        if (Objects.requireNonNull(post2).getpId().equals(id)) {
//                            productList.add(post2);
//                        }
//                    }
//                }
//
////                binding.cons3.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
////                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
////                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
////                                    ModelProduct post2 = snapshot1.getValue(ModelProduct.class);
////                                    for (String id : pList) {
////                                        if (Objects.requireNonNull(post2).getpId().equals(id)) {
////                                            if (Objects.requireNonNull(post2).getAvailable().equals("false")) {
////                                                cart = false;
////                                            }
////                                        }
////                                    }
////                                }
////
////                                if (cart){
////                                    String am = String.valueOf(sum);
////                                    Intent intent = new Intent(CartDetails.this, EditAddressActivity.class);
////                                    intent.putExtra("amount",am);
////                                    startActivity(intent);
////                                }else{
////                                    Toast.makeText(CartDetails.this, "Please remove out of stock items from cart", Toast.LENGTH_SHORT).show();
////                                }
////                                cart = false;
////
////                            }
////
////                            @Override
////                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
////
////                            }
////                        });
////
////
////                    }
////                });
//
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    public void copyFirebaseData() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference cartNodes = FirebaseDatabase.getInstance().getReference().child("SingleCart").child(userId);
        DatabaseReference shippingaddress =  FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("shippingaddress");
        final DatabaseReference ordernode = FirebaseDatabase.getInstance().getReference().child("CustomerOrders").child(timeStamp).child("Order");
        final DatabaseReference addressnode = FirebaseDatabase.getInstance().getReference().child("CustomerOrders").child(timeStamp).child("Address");

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("CustomerOrders");
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    long milliSeconds= Long.parseLong(String.valueOf(System.currentTimeMillis()));
                    String date = DateFormat.format("dd-MM-yyyy", milliSeconds).toString();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("orderdate", date);
                    hashMap.put("pId", timeStamp);
                    hashMap.put("id", userId);
                    likeRef.child(timeStamp).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        cartNodes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot){
                                ordernode.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        Toast.makeText(Pay2.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        shippingaddress.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                addressnode.setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        Toast.makeText(Pay2.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                });

                }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
//        Query query = cartNodes.orderByChild("pId").equalTo(productId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NotNull DataSnapshot dataSnapshot){
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    hiscategory = "" + ds.child("category").getValue();
//                    hisproductname = "" + ds.child("productname").getValue();
//                    hisbrandname = "" + ds.child("brandname").getValue();
//                    hismrp = "" + ds.child("mrp").getValue();
//                    hisdiscount = "" + ds.child("discount").getValue();
//                    hisprice = "" + ds.child("price").getValue();
//                    hissaveamount = "" + ds.child("saveamount").getValue();
//                    hisaddress = "" + ds.child("address").getValue();
//                    hisdetail = "" + ds.child("detail").getValue();
//                    hispTime = "" + ds.child("pTime").getValue();
//                    hisquantity = "" + ds.child("quantity").getValue();
//                    hisfinalprice = "" + ds.child("finalprice").getValue();
//                    hisfinalmrp = "" + ds.child("finalmrp").getValue();
//                    hisfinalsave = "" + ds.child("finalsave").getValue();
//                    hisavailabilty = "" + ds.child("available").getValue();
//                }
//                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("category", hiscategory);
//                hashMap.put("productname", hisproductname);
//                hashMap.put("brandname", hisbrandname);
//                hashMap.put("mrp", hismrp);
//                hashMap.put("discount", hisdiscount);
//                hashMap.put("price", hisprice);
//                hashMap.put("saveamount", hissaveamount);
//                hashMap.put("address", hisaddress);
//                hashMap.put("detail", hisdetail);
//                hashMap.put("orderdate", timeStamp);
//                hashMap.put("quantity", hisquantity);
//                hashMap.put("finalprice", hisfinalprice);
//                hashMap.put("finalmrp", hisfinalmrp);
//                hashMap.put("finalsave", hisfinalsave);
//                hashMap.put("available", hisavailabilty);
//                likeRef.child(timeStamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                });
//            }
//            @Override
//            public void onCancelled(@NotNull DatabaseError databaseError) {
//
//            }
//        });
//        Query query1 =shippingaddress.orderByChild("country").equalTo("India");
//        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()){
//                    hiscountry= ""+ds.child("country").getValue();
//                    hisfullname = ""+ds.child("fullname").getValue();
//                    hismobile = ""+ds.child("mobile").getValue();
//                    hispincode = ""+ds.child("pincode").getValue();
//                    hishouseno= ""+ds.child("houseno").getValue();
//                    hisarea = ""+ds.child("area").getValue();
//                    hislandmark = ""+ds.child("landmark").getValue();
//                    hiscity = ""+ds.child("city").getValue();
//                    hisstate = ""+ds.child("state").getValue();
//                }
//                HashMap<String, Object> hashMap2 = new HashMap<>();
//                hashMap2.put("country", hiscountry);
//                hashMap2.put("fullname", hisfullname);
//                hashMap2.put("mobile", hismobile);
//                hashMap2.put("pincode", hispincode);
//                hashMap2.put("houseno", hishouseno);
//                hashMap2.put("area", hisarea);
//                hashMap2.put("landmark", hislandmark);
//                hashMap2.put("city", hiscity);
//                hashMap2.put("state", hisstate);
//                likeRef.child(timeStamp).updateChildren(hashMap2);
////                                    ordernode.setValue(snapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
////                                        @Override
////                                        public void onSuccess(Void unused) {
////                                            cartNodes.child(productId).child("available").setValue("false");
////                                        }
////                                    });
//            }
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//        HashMap<Object, String> hashMap = new HashMap<>();
//        hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//        hashMap.put("pId", timeStamp);
//        FirebaseDatabase.getInstance().getReference("Invoice").child(timeStamp).setValue(hashMap);
//        Snackbar.make(binding.cl,"Uploaded", Snackbar.LENGTH_LONG).show();
    }

    private void startPayment(String amount) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_QEbhniXCho8QRC");

        /**
         * Set your logo here
         */
//        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Phigital");
//            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#00ACED");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits
            options.put("prefill.email", "phigital@gmail.com");
            options.put("prefill.contact","9988776655");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);
            checkout.setKeyID("rzp_live_QEbhniXCho8QRC");
            checkout.open(activity, options);

        } catch(Exception e) {

        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this,"Please wait", Toast.LENGTH_SHORT).show();
        copyFirebaseData();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment UnSuccess", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("QueryPermissionsNeeded")
    private void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tr", "261433")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(UPI_PAYMENT == requestCode){
            if(RESULT_OK == resultCode){
                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "UnSuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }
}