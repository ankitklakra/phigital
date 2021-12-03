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
import com.phigital.ai.Model.ModelWork;
import com.phigital.ai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JobFragAdapter2 extends RecyclerView.Adapter<JobFragAdapter2.ViewHolder> {
    private final ArrayList<String> mtext2;
    private final ArrayList<String> mnum2;
    final Context mContext ;
    private final DatabaseReference postnumref;
    public JobFragAdapter2(ArrayList<String> mtext2, ArrayList<String> mnum2, Context context) {
        this.mtext2 = mtext2;
        this.mnum2 = mnum2;
        this.mContext = context;
        postnumref = FirebaseDatabase.getInstance().getReference().child("Work");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xyz2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text2.setText(mtext2.get(position));
        holder.num2.setText(mnum2.get(position));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.rootview.getContext(), SingleSearch2.class);
            intent.putExtra("title", mtext2.get(position));
            holder.rootview.getContext().startActivity(intent);
        });
        if (holder.text2.getText().equals("Private")){
            privatenum(holder,position);
        }
        if (holder.text2.getText().equals("Skilled")){
            skillednum(holder,position);
        }
        if (holder.text2.getText().equals("Assistant")){
            assistantnum(holder,position);
        }
        if (holder.text2.getText().equals("Freelancing")){
            freelancingnum(holder,position);
        }
        if (holder.text2.getText().equals("Government")){
            governmentnum(holder,position);
        }
        if (holder.text2.getText().equals("Others")){
            othersnum(holder,position);
        }
    }
    private void othersnum(JobFragAdapter2.ViewHolder holder, int position) {
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
                        holder.num2.setText("0");
                    } else {
                        holder.num2.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void governmentnum(JobFragAdapter2.ViewHolder holder, int position) {
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
                        holder.num2.setText("0");
                    } else {
                        holder.num2.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void freelancingnum(JobFragAdapter2.ViewHolder holder, int position) {
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
                        holder.num2.setText("0");
                    } else {
                        holder.num2.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void assistantnum(JobFragAdapter2.ViewHolder holder, int position) {
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
                        holder.num2.setText("0");
                    } else {
                        holder.num2.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void skillednum(JobFragAdapter2.ViewHolder holder, int position) {
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
                        holder.num2.setText("0");
                    } else {
                        holder.num2.setText(String.valueOf(num));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void privatenum(JobFragAdapter2.ViewHolder holder, int position) {
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
                        holder.num2.setText("0");
                    } else {
                        holder.num2.setText(String.valueOf(num));
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
        return mtext2.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text2;
        TextView num2;
        View rootview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text2 = itemView.findViewById(R.id.texttv2);
            num2 = itemView.findViewById(R.id.numtv2);
            rootview = itemView;
        }
    }
}
