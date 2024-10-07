package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Model.ModelProduct;
import com.phigital.ai.R;
import com.phigital.ai.Shop.UpdateProduct;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class AdapterMyProduct extends RecyclerView.Adapter<AdapterMyProduct.MyHolder> {

    Context context;
    final List<ModelProduct> productList;

    private String userId;

    public AdapterMyProduct(Context context, List<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.myproduct_cart, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String id = productList.get(position).getId();
        String pId = productList.get(position).getpId();

        String name = productList.get(position).getProductname();
        String price = productList.get(position).getPrice();

        String brandname = productList.get(position).getBrandname();
        String address = productList.get(position).getAddress();
        String discount = productList.get(position).getDiscount();
        String mrp = productList.get(position).getMrp();
        String saveamount = productList.get(position).getSaveamount();
        String available = productList.get(position).getAvailable();
//        String quantity = productList.get(position).getQuantity();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Product").child(pId).child("images");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String img = (String) dataSnapshot.child("ImgLink0").getValue();
                            try {
                                Picasso.get().load(img).placeholder(R.drawable.placeholder).into(holder.image);
                            } catch (Exception ignored) {

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                });

//        if (quantity.equals("0")) {
//            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Pro").child(userId).child(pId);
//            ref2.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                    for (DataSnapshot ds : snapshot.getChildren()) {
//                        ds.getRef().removeValue();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                }
//            });
//            Toast.makeText(context, "Item is removed from cart", Toast.LENGTH_SHORT).show();
//        }

        holder.name.setText(name);
        holder.brandname.setText(brandname);
        holder.mrp.setText("MRP "+"₹ "+mrp);
        holder.save.setText("₹ "+saveamount + " Save");
        holder.discount.setText("("+discount+")");
        holder.address.setText(address);
        holder.price.setText("₹ " +price);
//        holder.quantity.setText(quantity);
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.con.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateProduct.class);
            intent.putExtra("editPostId", pId);
            context.startActivity(intent);
        });

        if (available.equals("false")){
           holder.available.setText("Out of Stock");
        }else{
            holder.available.setText("Available");
        }

//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Cart");
//                Ref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
//                Query query = messageRef.orderByChild("pId").equalTo(pId);
//                ValueEventListener valueEventListener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                            ds.getRef().removeValue();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                };
//                query.addListenerForSingleValueEvent(valueEventListener);
//                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView image;
        final TextView name;
        final TextView price;
        final TextView brandname;
        final TextView mrp;
        final TextView save;
        final TextView discount;
        final TextView address;
        final TextView quantity;
        final ConstraintLayout con;
        final TextView available;
        final ImageView imageView1;
        final ImageView imageView2;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            brandname = itemView.findViewById(R.id.brandname);
            mrp = itemView.findViewById(R.id.mrp);
            save = itemView.findViewById(R.id.save);
            discount = itemView.findViewById(R.id.discount);
            address = itemView.findViewById(R.id.address);
            con = itemView.findViewById(R.id.con);
            quantity = itemView.findViewById(R.id.quantity);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView1 = itemView.findViewById(R.id.imageView1);
            available = itemView.findViewById(R.id.available);
        }
    }
}








