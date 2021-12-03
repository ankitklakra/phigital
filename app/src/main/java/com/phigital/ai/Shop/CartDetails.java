package com.phigital.ai.Shop;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterProductCart;
import com.phigital.ai.Model.ModelCart;
import com.phigital.ai.Model.ModelProduct;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityCartBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartDetails extends AppCompatActivity {

    SharedPref sharedPref;
    String userId;
    AdapterProductCart adapters;
    ActivityCartBinding binding;
    List<ModelCart> productList ;
    List<String> pList ;
    List<Integer> nList ;
    boolean cart = true;
    int sum = 0;
    String numOfLikes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        binding.back.setOnClickListener(v -> onBackPressed());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        productList = new ArrayList<>();
        pList = new ArrayList<>();
        nList = new ArrayList<>();


        adapters = new AdapterProductCart(CartDetails.this,productList);
        binding.cartView.setHasFixedSize(true);
        binding.cartView.setLayoutManager(new LinearLayoutManager(CartDetails.this,LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(adapters);

        mySaved();
        setCommentno();
    }

    private void setCommentno() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    pList.add(snapshot1.getKey());
                }
                readSave();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference d =  FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
        d.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                nList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ModelCart post1 = postSnapshot.getValue(ModelCart.class);
                    String a = Objects.requireNonNull(post1).getFinalprice();
                    if (null != a){
                        nList.add(Integer.parseInt(a));
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Product");
        reference2.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelCart post2 = snapshot1.getValue(ModelCart.class);
                    for (String id: pList){
                        if (Objects.requireNonNull(post2).getpId().equals(id)) {
                            productList.add(post2);
                            sum = nList.stream().mapToInt(Integer::intValue).sum();
                            binding.amount.setText("₹ " +sum);
                        }
                    }

                }
                binding.cons3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    ModelProduct post2 = snapshot1.getValue(ModelProduct.class);
                                    for (String id : pList) {
                                        if (Objects.requireNonNull(post2).getpId().equals(id)) {
                                            if (Objects.requireNonNull(post2).getAvailable().equals("false")) {
                                                cart = false;
                                            }
                                        }
                                    }
                                }

                                if (cart){
                                    String am = String.valueOf(sum);
                                    Intent intent = new Intent(CartDetails.this, EditAddressActivity.class);
                                    intent.putExtra("amount",am);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(CartDetails.this, "Please remove out of stock items from cart", Toast.LENGTH_SHORT).show();
                                    cart = false;
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });


                    }
                });
                adapters.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}