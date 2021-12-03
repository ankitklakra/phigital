package com.phigital.ai.Shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterMyProduct;
import com.phigital.ai.Model.ModelProduct;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityMyproductBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyProductActivity extends AppCompatActivity {
    SharedPref sharedPref;

    String id,productId,userId;
    AdapterMyProduct adapters;

    ActivityMyproductBinding binding;
    List<ModelProduct> productList = new ArrayList<>();
    List<String> pList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_myproduct);
        binding.back.setOnClickListener(v -> onBackPressed());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        adapters = new AdapterMyProduct(getApplicationContext(),productList);
        binding.cartView.setHasFixedSize(true);
        binding.cartView.setLayoutManager(new LinearLayoutManager(MyProductActivity.this,LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(adapters);

        binding.cons3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProductActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.state1.setVisibility(View.GONE);
                binding.state2.setVisibility(View.VISIBLE);
            }
        });
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.state1.setVisibility(View.VISIBLE);
                binding.state2.setVisibility(View.GONE);
            }
        });
        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterproduct(s.toString().toLowerCase());
                }else {
                    loadPost();
                }
            }
        });
        loadPost();
        setCommentno();
    }

    private void filterproduct(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    assert modelProduct != null;
                    if (modelProduct.getId().equals(userId)){
                        if (Objects.requireNonNull(modelProduct).getBrandname().toLowerCase().contains(s.toLowerCase()) || modelProduct.getProductname().contains(s.toLowerCase())  ){
                            productList.add(modelProduct);
                        }
                    }
                    adapters.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCommentno() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelProduct post = snapshot.getValue(ModelProduct.class);
                    if (Objects.requireNonNull(post).getId().equals(userId)) {
                        i++;
                    }
                }
                binding.numbers.setText(""+i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    assert modelProduct != null;
                    if (modelProduct.getId().equals(userId)){
                        productList.add(modelProduct);
                    }
                    adapters.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}