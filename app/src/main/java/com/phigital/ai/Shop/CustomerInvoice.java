package com.phigital.ai.Shop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterInvoicelist;
import com.phigital.ai.Model.ModelCustomerOrder;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityInvoicesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerInvoice extends AppCompatActivity {
    SharedPref sharedPref;

    String id,productId,userId;
    AdapterInvoicelist adapters;
    ActivityInvoicesBinding binding;
    List<ModelCustomerOrder> userList ;
    List<ModelCustomerOrder> pList ;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrenPage = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoices);
        binding.back.setOnClickListener(v -> onBackPressed());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userList = new ArrayList<>();
        pList = new ArrayList<>();



        adapters = new AdapterInvoicelist(CustomerInvoice.this,userList);
        binding.invoiceView.setHasFixedSize(true);
        binding.invoiceView.setLayoutManager(new LinearLayoutManager(CustomerInvoice.this,LinearLayoutManager.VERTICAL, false));
        binding.invoiceView.setAdapter(adapters);
        binding.cons3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchheader.setVisibility(View.VISIBLE);
                binding.header.setVisibility(View.GONE);
            }
        });
        binding.back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchheader.setVisibility(View.GONE);
                binding.header.setVisibility(View.VISIBLE);
            }
        });

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterInvoice(s.toString().toLowerCase());
                }else {
                    readSave();
                }
            }
        });
        setCommentno();
        readSave();

        binding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                readSave();
            }
        });
    }

    private void filterInvoice(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Invoice");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelCustomerOrder modelPost = ds.getValue(ModelCustomerOrder.class);
                    if (Objects.requireNonNull(modelPost).getId().equals(userId)){
                        if (modelPost.getInvoicedate().toLowerCase().contains(query.toLowerCase()) || modelPost.getpId().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelPost);
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Invoice");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelCustomerOrder modelPost = snapshot1.getValue(ModelCustomerOrder.class);
                    if (Objects.requireNonNull(modelPost).getId().equals(userId)){
                        pList.add(modelPost);
                    }
                    binding.numbers.setText(String.valueOf(pList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Invoice");
        Query q = reference2.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelCustomerOrder modelPost = snapshot1.getValue(ModelCustomerOrder.class);
                    if (Objects.requireNonNull(modelPost).getId().equals(userId)){
                        userList.add(modelPost);
                    }
                }
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}