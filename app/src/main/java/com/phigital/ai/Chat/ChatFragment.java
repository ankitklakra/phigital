package com.phigital.ai.Chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterChatList;
import com.phigital.ai.Model.ModelChat;
import com.phigital.ai.Model.ModelChatList;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.databinding.FragmentLineupChatBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    List<ModelChatList> chatlistList;
    List<ModelUser> userList;
    AdapterChatList adapterChatList;

    FragmentLineupChatBinding binding;
    String muserId;
    public ChatFragment() {

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLineupChatBinding.inflate(getLayoutInflater());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        muserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        chatlistList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Chatlist").child(muserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChatList chatlist = ds.getValue(ModelChatList.class);
                    chatlistList.add(chatlist);
                }
                if(snapshot.exists()){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    loadChats();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    private void loadChats() {
        userList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    if (user != null){
                        for (ModelChatList chatlist: chatlistList){
                            if (user != null && user.getId() != null && user.getId().equals(chatlist.getId())){
                                userList.add(user);
                                break;
                            }
                        }
                    }

                    Activity activity = getActivity();
                    if(activity != null && isAdded()){
                        adapterChatList = new AdapterChatList(requireActivity(), userList);
                        binding.recyclerView.setAdapter(adapterChatList);
                    }
//                    for (int i=0; i<userList.size(); i++){
//                        lastMessage(userList.get(i).getId());
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void lastMessage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender == null || receiver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(muserId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(muserId)){
                        switch (chat.getType()) {
                            case "image":
                                theLastMessage = "Sent a photo";
                                break;
                            case "video":
                                theLastMessage = "Sent a video";
                                break;
                            case "post":
                                theLastMessage = "Sent a post";
                                break;
                            default:
                                theLastMessage = chat.getMsg();
                                break;
                        }
                    }
                }
                adapterChatList.notifyDataSetChanged();
//                adapterChatList.setLastMessageMap(userId, theLastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
