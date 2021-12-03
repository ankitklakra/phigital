package com.phigital.ai.Shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.ProductDetailRecyclerView;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ProductDetailsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductDetails extends AppCompatActivity   {

    ArrayList<String> imageslist = new ArrayList<>();
    ArrayList<String> sizelist = new ArrayList<>();
    ArrayList<Uri> list = new ArrayList<>();
    private RequestQueue requestQueue;
    Context context = ProductDetails.this;
    SharedPref sharedPref;
    DatabaseReference productRef;

    private String userId, myName, myDp;
    private DatabaseReference mDatabase;
    String id,productId,myUsername;
    String hisId,hispId,hiscategory,hisproductname,hisbrandname,hismrp,hisdiscount,hisimages,hisprice,hissaveamount,hisaddress,hisdetail,hispTime,hisavailabilty;


    String size;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    ProductDetailRecyclerView adapters;
    Map<String,Object> td;
    Map<String,Object> vd;


    ProductDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.product_details);
        productRef = FirebaseDatabase.getInstance().getReference().child("Product");

        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        id = intent.getStringExtra("id");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        loadPostInfo();
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
        Query query = ref.orderByChild("pId").equalTo(productId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    hisId = ""+ds.child("id").getValue();
                    hispId = ""+ds.child("pId").getValue();
                    hiscategory = ""+ds.child("category").getValue();
                    hisproductname = ""+ds.child("productname").getValue();
                    hisbrandname = ""+ds.child("brandname").getValue();
                    hismrp = ""+ds.child("mrp").getValue();
                    hisdiscount = ""+ds.child("discount").getValue();
                    hisprice = ""+ds.child("price").getValue();
                    hissaveamount = ""+ds.child("saveamount").getValue();
                    hisaddress = ""+ds.child("address").getValue();
                    hisdetail = ""+ds.child("detail").getValue();
                    hispTime = ""+ds.child("pTime").getValue();
                    hisavailabilty = ""+ds.child("available").getValue();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Product").child(productId).child("images");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                    td = (HashMap<String,Object>) dataSnapshot.getValue();

                                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(td).entrySet()){
                                        imageslist.add((String)entry.getValue());
                                    }
                                    imageslist.forEach(s -> list.add(Uri.parse(s)));

                                    adapters = new ProductDetailRecyclerView(list);
                                    binding.photoView.setHasFixedSize(true);
                                    binding.photoView.setLayoutManager(new LinearLayoutManager(ProductDetails.this,LinearLayoutManager.HORIZONTAL, false));
                                    binding.photoView.setAdapter(adapters);
                                }

                                @Override
                                public void onCancelled(@NotNull DatabaseError databaseError) {
                                    //handle databaseError
                                }
                            });

                    if (hiscategory.equals("apparel")){
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Product").child(productId).child("sizes");
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                td = (HashMap<String,Object>) dataSnapshot.getValue();

                                for (Map.Entry<String, Object> entry : Objects.requireNonNull(td).entrySet()){
                                    sizelist.add((String)entry.getValue());
                                }

                                String[] sizes = sizelist.toArray(new String[sizelist.size()]);

                                for (String s : sizes) {
                                    if ("xs".equals(s)) {
                                        binding.xs.setVisibility(View.VISIBLE);
                                    }
                                    if ("s".equals(s)) {
                                        binding.s.setVisibility(View.VISIBLE);
                                    }
                                    if ("m".equals(s)) {
                                        binding.m.setVisibility(View.VISIBLE);
                                    }
                                    if ("l".equals(s)) {
                                        binding.l.setVisibility(View.VISIBLE);
                                    }
                                    if ("xl".equals(s)) {
                                        binding.xl.setVisibility(View.VISIBLE);
                                    }
                                    if ("xxl".equals(s)) {
                                        binding.xxl.setVisibility(View.VISIBLE);
                                    }
                                    if ("xxxl".equals(s)) {
                                        binding.xxxl.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NotNull DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
                    }

                    binding.name.setText(hisproductname);
                    binding.price.setText("₹ "+ hisprice);
                    binding.brandname.setText(hisbrandname);
                    binding.discount.setText("("+hisdiscount+")");
                    binding.save.setText("₹ "+hissaveamount + " Save");
                    binding.detail.setText(hisdetail);
                    binding.mrp.setText("MRP "+"₹ "+hismrp);
                    binding.mrp.setPaintFlags(binding.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    binding.address.setText(hisaddress);

                    binding.xs.setOnClickListener(v -> {
                        size = "xs";
                        Toast.makeText(ProductDetails.this, "xs is selected", Toast.LENGTH_SHORT).show();
                    });
                    binding.s.setOnClickListener(v -> {
                        size = "s";
                        Toast.makeText(ProductDetails.this, "s is selected", Toast.LENGTH_SHORT).show();
                    });
                    binding.m.setOnClickListener(v -> {
                        size = "m";
                        Toast.makeText(ProductDetails.this, "m is selected", Toast.LENGTH_SHORT).show();
                    });
                    binding.l.setOnClickListener(v -> {
                        size = "l";
                        Toast.makeText(ProductDetails.this, "l is selected", Toast.LENGTH_SHORT).show();
                    });
                    binding.xl.setOnClickListener(v -> {
                        size = "xl";
                        Toast.makeText(ProductDetails.this, "xl is selected", Toast.LENGTH_SHORT).show();
                    });
                    binding.xxl.setOnClickListener(v -> {
                        size = "xxl";
                        Toast.makeText(ProductDetails.this, "xxl is selected", Toast.LENGTH_SHORT).show();
                    });
                    binding.xxxl.setOnClickListener(v -> {
                        size = "xxxl";
                        Toast.makeText(ProductDetails.this, "xxxl is selected", Toast.LENGTH_SHORT).show();
                    });

                    if (hisavailabilty.equals("false")){
                        binding.buy.setText("Out of Stock");
                        binding.buy.setEnabled(false);
                        binding.cart.setEnabled(false);
                    }else{
                        binding.buy.setText("Buy now");
                        binding.buy.setEnabled(true);
                        binding.cart.setEnabled(true);
                    }

                    if (hiscategory.equals("apparel")){

                        binding.buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (size != null){
                                    DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("SingleCart");
                                    likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                DatabaseReference cartNodes = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
                                                final DatabaseReference ordernode = FirebaseDatabase.getInstance().getReference().child("SingleCart").child(userId).child(productId);
                                                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("SingleCart");
                                                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                        cartNodes.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NotNull DataSnapshot dataSnapshot){
                                                                ordernode.setValue(dataSnapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        HashMap<String , Object> hashMap = new HashMap<>();
                                                                        hashMap.put("quantity", "1");
                                                                        hashMap.put("finalprice", hisprice);
                                                                        hashMap.put("finalmrp", hismrp);
                                                                        hashMap.put("finalsave", hissaveamount);
                                                                        hashMap.put("size", size);
                                                                        likeRef.child(userId).child(productId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Intent intent = new Intent(ProductDetails.this,AddressActivity.class);
                                                                                intent.putExtra("productId",hispId);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                    }
                                                });
                                            }


                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                }else{
                                    Toast.makeText(ProductDetails.this, "Please select the size", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        binding.cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (size != null){
                                    DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Cart");
                                    likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if (snapshot.child(userId).hasChild(productId)) {
                                                    Toast.makeText(ProductDetails.this, "Already added to cart", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    DatabaseReference cartNodes = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
                                                    final DatabaseReference ordernode = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId).child(productId);
                                                    DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Cart");
                                                    likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                            cartNodes.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NotNull DataSnapshot dataSnapshot){
                                                                    ordernode.setValue(dataSnapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            HashMap<String , Object> hashMap = new HashMap<>();
                                                                            hashMap.put("quantity", "1");
                                                                            hashMap.put("finalprice", hisprice);
                                                                            hashMap.put("finalmrp", hismrp);
                                                                            hashMap.put("finalsave", hissaveamount);
                                                                            hashMap.put("size", size);
                                                                            likeRef.child(userId).child(productId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(ProductDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                }else{
                                    Toast.makeText(ProductDetails.this, "Please select the size", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    if (hiscategory.equals("wooden items")){

                        binding.buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("SingleCart");
                                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        DatabaseReference cartNodes = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
                                        final DatabaseReference ordernode = FirebaseDatabase.getInstance().getReference().child("SingleCart").child(userId).child(productId);
                                        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("SingleCart");
                                        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                cartNodes.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NotNull DataSnapshot dataSnapshot){
                                                        ordernode.setValue(dataSnapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                HashMap<String , Object> hashMap = new HashMap<>();
                                                                hashMap.put("quantity", "1");
                                                                hashMap.put("finalprice", hisprice);
                                                                hashMap.put("finalmrp", hismrp);
                                                                hashMap.put("finalsave", hissaveamount);
                                                                likeRef.child(userId).child(productId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Intent intent = new Intent(ProductDetails.this,AddressActivity.class);
                                                                        intent.putExtra("productId",hispId);
                                                                        startActivity(intent);
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                    }


                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        binding.cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Cart");
                                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        if (snapshot.child(userId).hasChild(productId)) {
                                            Toast.makeText(ProductDetails.this, "Already added to cart", Toast.LENGTH_SHORT).show();
                                        } else {
                                            DatabaseReference cartNodes = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
                                            final DatabaseReference ordernode = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId).child(productId);
                                            DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Cart");
                                            likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    cartNodes.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NotNull DataSnapshot dataSnapshot){
                                                            ordernode.setValue(dataSnapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    HashMap<String , Object> hashMap = new HashMap<>();
                                                                    hashMap.put("quantity", "1");
                                                                    hashMap.put("finalprice", hisprice);
                                                                    hashMap.put("finalmrp", hismrp);
                                                                    hashMap.put("finalsave", hissaveamount);
                                                                    likeRef.child(userId).child(productId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(ProductDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}