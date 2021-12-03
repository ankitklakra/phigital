package com.phigital.ai.TabFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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


public class ShootFragment extends Fragment {

    RecyclerView recyclerView;
    List<ModelPost> shootList;
    AdapterPost adapterPost3;
    String userId;

    public ShootFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shoot, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//        loadPost(view);
        shootList= new ArrayList<>();
        return view;
    }
    private void loadPost(View view) {
        recyclerView = view.findViewById(R.id.shootView);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shootList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost2 = ds.getValue(ModelPost.class);
                    assert modelPost2 != null;
                    if((modelPost2).getType().contains("Video")) {
                        shootList.add(modelPost2);
                    }
                    adapterPost3 = new AdapterPost(getActivity(), shootList);
                    recyclerView.setAdapter(adapterPost3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}