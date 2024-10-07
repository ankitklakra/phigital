package com.phigital.ai.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterChatList;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.databinding.FragmentOnlineChatBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OnlineFragment extends Fragment {

    List<ModelUser> userList;
    AdapterChatList adapterChatList;
    List<String> followingList;
    FragmentOnlineChatBinding binding;
    String muserId;
    public OnlineFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOnlineChatBinding.inflate(getLayoutInflater());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        muserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        followingList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Follow").child(muserId).child("Following").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        followingList.clear();
                        followingList.add(muserId);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            followingList.add(snapshot.getKey());
                        }
                        loadChats();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return binding.getRoot();
    }

    private void loadChats() {
        userList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    if (user != null && user.getId() != null && user.getStatus() != null){
                        for (String id : followingList) {
                            if (!muserId.equals(id) && user.getId().equals(id) && user.getStatus().equals("online")) {
                                userList.add(user);
                            }
                        }
                    }
                }

                adapterChatList = new AdapterChatList(getActivity(), userList);
                binding.recyclerView2.setAdapter(adapterChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
