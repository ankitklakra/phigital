package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterProductImages;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelProduct;
import com.phigital.ai.R;
import com.phigital.ai.Shop.CartDetails;
import com.phigital.ai.Shop.CustomerInvoice;
import com.phigital.ai.Shop.CustomerOrders;
import com.phigital.ai.Shop.MyOrders;
import com.phigital.ai.Shop.MyProductActivity;
import com.phigital.ai.Shop.ShippingOrders;
import com.phigital.ai.databinding.FragmentShopBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ShopFragment extends Fragment {

    FragmentShopBinding binding;

    AdapterProductImages adapterPost;
    List<ModelProduct> postList;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrenPage = 1;
    BottomSheetDialog productbottomsheet;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        binding.pg.setVisibility(View.VISIBLE);
        postList= new ArrayList<>();
        loadPost();
        binding.nm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        binding.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartDetails.class);
                startActivity(intent);
            }
        });
        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               productbottomsheet.show();
            }
        });

        binding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                loadPost();
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
                    filterproduct(s.toString().toLowerCase());
                }else {
                    loadPost();
                }
            }
        });
        addBottomSheet();
    }

    private void addBottomSheet() {
        if (productbottomsheet == null){
           @SuppressLint("InflateParams") View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.product_bottom_sheet, null);
            ConstraintLayout tshirt = view.findViewById(R.id.tshirt);
            ConstraintLayout woodenitems = view.findViewById(R.id.woodenitems);
            ConstraintLayout allitems = view.findViewById(R.id.allitems);
            ConstraintLayout orders = view.findViewById(R.id.orders);
            ConstraintLayout shipping = view.findViewById(R.id.shipping);
            ConstraintLayout invoice = view.findViewById(R.id.invoice);
            ConstraintLayout myorders = view.findViewById(R.id.myorders);
            ConstraintLayout myproducts = view.findViewById(R.id.myproducts);
            tshirt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.searchView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
                    Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
                    q.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            postList.clear();
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ModelProduct modelPost = ds.getValue(ModelProduct.class);
                                assert modelPost != null;
                                if (modelPost.getCategory().equals("apparel")&&modelPost.getAvailable().equals("true")){
                                    postList.add(modelPost);
                                }
                                adapterPost = new AdapterProductImages(getActivity(), postList);
                                binding.searchView.setAdapter(adapterPost);
                                adapterPost.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            woodenitems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.searchView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Product");
                    Query q2 = ref2.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
                    q2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            postList.clear();
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ModelProduct modelPost = ds.getValue(ModelProduct.class);
                                assert modelPost != null;
                                if (modelPost.getCategory().equals("wooden items")&&modelPost.getAvailable().equals("true")){
                                    postList.add(modelPost);
                                }
                                adapterPost = new AdapterProductImages(getActivity(), postList);
                                binding.searchView.setAdapter(adapterPost);
                                adapterPost.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            allitems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   loadPost();
                }
            });
            orders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CustomerOrders.class);
                    startActivity(intent);
                }
            });
            shipping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShippingOrders.class);
                    startActivity(intent);
                }
            });
            invoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CustomerInvoice.class);
                    startActivity(intent);
                }
            });
            myorders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MyOrders.class);
                    startActivity(intent);
                }
            });
            myproducts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent4 = new Intent(getContext(), MyProductActivity.class);
                    startActivity(intent4);
                }
            });
            FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            myproducts.setVisibility(View.VISIBLE);
                            shipping.setVisibility(View.VISIBLE);
                            orders.setVisibility(View.VISIBLE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            productbottomsheet = new BottomSheetDialog(binding.getRoot().getContext());
            productbottomsheet.setContentView(view);
        }
    }

    private void filterproduct(String s) {
        binding.searchView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelPost = ds.getValue(ModelProduct.class);
                    assert modelPost != null;
                    if (Objects.requireNonNull(modelPost).getBrandname().toLowerCase().contains(s.toLowerCase()) || modelPost.getProductname().contains(s.toLowerCase()) || modelPost.getDiscount().contains(s.toLowerCase()) ){
                        postList.add(modelPost);
                    }
                    adapterPost = new AdapterProductImages(getActivity(), postList);
                    binding.searchView.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadPost() {
        binding.searchView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelPost = ds.getValue(ModelProduct.class);
                    if (Objects.requireNonNull(modelPost).getAvailable().equals("true")){
                       postList.add(modelPost);
                    }
                    adapterPost = new AdapterProductImages(getActivity(), postList);
                    binding.searchView.setAdapter(adapterPost);
//                    binding.pg.setVisibility(View.GONE);
                    adapterPost.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
