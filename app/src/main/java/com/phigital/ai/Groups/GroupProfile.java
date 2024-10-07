package com.phigital.ai.Groups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterMembers;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.Utility.UserProfile;
import com.phigital.ai.Utility.MediaView;
import com.phigital.ai.databinding.ActivityGroupProfileBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

@SuppressWarnings("ALL")
public class GroupProfile extends BaseActivity {

    //Firebase
    FirebaseAuth mAuth;
    ActivityGroupProfileBinding binding;
    //    TextView mUsername, mName,noMemeber,creator;
//    CircleImageView circularImageView;
//    TextView bio, link;
//    RelativeLayout bio_layout, web_layout,relativeLayout18;
//    ProgressBar pb;
//    ConstraintLayout main;
//    RecyclerView recyclerView;
    Context context=  GroupProfile.this;
    String GroupId, myGroupRole;
    //    TextView mCreator,mAdmin,mMember;
    String userId;
    private  String createdBy;
    //    ImageView imageView3;
    SharedPref sharedPref;
    private ArrayList<ModelUser> userArrayList;
    private AdapterMembers adapterParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_profile);
//        setContentView(R.layout.activity_group_profile);

        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //view
//        mUsername = findViewById(R.id.textView1);
//        mCreator = findViewById(R.id.textView3);
//        mAdmin = findViewById(R.id.textView5);
//        mMember = findViewById(R.id.textView7);
//        imageView3 = findViewById(R.id.imageView3);

        binding.imageView3.setOnClickListener(v -> onBackPressed());
//        mName = findViewById(R.id.groupname);
//        noMemeber = findViewById(R.id.noMemeber);
//        imageView4 = findViewById(R.id.imageView4);
//        circularImageView = findViewById(R.id.circularImageView);
//        pb = findViewById(R.id.pb);
        binding.pb.setVisibility(View.VISIBLE);
//        recyclerView = findViewById(R.id.postView);
//        relativeLayout18 = findViewById(R.id.relativeLayout18);
//        main = findViewById(R.id.main);

        //view
