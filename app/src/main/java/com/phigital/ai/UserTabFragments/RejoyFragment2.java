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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Model.ModelPost;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.databinding.FragmentRejoy2Binding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RejoyFragment2 extends Fragment {

    FragmentRejoy2Binding binding;
    List<ModelPost> rejoyList;
    AdapterPost adapterPost3;
    String hisUid;

    public RejoyFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRejoy2Binding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,new IntentFilter(
                "hisUid"
        ));
        rejoyList= new ArrayList<>();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context2, Intent intent) {
            hisUid = intent.getStringExtra("item");
            Activity activity = getActivity();
            if(activity != null){
                loadPost3();
            }
        }
    };

    private void loadPost3() {
        binding.rejoyView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rejoyList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (Objects.requireNonNull(modelPost).getId().equals(hisUid)&&!modelPost.getReId().isEmpty()){
                        rejoyList.add(modelPost);
                    }
                    adapterPost3 = new AdapterPost(getActivity(), rejoyList);
                    binding.rejoyView.setAdapter(adapterPost3);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}