package com.phigital.ai.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.phigital.ai.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HorizontalRecyclerView extends RecyclerView.Adapter<HorizontalRecyclerView.HorizontalViewHolder> {

    private ArrayList<Uri> uri;

    public HorizontalRecyclerView(ArrayList<Uri> uri) {
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
//        horizontalViewHolder.mImageRecyclerView.setImageURI(uri.get(position));

        horizontalViewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("remove");
                intent.putExtra("removephoto", String.valueOf(position));
                LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
            }
        });

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
