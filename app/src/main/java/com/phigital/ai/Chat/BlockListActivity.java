package com.phigital.ai.Chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterBlockList;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityBlockedUsersBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockListActivity extends BaseActivity implements View.OnClickListener {

    ActivityBlockedUsersBinding binding;
    private List<ModelUser> userList;
    ConstraintLayout unblock;
    String userId;
     FirebaseAuth mAuth;
    BottomSheetDialog bottomSheetDialog;
    AdapterBlockList adapterPost;
    SharedPref sharedPref;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blocked_users);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//               String dbImage = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
//                try {
//                    Picasso.get().load(dbImage).into(binding.circleImageView3);
//                }
//                catch (Exception e ){
//                    Picasso.get().load(R.drawable.placeholder).into(binding.circleImageView3);
//                }
//                binding.circleImageView3.setOnClickListener(v -> {
//                    Intent intent = new Intent(BlockListActivity.this, UserProfile.class);
//                    startActivity(intent);
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BlockListActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        binding.pg.setVisibility(View.VISIBLE);
        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.savedrv.setHasFixedSize(true);
        binding.savedrv.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapterPost = new AdapterBlockList(this, userList);
        binding.savedrv.setAdapter(adapterPost);
        createBottomSheetDialog();
        setCommentno();
        binding.more.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Blocklist");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String hisUid = ""+ ds.getRef().getKey();
                    getUsers(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsers(String hisUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("id").equalTo(hisUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    userList.add(modelUser);
                }
                adapterPost = new AdapterBlockList(BlockListActivity.this, userList);
                binding.savedrv.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.unblock_bottom_sheet, null);
            unblock = view.findViewById(R.id.unblock);

            unblock.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

//    private void loadPost() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(userId);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                postList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelBlocklist modelPost = ds.getValue(ModelBlocklist.class);
//                    if (Objects.requireNonNull(modelPost).getId().equals(userId)){
//                        postList.add(modelPost);
//                    }
//                    adapterPost = new AdapterBlockList(BlockListActivity.this, postList);
//                    adapterPost.notifyDataSetChanged();
//                    binding.savedrv.setAdapter(adapterPost);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void setCommentno() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Blocklist").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                    binding.textView10.setText("0");
                } else {
                    binding.textView10.setText(snapshot.getChildrenCount()+"");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.unblock:
                bottomSheetDialog.cancel();
                unblockeveryone();
                break;
        }
    }

    private void unblockeveryone() {
        Query query = FirebaseDatabase.getInstance().getReference("Blocklist").child(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue().addOnSuccessListener(aVoid -> userList.clear());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}