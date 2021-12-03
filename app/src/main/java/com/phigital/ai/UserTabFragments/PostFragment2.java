package com.phigital.ai.UserTabFragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.phigital.ai.Adapter.AdapterProfileFragmentImages;
import com.phigital.ai.Model.ModelPost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.databinding.FragmentPost2Binding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PostFragment2 extends Fragment {

    FragmentPost2Binding binding;
//    RecyclerView recyclerView;
    int num = 3;
    List<ModelPost> postList;
    AdapterProfileFragmentImages adapterPost;
    String userId,hisUid;

    public PostFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPost2Binding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,new IntentFilter(
                "hisUid"
        ));
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        postList= new ArrayList<>();

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context2, Intent intent) {
            hisUid = intent.getStringExtra("item");

            Activity activity = getActivity();

            if(activity != null){
                loadPost(num);
            }

        }
    };

    public void loadPost(int num) {
        int nu =Integer.parseInt(String.valueOf(num));
        binding.postView.setLayoutManager(new GridLayoutManager(getActivity(),nu));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(Objects.requireNonNull(modelPost).getType().equals("meme") || modelPost.getType().equals("image")) {
                        if (modelPost.getReId().equals("")) {
                            postList.add(modelPost);
                        }
                    }
                    Collections.reverse(postList);
                    adapterPost = new AdapterProfileFragmentImages(getActivity(),postList);
                    binding.postView.setAdapter(adapterPost);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}