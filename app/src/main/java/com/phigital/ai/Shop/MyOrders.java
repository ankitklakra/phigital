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
import com.phigital.ai.Adapter.AdapterCustomerUser2;
import com.phigital.ai.Model.ModelCustomerOrder;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityOrder2Binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyOrders extends AppCompatActivity {
    SharedPref sharedPref;

    String id,productId,userId;
    AdapterCustomerUser2 adapters;
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

        adapters = new AdapterCustomerUser2(MyOrders.this,userList);
        binding.cartView.setHasFixedSize(true);
        binding.cartView.setLayoutManager(new LinearLayoutManager(MyOrders.this,LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(adapters);

        readSave();
    }

    private void readSave() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CustomerOrders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelCustomerOrder modelPost = snapshot1.getValue(ModelCustomerOrder.class);
                    if (Objects.requireNonNull(modelPost).getId().equals(userId)){
                        userList.add(modelPost);
                    }
                }
                int a = userList.size();
                if (String.valueOf(a).equals("0")) {
                    binding.numbers.setText("0");
                } else {
                    binding.numbers.setText(String.valueOf(a));
                }
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}