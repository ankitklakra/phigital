package com.phigital.ai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Job.HireDetails;
import com.phigital.ai.Model.ModelHire;
import com.phigital.ai.R;
import com.phigital.ai.Chat.ChatActivity;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAdminHire extends RecyclerView.Adapter<AdapterAdminHire.MyHolder>{
    final Context context;
    final List<ModelHire> userList;

    public AdapterAdminHire(Context context, List<ModelHire> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_hire, parent, false);
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
        String pId = userList.get(position).getpId();
        String bname = userList.get(position).getBusiness();
        String hirenum = userList.get(position).getHire();

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

        holder.cons.setOnClickListener(v -> {
            Intent intent = new Intent(context, HireDetails.class);
            intent.putExtra("postId", pId);
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

        holder.cons.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.cons, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE,1,0, "Delete");
                popupMenu.getMenu().add(Menu.NONE,2,0, "Delete from report");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == 1){
                            deletePost(pId);
                            FirebaseDatabase.getInstance().getReference().child("HireReport").child(pId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }   else  if (id == 2){

                            FirebaseDatabase.getInstance().getReference().child("HireReport").child(pId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();

                                    Toast.makeText(context, "Hire Removed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

    }

    private void deletePost(String pId) {
        Query query = FirebaseDatabase.getInstance().getReference("Hire").orderByChild("pId").equalTo(pId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        final TextView business;
        final TextView hire;
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
            business = itemView.findViewById(R.id.businessname);
            hire = itemView.findViewById(R.id.hire);
        }


    }
}
