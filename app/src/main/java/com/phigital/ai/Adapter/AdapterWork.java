package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Job.WorkDetails;
import com.phigital.ai.Model.ModelWork;
import com.phigital.ai.R;
import com.phigital.ai.Chat.ChatActivity;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterWork extends RecyclerView.Adapter<AdapterWork.MyHolder>{
    final Context context;
    final List<ModelWork> userList;

    public AdapterWork(Context context, List<ModelWork> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_work, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUid = userList.get(position).getId();
        String userImage = userList.get(position).getDp();
        String edu = userList.get(position).getEducation();
        String expert = userList.get(position).getExpert();
        String salary = userList.get(position).getSalary();
        String wage = userList.get(position).getWage();
        String time = userList.get(position).getpTime();
        String expnum = userList.get(position).getExperiencenum();
        String exptime = userList.get(position).getExperiencetime();
        String nam = userList.get(position).getName();
        String city = userList.get(position).getCity();
        String pid = userList.get(position).getpId();

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(time);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
        holder.mTime.setText(lastSeenTime);

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.placeholder).into(holder.avatar);
        }catch (Exception ignored){

        }
        holder.avatar.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
        });

        holder.mssg.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("hisUid", hisUid);
            context.startActivity(intent);
        });
        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams()      ;
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.cons.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkDetails.class);
            intent.putExtra("postId", pid);
            context.startActivity(intent);
        });

        holder.expert.setText(expert);

        holder.edutv.setText(edu);

        holder.exptv.setText(exptime);

        holder.exptvnum.setText(expnum);

        holder.exptv.setOnClickListener(v -> holder.exptv.setMaxLines(Integer.MAX_VALUE));

        holder.edutv.setOnClickListener(v -> holder.edutv.setMaxLines(Integer.MAX_VALUE));

        holder.city.setText(city);

        holder.name.setText(nam);

        holder.salarynum.setText(salary);

        holder.salary.setText(wage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final ImageView mssg;
        final TextView edutv;
        final TextView exptvnum;
        final TextView expert;
        final TextView mTime;
        final TextView exptv;
        final TextView city;
        final TextView name;
        final TextView salary;
        final TextView salarynum;
        final ConstraintLayout cons;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circularImageView);
            mTime = itemView.findViewById(R.id.time);
            cons = itemView.findViewById(R.id.constraintLayout9);
            exptv = itemView.findViewById(R.id.experience);
            exptvnum = itemView.findViewById(R.id.experiencenum);
            expert = itemView.findViewById(R.id.expert);
            edutv = itemView.findViewById(R.id.education);
            city = itemView.findViewById(R.id.city);
            name = itemView.findViewById(R.id.name);
            mssg = itemView.findViewById(R.id.mssg);
            salary = itemView.findViewById(R.id.salary);
            salarynum = itemView.findViewById(R.id.salarynum);
        }

    }
}
