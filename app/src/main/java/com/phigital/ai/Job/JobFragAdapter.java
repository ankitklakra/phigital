package com.phigital.ai.Job;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelWork;
import com.phigital.ai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JobFragAdapter extends RecyclerView.Adapter<JobFragAdapter.ViewHolder> {
    public MainActivity activity;
    private final ArrayList<String> mtext;
    private final ArrayList<String> mnum;
    final Context mContext ;
    private final DatabaseReference postnumref;

    public JobFragAdapter(ArrayList<String> mtext, ArrayList<String> mnum, Context context) {
        this.mtext = mtext;
        this.mnum = mnum;
        this.mContext = context;
        postnumref = FirebaseDatabase.getInstance().getReference().child("Hire");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xyz, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(mtext.get(position));
        holder.num.setText(mnum.get(position));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.rootview.getContext(), SingleSearch.class);
            intent.putExtra("title", mtext.get(position));
            holder.rootview.getContext().startActivity(intent);
        });

        if (holder.text.getText().equals("Private")){
            privatenum(holder,position);
        }
        if (holder.text.getText().equals("Skilled")){
            skillednum(holder,position);
        }
        if (holder.text.getText().equals("Assistant")){
            assistantnum(holder,position);
        }
        if (holder.text.getText().equals("Freelancing")){
            freelancingnum(holder,position);
        }
        if (holder.text.getText().equals("Government")){
            governmentnum(holder,position);
        }
        if (holder.text.getText().equals("Others")){
            othersnum(holder,position);
        }

    }

    private void othersnum(ViewHolder holder, int position) {
        postnumref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelWork> userList;
                userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getJobtype().equals("Others")) {
                        userList.add(modelUser);
                    }
                    int num = userList.size();
                    if (String.valueOf(num).equals("0")) {
                        holder.num.setText("0");
                    } else {
                        holder.num.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void governmentnum(ViewHolder holder, int position) {
        postnumref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelWork> userList;
                userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getJobtype().equals("Government")) {
                        userList.add(modelUser);
                    }
                    int num = userList.size();
                    if (String.valueOf(num).equals("0")) {
                        holder.num.setText("0");
                    } else {
                        holder.num.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void freelancingnum(ViewHolder holder, int position) {
        postnumref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelWork> userList;
                userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getJobtype().equals("Freelancing")) {
                        userList.add(modelUser);
                    }
                    int num = userList.size();
                    if (String.valueOf(num).equals("0")) {
                        holder.num.setText("0");
                    } else {
                        holder.num.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void assistantnum(ViewHolder holder, int position) {
        postnumref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelWork> userList;
                userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getJobtype().equals("Assistant")) {
                        userList.add(modelUser);
                    }
                    int num = userList.size();
                    if (String.valueOf(num).equals("0")) {
                        holder.num.setText("0");
                    } else {
                        holder.num.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void skillednum(ViewHolder holder, int position) {
        postnumref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelWork> userList;
                userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getJobtype().equals("Skilled")) {
                        userList.add(modelUser);
                    }
                    int num = userList.size();
                    if (String.valueOf(num).equals("0")) {
                        holder.num.setText("0");
                    } else {
                        holder.num.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void privatenum(ViewHolder holder, int position) {
        postnumref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelWork> userList;
                userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelWork modelUser = ds.getValue(ModelWork.class);
                    if (Objects.requireNonNull(modelUser).getJobtype().equals("Private")) {
                        userList.add(modelUser);
                    }
                    int num = userList.size();
                    if (String.valueOf(num).equals("0")) {
                        holder.num.setText("0");
                    } else {
                        holder.num.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mtext.size();
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
