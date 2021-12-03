package com.phigital.ai.Chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterGroupChat;
import com.phigital.ai.Adapter.AdapterSaveChat;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelGroupChat;
import com.phigital.ai.R;
import com.phigital.ai.Activity.ShareActivity;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityLineupFavouriteBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatSaveActivity extends BaseActivity implements View.OnClickListener {

    ActivityLineupFavouriteBinding binding;

    ConstraintLayout unfav;
    String userId;
    FirebaseAuth mAuth;
    BottomSheetDialog bottomSheetDialog;
    private ArrayList<ModelGroupChat> groupChats;
    private AdapterSaveChat adapterChatsave;
    List<String> mySaves;
    SharedPref sharedPref;
    ImageView imageView21;
    String myUid;
    private DatabaseReference mDatabase;
    BottomSheetDialog chatbottomsheet;
    String mchatid,msgtimestamp,pId,msg,type;
    ConstraintLayout star,copy,delete,forward;
    DatabaseReference databaseReference,likeRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lineup_favourite);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        myUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

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
//                    Intent intent = new Intent(ChatSaveActivity.this, UserProfile.class);
//                    startActivity(intent);
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatSaveActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        binding.pg.setVisibility(View.VISIBLE);
        binding.imageView3.setOnClickListener(v -> onBackPressed());
        binding.savedrv.setHasFixedSize(true);
        binding.savedrv.setLayoutManager(new LinearLayoutManager(this));
        groupChats = new ArrayList<>();
//        adapterGroupChat = new AdapterGroupChat(this, groupChats);
//        adapterGroupChat.notifyDataSetChanged();
//        binding.savedrv.setAdapter(adapterGroupChat);
        mySaved();
        createBottomSheetDialog();

        binding.more.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });
//        binding.imageView3.setOnClickListener(v -> {
//            Intent intent = new Intent(ChatSaveActivity.this, ChatViewPagerActivity.class);
//            startActivity(intent);
//
//        });
        setCommentno();
    }

