package com.phigital.ai.Shop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.R;
import com.phigital.ai.databinding.ActivityCheckoutBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class EditAddressActivity extends AppCompatActivity {
    String hisarea,hiscity,hiscountry,hisfullname,hishouseno,hislandmark,hispincode,hismobile,hisstate,amount;
    Context context = EditAddressActivity.this;
    int duration = Toast.LENGTH_SHORT;
    ActivityCheckoutBinding binding;
    Uri pdfuri;
    String userId;
    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;
    String name, dp, id;
   private RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);

        binding.back.setOnClickListener(v -> onBackPressed());

        String[] items = new String[]{
                "India"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            EditAddressActivity.this,
                    R.layout.dropdown_menu,
                    items
        );
        binding.country.setAdapter(adapter);
        mRequestQueue = Volley.newRequestQueue(EditAddressActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.cons3.setOnClickListener(v -> {
            String country = binding.country.getText().toString().trim();
            String fullname = Objects.requireNonNull(binding.fullname.getText()).toString().trim();
            String mobile = Objects.requireNonNull(binding.mobile.getText()).toString().trim();
            String pincode = Objects.requireNonNull(binding.pincode.getText()).toString().trim();
            String houseno = Objects.requireNonNull(binding.houseno.getText()).toString().trim();
            String area = Objects.requireNonNull(binding.area.getText()).toString().trim();
            String landmark = Objects.requireNonNull(binding.lankmark.getText()).toString().trim();
            String city = Objects.requireNonNull(binding.city.getText()).toString().trim();
            String state = Objects.requireNonNull(binding.state.getText()).toString().trim();

            if (TextUtils.isEmpty(country)) {
                binding.country.setError("Enter Country");
                Toast.makeText(context, "Enter Country", duration).show();
                return;
            }
            if (TextUtils.isEmpty(fullname)) {
                binding.fullname.setError("Enter Fullname");
                Toast.makeText(context, "Enter Fullname", duration).show();
                return;
            }
            if (TextUtils.isEmpty(mobile)) {
                binding.mobile.setError("Enter Mobile number");
                Toast.makeText(context, "Enter Mobile number", duration).show();
                return;
            }
            if (TextUtils.isEmpty(pincode)) {
                binding.pincode.setError("Enter pincode");
                Toast.makeText(context, "Enter pincode", duration).show();
                return;
            }
            if (TextUtils.isEmpty(houseno)) {
                binding.houseno.setError("Enter House no");
                Toast.makeText(context, "Enter House no", duration).show();
                return;
            }
            if (TextUtils.isEmpty(area)) {
                binding.area.setError("Enter House no");
                Toast.makeText(context, "Enter Flat, House no, Building, Company, Apartment", duration).show();
                return;
            }
            if (TextUtils.isEmpty(landmark)) {
                Toast.makeText(context, "Enter landmark", duration).show();
                return;
            }
            if (TextUtils.isEmpty(city)) {
                Toast.makeText(context, "Enter City", duration).show();
                return;
            }
            if (TextUtils.isEmpty(state)) {
                Toast.makeText(context, "Enter State", duration).show();
                return;
            }
//            if (TextUtils.isEmpty(maddress)) {
//                Toast.makeText(context, "Enter Address", duration).show();
//                return;
//            }
//            if (TextUtils.isEmpty(mdescription)) {
//                Toast.makeText(context, "Enter Description", duration).show();
//                return;
//            }
//            if (pdfuri == null){
//                uploadwithoutcv(mjobtype,medu,mexpert,mcity,msalary,mwage,mexperiencenum,mexperiencetime,memail,maddress,mdescription);
//               binding.pb.setVisibility(View.VISIBLE);
//            }
            else {
                confirm(country,fullname,mobile,pincode,houseno,area,landmark,city,state);
            }
        });

        binding.pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int a = 6;
                if (s.toString().length()==a){
                    getDataFromPinCode(s.toString());
                }
            }
        });

        loadPostInfo(userId);
    }

    private void loadPostInfo(String userId) {
        DatabaseReference dREF =  FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Query query = dREF.orderByChild("country").equalTo("India");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    hisarea = ""+ds.child("area").getValue();
                    hiscity = ""+ds.child("city").getValue();
                    hiscountry = ""+ds.child("country").getValue();
                    hisfullname = ""+ds.child("fullname").getValue();
                    hishouseno = ""+ds.child("houseno").getValue();
                    hislandmark= ""+ds.child("landmark").getValue();
                    hispincode = ""+ds.child("pincode").getValue();
                    hismobile= ""+ds.child("mobile").getValue();
                    hisstate = ""+ds.child("state").getValue();
                }
                binding.area.setText(hisarea);
                binding.city.setText(hiscity);
                binding.country.setText(hiscountry);
                binding.fullname.setText(hisfullname);
                binding.houseno.setText(hishouseno);
                binding.lankmark.setText(hislandmark);
                binding.pincode.setText(hispincode);
                binding.mobile.setText(hismobile);
                binding.state.setText(hisstate);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void confirm(String country, String fullname, String mobile, String pincode, String houseno, String area, String landmark, String city, String state) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("country", country);
        hashMap.put("fullname", fullname);
        hashMap.put("mobile", mobile);
        hashMap.put("pincode", pincode);
        hashMap.put("houseno", houseno);
        hashMap.put("area", area);
        hashMap.put("landmark", landmark);
        hashMap.put("city", city);
        hashMap.put("state", state);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        dRef.child("shippingaddress").setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Address added", duration).show();
                    Intent intent1 = new Intent(EditAddressActivity.this, Pay.class);
