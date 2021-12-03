package com.phigital.ai.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.R;
import com.phigital.ai.Utility.MediaView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailRecyclerView extends RecyclerView.Adapter<ProductDetailRecyclerView.HorizontalViewHolder> {

    private final ArrayList<Uri> uri;

    public ProductDetailRecyclerView(ArrayList<Uri> uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gridsquarephoto, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder horizontalViewHolder, int position) {

        try {
            Picasso.get().load(uri.get(position)).placeholder(R.drawable.placeholder).into(horizontalViewHolder.mImageRecyclerView);
        } catch (Exception ignored) {

        }
        horizontalViewHolder.close.setVisibility(View.GONE);

        Uri a = uri.get(position);
        horizontalViewHolder.mImageRecyclerView.setOnClickListener(v -> {
                    Intent intent = new Intent(horizontalViewHolder.mImageRecyclerView.getContext(), MediaView.class);
                    intent.putExtra("type","image");
                    intent.putExtra("uri",String.valueOf(a));
                    horizontalViewHolder.mImageRecyclerView.getContext().startActivity(intent);
        }
        );
    }


    @Override
    public int getItemCount() {
        return uri.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageRecyclerView;
        ImageView close;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            mImageRecyclerView = itemView.findViewById(R.id.gridimage);
            close = itemView.findViewById(R.id.close);
        }
    }
}
