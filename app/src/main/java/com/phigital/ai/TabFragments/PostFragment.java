package com.phigital.ai.TabFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.Adapter.AdapterProfileFragmentImages;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PostFragment extends Fragment {

    RecyclerView recyclerView;
    int num = 3;
    List<ModelPost> postList;
    AdapterProfileFragmentImages adapterPost;
    String userId,hisUid;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Bundle bundle = new Bundle();
        hisUid = bundle.getString("hisUid");
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        loadPost(view,String.valueOf(num));
        postList= new ArrayList<>();
        return view;
    }
    public void loadPost(View view , String num) {
        recyclerView = view.findViewById(R.id.postView);
        int nu =Integer.parseInt(num);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),nu));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(Objects.requireNonNull(modelPost).getType().equals("image")){
                        if (modelPost.getReId().equals("")) {
                            postList.add(modelPost);
                        }
                    }
                    Collections.reverse(postList);
                    adapterPost = new AdapterProfileFragmentImages(getActivity(),postList);
                    recyclerView.setAdapter(adapterPost);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}