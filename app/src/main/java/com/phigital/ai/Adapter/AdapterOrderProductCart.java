package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.Model.ModelCart;

import com.phigital.ai.R;
import com.phigital.ai.Shop.ProductDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishabhharit.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class AdapterOrderProductCart extends RecyclerView.Adapter<AdapterOrderProductCart.MyHolder> {

    Context context;
    final List<ModelCart> productList;

    public AdapterOrderProductCart(Context context, List<ModelCart> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_cart2, parent, false);
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
        String quantity = productList.get(position).getQuantity();
        String category = productList.get(position).getCategory();
        String size = productList.get(position).getSize();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String  userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

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
        holder.brandname.setText(brandname);
        holder.mrp.setText("MRP "+"₹ "+mrp);
        holder.save.setText("₹ "+saveamount + " Save");
        holder.discount.setText("("+discount+")");

        holder.price.setText("₹ " +price);
        holder.quantity.setText(quantity);
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.con.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("productId", pId);
            context.startActivity(intent);
        });

        if (category.equals("apparel")){
            holder.sizetv.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.size.setText(size);
        }

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final RoundedImageView image;
        final TextView name;
        final TextView price;
        final TextView brandname;
        final TextView mrp;
        final TextView save;
        final TextView discount;
        final TextView size;
        final TextView quantity;
        final TextView sizetv;
        final ConstraintLayout con;
        final ConstraintLayout buttons;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            brandname = itemView.findViewById(R.id.brandname);
            mrp = itemView.findViewById(R.id.mrp);
            save = itemView.findViewById(R.id.save);
            discount = itemView.findViewById(R.id.discount);
            size = itemView.findViewById(R.id.size);
            con = itemView.findViewById(R.id.con);
            quantity = itemView.findViewById(R.id.quantity);
            buttons = itemView.findViewById(R.id.buttons);
            sizetv = itemView.findViewById(R.id.sizetv);
        }
    }
}








