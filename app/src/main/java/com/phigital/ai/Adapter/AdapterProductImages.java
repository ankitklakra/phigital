package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.Model.ModelProduct;
import com.phigital.ai.R;
import com.phigital.ai.Shop.ProductDetails;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class AdapterProductImages extends RecyclerView.Adapter<AdapterProductImages.MyHolder> {

    Context context;
    final List<ModelProduct> productList;

    private String userId;

    public AdapterProductImages(Context context, List<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_photo, parent, false);
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

//        String pTime = productList.get(position).getpTime();
//        String text = productList.get(position).getText();
//        String vine = productList.get(position).getVine();
//        String type = productList.get(position).getType();
//        String pViews = productList.get(position).getpViews();

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

            holder.name.setText(name);

            holder.price.setText("₹ " +price);

        holder.image.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("productId", pId);
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView image;
        final TextView name;
        final TextView price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);

        }
    }
}