//                    intent1.putExtra("amount",am);
                    startActivity(intent1);
                }).addOnFailureListener(e -> {
                    Toast.makeText(context,  e.getMessage(), duration).show();
                });
    }

    private void getDataFromPinCode(String pinCode) {
        // clearing our cache of request queue.
        mRequestQueue.getCache().clear();

        // below is the url from where we will be getting
        // our response in the json format.
        String url = "http://www.postalpincode.in/api/pincode/" + pinCode;

        // below line is use to initialize our request queue.
        RequestQueue queue = Volley.newRequestQueue(EditAddressActivity.this);

        // in below line we are creating a
        // object request using volley.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // inside this method we will get two methods
                // such as on response method
                // inside on response method we are extracting
                // data from the json format.
                try {
                    // we are getting data of post office
                    // in the form of JSON file.
                    JSONArray postOfficeArray = response.getJSONArray("PostOffice");
                    if (response.getString("Status").equals("Error")) {
                        // validating if the response status is success or failure.
                        // in this method the response status is having error and
                        // we are setting text to TextView as invalid pincode.
                        Toast.makeText(EditAddressActivity.this, "Pin code is not valid.", Toast.LENGTH_SHORT).show();
                    } else {
                        // if the status is success we are calling this method
                        // in which we are getting data from post office object
                        // here we are calling first object of our json array.
                        JSONObject obj = postOfficeArray.getJSONObject(0);

                        // inside our json array we are getting district name,
                        // state and country from our data.
                        String district = obj.getString("District");
                        String state = obj.getString("State");
                        String country = obj.getString("Country");

                        // after getting all data we are setting this data in
                        // our text view on below line.
                        binding.state.setText(state);
                        binding.city.setText(district);
//                        pinCodeDetailsTV.setText("Details of pin code is : \n" + "District is : " + district + "\n" + "State : "
//                                + state + "\n" + "Country : " + country);
                    }
                } catch (JSONException e) {
                    // if we gets any error then it
                    // will be printed in log cat.
                    e.printStackTrace();
                    Toast.makeText(EditAddressActivity.this, "Pin code is not valid.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // below method is called if we get
                // any error while fetching data from API.
                // below line is use to display an error message.
                Toast.makeText(EditAddressActivity.this, "Pin code is not valid.", Toast.LENGTH_SHORT).show();
            }
        });
        // below line is use for adding object
        // request to our request queue.
        queue.add(objectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1   && data != null && data.getData() != null){
            pdfuri = data.getData();

        }
    }

}