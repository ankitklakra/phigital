package com.phigital.ai.Job;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.phigital.ai.MainActivity;
import com.phigital.ai.R;

import java.util.ArrayList;

public class ArticlervAdapter extends RecyclerView.Adapter<ArticlervAdapter.ViewHolder> {
    public MainActivity activity;
    private final ArrayList<String> mtext3;
    final Context mContext ;

    public ArticlervAdapter(ArrayList<String> mtext3, Context context) {
        this.mtext3 = mtext3;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articlerv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(mtext3.get(position));

        holder.itemView.setOnClickListener(v -> {
            if (holder.text.getText().equals("Business")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Education")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Entertainment")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Food")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Fashion")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("History")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Health")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Literature")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Media")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Politics")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Science")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Sports")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Technology")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
            if (holder.text.getText().equals("Others")){
                Intent intent = new Intent(holder.rootview.getContext(), ArticleSearch.class);
                intent.putExtra("title", mtext3.get(position));
                holder.rootview.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mtext3.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView num;
        View rootview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.texttv);
            num = itemView.findViewById(R.id.numtv);
            rootview = itemView;
        }
    }
}
