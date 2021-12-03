package com.phigital.ai.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Adapter.AdapterSearchImages;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.databinding.FragmentSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    List<ModelPost> sponsorshipList = new ArrayList<>();
    AdapterSearchImages adapterPost;
    List<ModelPost> postList;

    private static final int TOTAL_ITEMS_TO_LOAD = 100;
    private int mCurrenPage = 1;
    List<ModelPost> result = new ArrayList<>();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.pg.setVisibility(View.VISIBLE);
        postList= new ArrayList<>();

        loadads();
        binding.searchBar.setShowSoftInputOnFocus(false);
        binding.searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        binding.cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                mCurrenPage++;
                loadads();
            }
        });
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
//        staggeredGridLayoutManager.setReverseLayout(true);
        binding.searchView.setLayoutManager(staggeredGridLayoutManager);
    }

    private void loadads() {
        DatabaseReference sponsorship = FirebaseDatabase.getInstance().getReference("Sponsorship");
        sponsorship.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                sponsorshipList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPost modelspon = ds.getValue(ModelPost.class);
                    if (Objects.requireNonNull(modelspon).getPrivacy().equals("public")){
                        sponsorshipList.add(modelspon);
                    }
                    if (sponsorshipList != null){
                        loadPosts(sponsorshipList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadPosts(List<ModelPost> sponsorshipList) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("type").equalTo("image")
                .limitToLast(mCurrenPage*TOTAL_ITEMS_TO_LOAD);
//        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                result.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    assert modelPost != null;
                    if( modelPost.getReId().equals("")) {
                        postList.add(modelPost);
                    }
                }
                if (postList.size() != 0) {
                    merge(postList, sponsorshipList, 9, 1);
                }
                Collections.reverse(result);
                adapterPost = new AdapterSearchImages(getActivity(), result);
                binding.searchView.setAdapter(adapterPost);
                binding.pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();

                if (adapterPost.getItemCount() == 0){
                    binding.pg.setVisibility(View.GONE);
                    binding.searchView.setVisibility(View.GONE);
                }else {
                    binding.pg.setVisibility(View.GONE);
                    binding.searchView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public void loadPost() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
////        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                postList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelPost modelPost = ds.getValue(ModelPost.class);
//                    if(Objects.requireNonNull(modelPost).getType().equals("image") && modelPost.getReId().equals("")) {
//                        postList.add(modelPost);
//                    }
//                    adapterPost = new AdapterSearchImages(getActivity(), postList);
////                    Collections.reverse(postList);
//                    binding.searchView.setAdapter(adapterPost);
//                    binding.pg.setVisibility(View.GONE);
//                    adapterPost.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void merge(List<ModelPost> l1, List<ModelPost> l2, int r1, int r2) {
        int index1 = 0;
        int index2 = 0;

        while (index1 < l1.size() || index2 < l2.size()) {
            for (int i = 0; i < r1; ++i)
                result.add(l1.get((index1 + i) % l1.size()));
            index1 += r1;
            if (index2 < l2.size()) {
                for (int i = 0; i < r2; ++i)
                    result.add(l2.get((index2 + i) % l2.size()));
                index2 += r2;
            }

        }

    }
}
