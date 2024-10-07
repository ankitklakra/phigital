package com.phigital.ai.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterPoll;
import com.phigital.ai.Adapter.AdapterStory;
import com.phigital.ai.Chat.ChatViewPagerActivity;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelPoll;
import com.phigital.ai.Model.ModelStory;
import com.phigital.ai.News.StoreActivity;
import com.phigital.ai.Notifications.NotificationScreen;
import com.phigital.ai.R;
import com.phigital.ai.databinding.FragmentPollBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PollFragment extends Fragment {

    FragmentPollBinding binding;
    FirebaseAuth mAuth;
    AdapterPoll adapterPoll;
    List<ModelPoll> pollList= new ArrayList<>();
    private String userId;
    List<String> followingList;
    private AdapterStory story;
    private List<ModelStory> storyList = new ArrayList<>();
    List<String> followingSList;
    long initial;
    boolean vs = true;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrenPage = 1;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPollBinding.inflate(getLayoutInflater());
        loadPost();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.postView.setLayoutManager(layoutManager);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Poll");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    i++;
                }
                initial = i;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                if (vs) {
                    mCurrenPage++;
                    loadPost();
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        binding.pb.setVisibility(View.VISIBLE);
        checkSFollowing();
        binding.storyView.setHasFixedSize(true);
        binding.storyView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        story = new AdapterStory(getContext(), storyList);
        binding.storyView.setAdapter(story);

        binding.chat.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), ChatViewPagerActivity.class)));
       // binding.buy.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), StoreActivity.class)));
        binding.notification.setOnClickListener(v2 -> startActivity(new Intent(getActivity(), NotificationScreen.class)));

        //Notification
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Count").addValueEventListener(new ValueEventListener() {
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
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Countm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.chatimage.setImageResource(R.drawable.ic_data_usage);
                    binding.countmtv.setText(String.valueOf(snapshot.getChildrenCount()));
                    binding.countmtv.setVisibility(View.VISIBLE);
                }else {
                    binding.countmtv.setVisibility(View.GONE);
                    binding.chatimage.setImageResource(R.drawable.ic_offline_bolt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkSFollowing(){
        followingSList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingSList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingSList.add(snapshot.getKey());
                }
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new ModelStory("",0,0,"", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
                for (String id : followingSList){
                    int countStory = 0;
                    ModelStory modelStory = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                        modelStory = snapshot1.getValue(ModelStory.class);
                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(modelStory);
                    }
                }
                story.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                followingList.add(userId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                loadPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Poll");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pollList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPoll modelPoll = ds.getValue(ModelPoll.class);
                    pollList.add(modelPoll);
                }
                adapterPoll = new AdapterPoll(getActivity(), pollList);
                binding.postView.setAdapter(adapterPoll);
                adapterPoll.notifyDataSetChanged();
                binding.pb.setVisibility(View.INVISIBLE);
                if (adapterPoll.getItemCount() == 0){
                    binding.postView.setVisibility(View.GONE);
                }else {
                    binding.postView.setVisibility(View.VISIBLE);
                    if(adapterPoll.getItemCount() == initial){
                        vs = false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    //Pressed return button - returns to the results menu
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });
    }
}