//    private void loadActionbar(String mchatid,String msgtimestamp,String pId,String msg,String type) {
//        binding.constraintLayout1.setVisibility(View.VISIBLE);
////        binding.delete.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//////                String msgtimestamp = modelChats.get(position).getTimestamp();
////                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
////                Query query = dbRef.orderByChild("timestamp").equalTo(msgtimestamp);
////                query.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        for (DataSnapshot ds: dataSnapshot.getChildren()){
////                            if (ds.child("sender").getValue().equals(myUID)){
////                                ds.getRef().removeValue();
////                                Toast.makeText(context,"message deleted...",Toast.LENGTH_SHORT).show();
////                                binding.constraintLayout1.setVisibility(View.GONE);
////                                binding.header.setVisibility(View.VISIBLE);
////                            }else{
////                                Toast.makeText(context,"You can delete only your message... ",Toast.LENGTH_SHORT).show();
////                                binding.constraintLayout1.setVisibility(View.GONE);
////                                binding.header.setVisibility(View.VISIBLE);
////                            }
////                        }
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                    }
////                });
////            }
////        });
//        binding.copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
////                String msgtimestamp = modelChats.get(position).getTimestamp();
//                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
//                Query query = dbRef.orderByChild("timestamp").equalTo(msgtimestamp);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                            String dmsg = Objects.requireNonNull(ds.child("msg").getValue()).toString();
//                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                            ClipData clip = ClipData.newPlainText("text", dmsg);
//                            clipboard.setPrimaryClip(clip);
//                            Toast.makeText(context,"message copied...",Toast.LENGTH_SHORT).show();
//                            binding.constraintLayout1.setVisibility(View.GONE);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });
//
//            }
//        });
//        likeRef = FirebaseDatabase.getInstance().getReference().child("Favourite");
//        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(pId).hasChild(userId)){
//                    binding.imageView21.setImageResource(R.drawable.star);
//                }else {
//                    binding.imageView21.setImageResource(R.drawable.starregular);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        binding.forward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ShareActivity.class);
//                intent.putExtra("postId", msg);
//                intent.putExtra("type", type);
//                context.startActivity(intent);
//                binding.constraintLayout1.setVisibility(View.GONE);
//            }
//        });
//        binding.star.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                likeRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child(userId);
//                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child(pId).hasChild(userId)){
//                            likeRef.child(pId).child(userId).removeValue();
//                            binding.imageView21.setImageResource(R.drawable.starregular);
//                            binding.constraintLayout1.setVisibility(View.GONE);
//                            adapterGroupChat.notifyDataSetChanged();
//                        }else {
//                            likeRef.child(pId).child(userId).setValue("star");
//                            binding.imageView21.setImageResource(R.drawable.star);
//                            binding.constraintLayout1.setVisibility(View.GONE);
//                            adapterGroupChat.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//    }

    private void setCommentno() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourite").child(userId);
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

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.unfav_bottom_sheet, null);
            unfav = view.findViewById(R.id.unfav);

            unfav.setOnClickListener(this);

            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
        if ( chatbottomsheet == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.activity_chat_bottom_sheet2, null);
            star = view.findViewById(R.id.star);
            imageView21 = view.findViewById(R.id.imageView21);
            copy = view.findViewById(R.id.copy);
            forward = view.findViewById(R.id.forward);
            star.setOnClickListener(this);
            copy.setOnClickListener(this);
            forward.setOnClickListener(this);
            chatbottomsheet = new BottomSheetDialog(ChatSaveActivity.this);
            chatbottomsheet.setContentView(view);
        }
    }

    private void mySaved() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Favourite").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mySaves.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    mySaves.add(snapshot1.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChats.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelGroupChat post = snapshot1.getValue(ModelGroupChat.class);
                    for (String id: mySaves){
                        if (Objects.requireNonNull(post).getTimestamp().equals(id)){
                            groupChats.add(post);
                        }
                    }
                }
                adapterChatsave = new AdapterSaveChat(ChatSaveActivity.this, groupChats);
                adapterChatsave.notifyDataSetChanged();
                binding.savedrv.setAdapter(adapterChatsave);
                binding.pg.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.unfav:
                bottomSheetDialog.cancel();
                unfav();
                break;
            case R.id.star:
                chatbottomsheet.cancel();
                unstar();

                break;
            case R.id.delete:
                chatbottomsheet.cancel();
//                String myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
//                Query query = dbRef.orderByChild("timestamp").equalTo(msgtimestamp);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                            if (ds.child("sender").getValue().equals(myUID)) {
//                                ds.getRef().removeValue();
//                                Toast.makeText(context, "message deleted...", Toast.LENGTH_SHORT).show();
//                            }
//                            if (ds.child("sender").getValue().equals(hisUid)) {
//                                Map<String, Object> hashMap = new HashMap<>();
//                                hashMap.put("hide", "true");
//                                dbRef.child(pId).updateChildren(hashMap);
//                                Toast.makeText(context, "message deleted for me... ", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                break;
            case R.id.copy:
                chatbottomsheet.cancel();
                String myUID2 = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Chats");
                Query query2 = dbRef2.orderByChild("timestamp").equalTo(msgtimestamp);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String dmsg = Objects.requireNonNull(ds.child("msg").getValue()).toString();
                            ClipboardManager clipboard = (ClipboardManager) ChatSaveActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("text", dmsg);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(ChatSaveActivity.this, "message copied...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                break;
            case R.id.forward:
                chatbottomsheet.cancel();
                Intent forwardintent = new Intent(ChatSaveActivity.this, ShareActivity.class);
                forwardintent.putExtra("postId", msg);
                forwardintent.putExtra("type", type);
                ChatSaveActivity.this.startActivity(forwardintent);
                break;
        }
    }

    private void unstar() {
        likeRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child(userId);
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(msgtimestamp).hasChild(myUid)) {
                    likeRef.child(msgtimestamp).child(myUid).removeValue();
                    imageView21.setImageResource(R.drawable.starregular);
                } else {
                    likeRef.child(msgtimestamp).child(myUid).setValue("star");
                    imageView21.setImageResource(R.drawable.star);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void unfav() {
        Query query = FirebaseDatabase.getInstance().getReference("Favourite").child(userId);
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
}