//        bio = findViewById(R.id.bio);
//        link = findViewById(R.id.link);
//        creator = findViewById(R.id.creator);
//        bio_layout = findViewById(R.id.bio_layout);
//        web_layout = findViewById(R.id.web_layout);

        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");
        mAuth = FirebaseAuth.getInstance();
        loadGroupInfo();
        loadMyInfo();
        getMembers();
        loadMembers2(userId,GroupId);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "groupadmin"
        ));

        FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.notificationimage.setImageResource(R.drawable.ic_notifications_active);
                    binding.counttv.setText(String.valueOf(snapshot.getChildrenCount()));
                    binding.counttv.setVisibility(View.VISIBLE);
                }else {
                    binding.counttv.setVisibility(View.GONE);
                    binding.notificationimage.setImageResource(R.drawable.icon_notification2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String usernam = intent.getStringExtra("item");
            binding.textView5.setText(usernam);
        }
    };

    private void getMembers() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Groups").child(GroupId).child("Participants");
        reference1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.textView7.setText(""+dataSnapshot.getChildrenCount() + " members");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants").orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child("role").getValue();
                            switch (myGroupRole) {
                                case "participant":
                                    binding.bell.setVisibility(View.GONE);
                                    binding.usergroup.setVisibility(View.VISIBLE);
                                    break;
                                case "admin":
                                    binding.bell.setVisibility(View.GONE);
                                    binding.usergroup.setVisibility(View.VISIBLE);
                                    break;
                                case "creator":
                                    binding.bell.setVisibility(View.VISIBLE);
                                    binding.usergroup.setVisibility(View.GONE);
                                    binding.bell.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(GroupProfile.this,GroupNotificationScreen.class);
                                            intent.putExtra("GroupId",GroupId);
                                            startActivity(intent);
                                        }
                                    });
                                    break;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(GroupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String groupId = ""+ds.child("groupId").getValue();
                    String gName = ""+ds.child("gName").getValue();
                    String gUsername = ""+ds.child("gUsername").getValue();
                    String gBio = ""+ds.child("gBio").getValue();
                    String gIcon = ""+ds.child("gIcon").getValue();
                    String gLink = ""+ds.child("gLink").getValue();
                    String stamp = ""+ds.child("timestamp").getValue();
                    createdBy = ""+ds.child("createdBy").getValue();

                    if (userId.equals(createdBy)){
                        binding.bell.setVisibility(View.VISIBLE);
                    }else{
                        binding.bell.setVisibility(View.GONE);
                    }

                    loadCreatorInfo(createdBy);

                    binding.textView3.setOnClickListener(v -> {
                        Intent intent = new Intent(context, UserProfile.class);
                        intent.putExtra("hisUid", createdBy);
                        context.startActivity(intent);
                    });
                    binding.textView5.setOnClickListener(v -> {
                        Intent intent = new Intent(context, UserProfile.class);
                        intent.putExtra("hisUid", createdBy);
                        context.startActivity(intent);
                    });

                    binding.groupname.setText(gName);
                    binding.textView1.setText(gUsername);

                    binding.textView10.setText(gBio);
                    binding.textView8.setText(gLink);
                    binding.textView8.setOnClickListener(v -> {
                        Intent intent = new Intent (Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY,gLink);
                        context.startActivity(intent);
                    });
                    try {
                        Picasso.get().load(gIcon).placeholder(R.drawable.group).into(binding.circularImageView);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.group).into(binding.circularImageView);
                    }

                    binding.circularImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(GroupProfile.this, MediaView.class);
                        intent.putExtra("type","image");
                        intent.putExtra("uri",gIcon);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });

                    String ed_text = binding.textView10.getText().toString().trim();
                    if (ed_text.length() > 0) {
                        binding.bioly.setVisibility(View.VISIBLE);
                    } else {
                        binding.bioly.setVisibility(View.GONE);
                    }
                    String ed_link = binding.textView8.getText().toString().trim();
                    if (ed_link.length() > 0) {
                        binding.linkly.setVisibility(View.VISIBLE);
                    } else {
                        binding.linkly.setVisibility(View.GONE);
                    }

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    long m = Long.parseLong(stamp);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(m);

                    String t =(formatter.format(calendar.getTime()));
                    binding.textView9.setText(t);

                    loadMembers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadMembers() {
        userArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelUser modelUser = ds.getValue(ModelUser.class);
                                userArrayList.add(modelUser);
                            }
                            adapterParticipants = new AdapterMembers(GroupProfile.this, userArrayList, GroupId, myGroupRole);
                            binding.postView.setAdapter(adapterParticipants);
                            binding.pb.setVisibility(View.GONE);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMembers2(String userId,String GroupId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    binding.imageView4.setVisibility(View.GONE);
                    binding.imageView0.setVisibility(View.VISIBLE);
                    binding.imageView0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupProfile.this);
                            builder2.setTitle("Leave group");
                            builder2.setMessage("Are you sure to leave this group?");
                            builder2.setPositiveButton("Leave", (dialog, which) -> {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                                reference.child(GroupId).child("Participants").child(userId).removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("You left group")
                                                    .textColor(Color.WHITE)
                                                    .textBold()
                                                    .gravity(0)
                                                    .length(2000)
                                                    .solidBackground()
                                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                    .show();
                                            sendnotification2(userId);
                                        }).addOnFailureListener(e -> {
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text(e.getMessage())
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .gravity(0)
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                });
                            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                            builder2.create().show();
                        }
                    });
                } else {
                    binding.imageView4.setVisibility(View.VISIBLE);
                    binding.imageView0.setVisibility(View.GONE);
                    binding.imageView4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupProfile.this);
                            builder2.setTitle("Join group");
                            builder2.setMessage("Are you sure to join this group?");
                            builder2.setPositiveButton("Join", (dialog, which) -> {
                                String timestamp = "" + System.currentTimeMillis();
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userId);
                                hashMap.put("role", "participant");
                                hashMap.put("timestamp", "" + timestamp);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(GroupId).child("Participants").child(userId).setValue(hashMap)
                                        .addOnSuccessListener(aVoid -> {
                                            new StyleableToast
                                                    .Builder(getApplicationContext())
                                                    .text("You joined this group")
                                                    .textColor(Color.WHITE)
                                                    .textBold()
                                                    .gravity(0)
                                                    .length(2000)
                                                    .solidBackground()
                                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                    .show();
                                            sendnotification(userId);
                                        }).addOnFailureListener(e -> {
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text(e.getMessage())
                                            .textColor(Color.WHITE)
                                            .textBold()
                                            .gravity(0)
                                            .length(2000)
                                            .solidBackground()
                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                            .show();
                                });
                            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                            builder2.create().show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendnotification(String userid) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", "");
        hashMap.put("notification", "Joined this group");
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("GroupNotifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Count").child(timestamp).setValue(true);
            }
        });

    }

    private void sendnotification2(String userId) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", "");
        hashMap.put("notification", "Left this group");
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("GroupNotifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Count").child(timestamp).setValue(true);
            }
        });
    }

    private void loadCreatorInfo(String createdBy) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("id").equalTo(createdBy).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    binding. textView3.setText(name);
                    binding. textView5.setText(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}