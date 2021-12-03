package com.phigital.ai.TabFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Model.ModelPost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FeelFragment extends Fragment {

    RecyclerView recyclerView2;
    List<ModelPost> feelList;
    AdapterPost adapterPost2;
    String userId;

    public FeelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feel, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        loadPost(view);
        feelList= new ArrayList<>();
        return view;
    }
    private void loadPost(View view) {
        recyclerView2 = view.findViewById(R.id.feelView);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feelList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (modelPost.getType().equals("text")&&modelPost.getReId().equals("")){
                        feelList.add(modelPost);
                    }
                    adapterPost2 = new AdapterPost(getActivity(), feelList);
                    recyclerView2.setAdapter(adapterPost2);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}