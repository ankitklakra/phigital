package com.phigital.ai.Utility;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterUsers;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewedActivity extends BaseActivity {

    List<String> idList;
    String id;

    ProgressBar pb;
    RecyclerView recyclerView;
    List<ModelUser> userList;
    AdapterUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed);
        recyclerView = findViewById(R.id.trendingRv);
        pb = findViewById(R.id.pb);
        id = getIntent().getStringExtra("id");
        pb.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(ViewedActivity.this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();

        ImageView imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        getViews(id);

    }

    private void getViews(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(id).child(Objects.requireNonNull(getIntent().getStringExtra("storyid"))).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    idList.add(snapshot1.getKey());
                }
                if (snapshot.getChildrenCount() == 0){
                    pb.setVisibility(View.GONE);
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);
                    if (modelUser != null){
                        for (String id : idList) {
                            if (Objects.requireNonNull(modelUser).getId().equals(id)){
                                userList.add(modelUser);
                            }
                            adapterUsers = new AdapterUsers(ViewedActivity.this, userList);
                            recyclerView.setAdapter(adapterUsers);
                            pb.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewedActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });
    }

}