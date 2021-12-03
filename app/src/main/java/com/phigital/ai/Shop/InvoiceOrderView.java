package com.phigital.ai.Shop;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterOrderProductCart;
import com.phigital.ai.Model.ModelCart;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class InvoiceOrderView extends AppCompatActivity {

    ActivityViewBinding binding;
    SharedPref sharedPref;
    String hisOrderid,hisId;
    AdapterOrderProductCart adapters;
    List<ModelCart> productList;

    List<String> pList ;
    String hisarea,hiscity,hiscountry,hisfullname,hishouseno,hislandmark,hispincode,userId,hismobile,hisstate,hisid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Intent intent = getIntent();
        hisOrderid = intent.getStringExtra("hispId");
        hisid = intent.getStringExtra("hisId");

        productList = new ArrayList<>();
        pList = new ArrayList<>();

        adapters = new AdapterOrderProductCart(InvoiceOrderView.this,productList);
        binding.cartView.setHasFixedSize(true);
        binding.cartView.setLayoutManager(new LinearLayoutManager(InvoiceOrderView.this,LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(adapters);

        binding.done.setText("Invoice Orders");
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setnum(hisOrderid);
        loadPost(hisOrderid);

        DatabaseReference dREF = FirebaseDatabase.getInstance().getReference().child("Invoice").child(hisOrderid);
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

                binding.name.setText(hisfullname);
                binding.address.setText(hishouseno+" "+hisarea+" "+ hislandmark);
                binding.city.setText(hiscity +" - "+hispincode +" "+hisstate +" "+"("+hiscountry+")");
                binding.number.setText(hismobile);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadPost(String hisOrderid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Invoice").child(hisOrderid).child("Order");
//        Query query = ref.orderByChild("pId").equalTo(hisOrderid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pList.clear();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                    ModelCart post2 = snapshot1.getValue(ModelCart.class);
                    productList.add(post2);
                }
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setnum(String hisOrderid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ShippingOrders").child(hisOrderid).child("Order");
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

}