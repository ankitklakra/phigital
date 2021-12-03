package com.phigital.ai.Groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterNotify;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelNotification;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GroupNotificationScreen extends BaseActivity {

    String userId,GroupId;
    FirebaseAuth mAuth;
    ProgressBar pg;
    RecyclerView recyclerView;
    ImageView back;
    TextView textView10;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelNotification> notifications;
    private AdapterNotify adapterNotify;
    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
        pg = findViewById(R.id.pg);
        textView10 = findViewById(R.id.textView10);
        pg.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        GroupId = intent.getStringExtra("GroupId");
        recyclerView = findViewById(R.id.users);
        firebaseAuth = FirebaseAuth.getInstance();
        getAllNotifications();
        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupNotificationScreen.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        getnotificationnumber();
        FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Count").getRef().removeValue();

    }

    private void getnotificationnumber() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("GroupNotifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                        if (numOfLikes.equals("0")) {
                            textView10.setText("0");
                        } else {
                            textView10.setText(String.valueOf(snapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllNotifications() {
        notifications = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("GroupNotifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelNotification modelNotification = ds.getValue(ModelNotification.class);
                            notifications.add(modelNotification);
                        }
                        adapterNotify = new AdapterNotify(GroupNotificationScreen.this, notifications);
                        recyclerView.setAdapter(adapterNotify);
                        pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}