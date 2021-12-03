package com.phigital.ai.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterChatListGroups;
import com.phigital.ai.Adapter.AdapterGroups;
import com.phigital.ai.Model.ModelChatListGroups;
import com.phigital.ai.Model.ModelGroups;
import com.phigital.ai.databinding.FragmentLineupGroupBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupFragment extends Fragment {

    FragmentLineupGroupBinding binding;
    //Groups
    AdapterGroups adapterGroups;
    List<ModelGroups> modelGroupsList;
    //Groups
    AdapterChatListGroups adapterChatListGroups;
    List<ModelChatListGroups> modelChatListGroupsList;
    private String userId;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLineupGroupBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        modelChatListGroupsList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.grouprecyclerView.setLayoutManager(layoutManager);
        getChatGroups();
    }

    private void getChatGroups() {
        FirebaseDatabase.getInstance().getReference("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelChatListGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelChatListGroups modelChatListGroups = ds.getValue(ModelChatListGroups.class);
                        modelChatListGroupsList.add(modelChatListGroups);
                    }
                    adapterChatListGroups = new AdapterChatListGroups(requireActivity(), modelChatListGroupsList);
                    binding.grouprecyclerView.setAdapter(adapterChatListGroups);
                    adapterChatListGroups.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void getMyGroups() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                modelGroupsList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                 if (ds.child("Participants").child(userId).exists()){
//                     ModelGroups modelGroups = ds.getValue(ModelGroups.class);
//                     modelGroupsList.add(modelGroups);
//                 }
//                    adapterGroups = new AdapterGroups(getActivity(), modelGroupsList);
////                    groups_rv.setAdapter(adapterGroups);
////                    pg.setVisibility(View.GONE);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}