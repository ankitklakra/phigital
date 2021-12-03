package com.phigital.ai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.R;

import java.util.ArrayList;
import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.NewViewHolder> {
    List<Users> usersList;
    Context context;

    public NewAdapter( Context context) {
        this.usersList = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<Users> newUsers) {
        int initsize=usersList.size();
        usersList.addAll(newUsers);
        notifyItemRangeChanged(initsize,newUsers.size());
    }


    public static class NewViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);

        }
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.user_round,parent,false);
        return new NewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        holder.name.setText(usersList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


}
