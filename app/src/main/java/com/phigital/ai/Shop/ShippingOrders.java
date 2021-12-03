package com.phigital.ai.Shop;

import android.os.Bundle;

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
import com.phigital.ai.Adapter.AdapterShippingUser;
import com.phigital.ai.Model.ModelCustomerOrder;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityOrder2Binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShippingOrders extends AppCompatActivity {
    SharedPref sharedPref;

    String id,productId,userId;
    AdapterShippingUser adapters;
    ActivityOrder2Binding binding;
    List<ModelCustomerOrder> userList ;
    List<String> pList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order2);
        binding.back.setOnClickListener(v -> onBackPressed());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userList = new ArrayList<>();
        pList = new ArrayList<>();

        adapters = new AdapterShippingUser(ShippingOrders.this,userList);
        binding.cartView.setHasFixedSize(true);
        binding.cartView.setLayoutManager(new LinearLayoutManager(ShippingOrders.this,LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(adapters);
        binding.text.setText("Shippings");
        mySaved();
        setCommentno();
        readSave();
    }

    private void setCommentno() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShippingOrders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                    binding.numbers.setText("0");
                } else {
                    binding.numbers.setText(snapshot.getChildrenCount()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mySaved() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShippingOrders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    pList.add(snapshot1.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShippingOrders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelCustomerOrder modelPost = snapshot1.getValue(ModelCustomerOrder.class);
                    userList.add(modelPost);
                }
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}